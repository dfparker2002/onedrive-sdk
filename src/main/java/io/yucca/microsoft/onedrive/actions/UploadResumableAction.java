/**
 * Copyright 2015 Rob Sessink
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.yucca.microsoft.onedrive.actions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.NotModifiedException;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.OneDriveFile;
import io.yucca.microsoft.onedrive.OneDriveResumableUploadException;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.io.FileFragmentStreamingOutput;
import io.yucca.microsoft.onedrive.io.Range;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.OneDriveError;
import io.yucca.microsoft.onedrive.resources.UploadSession;
import io.yucca.microsoft.onedrive.util.SimpleBackOffWaitStrategy;

/**
 * Action to upload item content larger than 100MB. Content is uploaded in
 * fragments and can be resumed after failures.
 * 
 * <pre>
 * TODO refactor:
 * 1. Split response handling to a seperate method.
 * 2. Perform uploading in background thread and make it cancelable.
 * 3. Eventually fail after waitStrategy reaches threshold.
 * </pre>
 * 
 * @author yucca.io
 */
public class UploadResumableAction extends AbstractAction
    implements Callable<Item> {

    private static final Logger LOG = LoggerFactory
        .getLogger(UploadResumableAction.class);

    public static final String ACTION = "upload.createSession";

    public static final long OPTIMAL_FRAGMENTSIZE_ALIGNMENT = 1024 * 320;

    public static final long FRAGMENTSIZE_100MB = 1024 * 1024 * 100;

    public static final long FRAGMENTSIZE_4MB = OPTIMAL_FRAGMENTSIZE_ALIGNMENT
                                                * 12;

    private final OneDriveFile content;

    private final ItemAddress parentAddress;

    private final ConflictBehavior behavior;

    private final long maxFragmentSize;

    private final int unknownFailureThreshold = 5;

    private UploadSession session;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param content OneDriveFile
     * @param parentAddress ItemAddress identifier or path of parent folder
     *            relative to the root folder i.e. "/drive/root:/".
     * @param behavior ConflictBehavior behaviour if a naming conflict occurs
     */
    public UploadResumableAction(OneDriveAPIConnection api,
                                 OneDriveFile content,
                                 ItemAddress parentAddress,
                                 ConflictBehavior behavior) {
        super(api);
        this.content = content;
        this.parentAddress = parentAddress;
        this.behavior = behavior;
        this.maxFragmentSize = FRAGMENTSIZE_4MB;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param content OneDriveContent to upload
     * @param parentAddress ItemAddress identifier or path of parent folder
     *            relative to the root folder i.e. "/drive/root:/".
     * @param behavior ConflictBehavior behaviour if a naming conflict occurs
     * @param maxFragmentSize long maximum size of a fragment
     */
    public UploadResumableAction(OneDriveAPIConnection api,
                                 OneDriveFile content,
                                 ItemAddress parentAddress,
                                 ConflictBehavior behavior,
                                 long maxFragmentSize) {
        super(api);
        this.content = content;
        this.parentAddress = parentAddress;
        this.behavior = behavior;
        this.maxFragmentSize = maxFragmentSize;
        assertOptimalFragmentSize(maxFragmentSize);

    }

    /**
     * Upload Item content
     * 
     * @return OneDriveContent
     * @throws NotModifiedException if an eTag was provided and matched, meaning
     *             the Item has not changed
     */
    @Override
    public Item call() throws OneDriveException {
        return upload();
    }

    /**
     * Upload an Item with resuming support
     * 
     * <pre>
     * 1. retry on 500, 502, 503, 504 with exponential backoff strategy: done
     * 2. for other errors use a retry counter with maximum: done
     * 3. on 404, restart upload entirely: done
     * 4. ranges should be rounded by 320K: done
     * 
     * TODO
     * Handle commit errors
     * </pre>
     * 
     * @return Item uploaded content
     */
    public Item upload() {
        LOG.info("Uploading file: {} using resumable method into folder: {}",
                 content.getName(), parentAddress);
        createSession();
        try {
            return uploadFragments();
        } catch (IOException e) {
            throw new OneDriveException("Failure uploading of file: "
                                        + content.getName()
                                        + ", file does not exist", e);
        } catch (OneDriveResumableUploadException e) {
            cancelSession();
            throw new OneDriveException("Failure uploading of file: "
                                        + content.getName()
                                        + ", session must be restarted", e);
        }
    }

    /**
     * Create an resumable upload session
     */
    public void createSession() {
        LOG.info("Creating session for uploading file: {} into folder: {}",
                 content.getName(), parentAddress);
        Map<String, Object> map = newCreateSessionBody(content, behavior);
        Response response = createUploadTarget(parentAddress, content).request()
            .post(Entity.json(toJson(map)));
        handleError(response, Status.OK,
                    "Failure creating session to upload item: "
                                         + content.getName() + " into folder: "
                                         + parentAddress);
        this.session = response.readEntity(UploadSession.class);
    }

    private WebTarget createUploadTarget(ItemAddress itemAddress,
                                         OneDriveFile content) {
        return api.webTarget()
            .path(parentAddress.getPathWithAddressAndFilename(ACTION))
            .resolveTemplateFromEncoded(ITEM_ADDRESS, itemAddress.getAddress())
            .resolveTemplateFromEncoded(FILENAME, content.getName());
    }

    /**
     * Create createSession POST body
     * 
     * @param content OneDriveContent
     * @param behavior ConflictBehavior
     * @return Map<String, Object>
     */
    private Map<String, Object> newCreateSessionBody(OneDriveContent content,
                                                     ConflictBehavior behavior) {
        Map<String, Object> item = new HashMap<>();
        item.put(QueryParameters.CONFLICT_BEHAVIOR,
                 (behavior == null
                     ? ConflictBehavior.REPLACE.getName()
                     : behavior.getName()));
        item.put("name", content.getName());
        Map<String, Object> map = new HashMap<>();
        map.put("item", item);
        return map;
    }

    /**
     * Upload OneDriveFile in multiple fragments
     * 
     * @return Item uploaded item
     * @throws OneDriveResumableUploadException if service indicated uploading
     *             failed or the failure threshold was reached
     * @throws IOException if file cannot be read
     */
    private Item uploadFragments()
        throws OneDriveResumableUploadException, IOException {

        int unknownFailureCount = 0;
        SimpleBackOffWaitStrategy waitStrategy = new SimpleBackOffWaitStrategy();
        Set<Range> ranges = Range.getRanges(maxFragmentSize,
                                            content.getLength());

        // loop over ranges until all fragments are uploaded
        while (!ranges.isEmpty()) {
            Range range = ranges.iterator().next();
            Response response = streamFragment(content, range, session);
            if (equalsStatus(response, Status.ACCEPTED)) {
                ranges.remove(range);
                waitStrategy.reset();
                unknownFailureCount = 0;
                LOG.info("Successfully uploaded file fragment {}, for: {}",
                         range.getContentRangeHeader(), content.getName());
            } else if (equalsStatus(response, Status.OK)
                       || equalsStatus(response, Status.CREATED)) {
                LOG.info("Successfully uploaded all file fragments for: {}",
                         content.getName());
                return response.readEntity(Item.class);
            } else if (equalsStatus(response,
                                    Status.REQUESTED_RANGE_NOT_SATISFIABLE)) {
                ranges.remove(range);
                LOG.info("Fragment: {} is already uploaded, skipping this fragment",
                         range.getContentRangeHeader());
                // could request UploadStatus and get expected range and upload
                // that, if this not exists fail completely
            } else if (equalsStatus(response, Status.CONFLICT)) {
                OneDriveError e = response.readEntity(OneDriveError.class);
                throw new OneDriveException(formatError(response.getStatus(),
                                                        "File: " + content
                                                            .getName() + " already exists, skipping upload"),
                                            e);
            } else if (equalsStatus(response, Status.NOT_FOUND)) {
                throw new OneDriveResumableUploadException(formatError(response
                    .getStatus(), "Upload failed for file: "
                                  + content.getName()));
            } else if (equalsStatus(response,
                                    new Status[] { Status.INTERNAL_SERVER_ERROR,
                                                   Status.BAD_GATEWAY,
                                                   Status.SERVICE_UNAVAILABLE,
                                                   Status.GATEWAY_TIMEOUT })) {
                LOG.debug("Failure uploading fragment: {} for file: {}, resuming after {} ms.",
                          new Object[] { range.getContentRangeHeader(),
                                         content.getName(),
                                         waitStrategy.getDuration() });
                unknownFailureCount++;
                waitStrategy.sleep();
            } else {
                unknownFailureCount++;
                waitStrategy.sleep();
                LOG.info("Unknown failure: {} while uploading fragment: {} for file: {}",
                         new Object[] { response.getStatus(),
                                        range.getContentRangeHeader(),
                                        content.getName() });
                if (unknownFailureCount > unknownFailureThreshold) {
                    throw new OneDriveResumableUploadException(formatError(response
                        .getStatus(), "Too many unknown failures while trying to upload file: "
                                      + content.getName()));
                }
            }
        }
        throw new OneDriveException("Failure uploading file: "
                                    + content.getName());
    }

    /**
     * Cancel the upload session
     */
    public void cancelSession() {
        LOG.info("Cancelling session for uploading file: {} into folder: {}",
                 content.getName(), parentAddress);
        Response response = api.getClient().target(session.getUploadUrl())
            .request().delete();
        handleError(response, Status.NO_CONTENT,
                    "Failure cancelling upload session:"
                                                 + session.getUploadUrl());
    }

    /**
     * See if content should be uploaded using the resumable method.
     * 
     * @param content OneDriveContent
     * @throws IOException
     * @throws OneDriveException if OneDriveContent is of type
     *             OneDriveInputStream and larger than 100MB or the size cannot
     *             be determined
     */
    public static boolean shouldUploadAsLargeContent(OneDriveContent content) {
        try {
            if (content.isLarger(FRAGMENTSIZE_100MB)) {
                if (content instanceof OneDriveFile) {
                    throw new OneDriveException("Resumable uploading of content larger than 100MB is only supported"
                                                + "for OneDriveFile instances");
                }
                return true;
            }
        } catch (IOException e) {
            throw new OneDriveException("Failure acquiring file size", e);
        }
        return false;
    }

    /**
     * Stream a fragment defined by Range
     * 
     * @param content OneDriveFile
     * @param range Range
     * @param session UploadSession
     * @return Response
     * @throws FileNotFoundException if file does not exist
     */
    private Response streamFragment(OneDriveFile content, Range range,
                                    UploadSession session)
                                        throws FileNotFoundException {
        FileFragmentStreamingOutput ffso = new FileFragmentStreamingOutput(content
            .getFile(), range);
        return api.getClient().target(session.getUploadUrl()).request()
            .header("Content-Length", range.getLength())
            .header("Content-Range", range.getContentRangeHeader())
            .put(Entity.json(ffso));
    }

    /**
     * Log a warning if optimal fragmentsize settings is not met as according to
     * https://dev.onedrive.com/items/upload_large_files.htm
     * 
     * @param long maxFragmentSize
     */
    private void assertOptimalFragmentSize(long maxFragmentSize) {
        if ((maxFragmentSize % OPTIMAL_FRAGMENTSIZE_ALIGNMENT) != 0) {
            LOG.warn("Fragment size {} is not a multiple of {}, uploading may fail on last segment",
                     maxFragmentSize, OPTIMAL_FRAGMENTSIZE_ALIGNMENT);
        }
    }

}
