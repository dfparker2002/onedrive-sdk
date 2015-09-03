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
package io.yucca.microsoft.onedrive;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.io.FileFragmentStreamingOutput;
import io.yucca.microsoft.onedrive.io.Range;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.OneDriveError;
import io.yucca.microsoft.onedrive.resources.UploadSession;
import io.yucca.microsoft.onedrive.util.ExponentionalBackOffWaitStrategy;

/**
 * OneDriveAPIResumableUpload, helper allowing for resumable uploading of
 * content. For every individual file upload, an new instance must be created.
 * 
 * <pre>
 * TODO refactor:
 * 1. Split response handling to a seperate method
 * 2. Perform uploading in background thread and make it cancelable
 * 3. Eventually fail after waitStrategy reaches threshold
 * </pre>
 * 
 * @author yucca.io
 */
public class OneDriveAPIResumableUpload {

    private static final Logger LOG = LoggerFactory
        .getLogger(OneDriveAPIResumableUpload.class);

    private static final long OPTIMAL_FRAGMENTSIZE_ALIGNMENT = 1024 * 320;

    public static final long FRAGMENTSIZE_100MB = 1024 * 1024 * 100;

    public static final long FRAGMENTSIZE_4MB = OPTIMAL_FRAGMENTSIZE_ALIGNMENT
                                                * 12;

    private final int unknownFailureThreshold = 5;

    private final long maxFragmentSize;

    private final OneDriveAPIConnection api;

    /**
     * Constructor, maxFragmentSize defaults to ~4MB
     * 
     * @param api OneDriveAPIConnection
     */
    public OneDriveAPIResumableUpload(OneDriveAPIConnection api) {
        this.api = api;
        this.maxFragmentSize = FRAGMENTSIZE_4MB;
        assertOptimalFragmentSize(maxFragmentSize);
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param maxFragmentSize long
     */
    public OneDriveAPIResumableUpload(OneDriveAPIConnection api,
                                      long maxFragmentSize) {
        this.api = api;
        this.maxFragmentSize = maxFragmentSize;
        assertOptimalFragmentSize(maxFragmentSize);
    }

    /**
     * Create an resumable upload session
     * 
     * @param content OneDriveContent content to upload
     * @param parentId String itemId of parent folder
     * @param behavior ConflictBehavior behaviour if a naming conflict occurs,
     *            default behavior is to replace
     * @return UploadSession available session used for uploading
     */
    public UploadSession createSessionById(OneDriveContent content,
                                           String parentId,
                                           ConflictBehavior behavior) {
        final Map<String, Object> map = newCreateSessionBody(content, behavior);
        Response response = api.getClient()
            .target(OneDriveAPIConnection.ONEDRIVE_URL)
            .path("/drive/items/{parent-id}:/{filename}:/upload.createSession")
            .resolveTemplateFromEncoded("parent-id", parentId)
            .resolveTemplateFromEncoded("filename", content.getName()).request()
            .post(Entity.json(api.mapToJson(map)));
        api.handleError(response, Status.OK,
                        "Failure creating session to upload item: "
                                             + content.getName()
                                             + " into folder: " + parentId);
        return response.readEntity(UploadSession.class);
    }

    /**
     * Create an resumable upload session
     * 
     * @param content OneDriveFile file to upload
     * @param path String path to the item, including the filename
     * @param behavior ConflictBehavior behaviour if a naming conflict occurs,
     *            default behavior is to replace
     * @return UploadSession available session used for uploading
     */
    public UploadSession createSessionByPath(OneDriveFile content, String path,
                                             ConflictBehavior behavior) {
        final Map<String, Object> map = newCreateSessionBody(content, behavior);
        Response response = api.getClient()
            .target(OneDriveAPIConnection.ONEDRIVE_URL)
            .path("/drive/root:/{path}:/upload.createSession")
            .resolveTemplateFromEncoded("path", path).request()
            .post(Entity.json(api.mapToJson(map)));
        api.handleError(response, Status.OK,
                        "Failure creating session to upload item: "
                                             + content.getName() + " to path: "
                                             + path);
        return response.readEntity(UploadSession.class);
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
        final Map<String, Object> item = new HashMap<>();
        item.put("@name.conflictBehavior",
                 (behavior == null
                     ? ConflictBehavior.REPLACE.getName()
                     : behavior.getName()));
        item.put("name", content.getName());
        final Map<String, Object> map = new HashMap<>();
        map.put("item", item);
        return map;
    }

    /**
     * Upload OneDriveFile in multiple fragments
     * 
     * @param file OneDriveFile
     * @param session UploadSession
     * @return Item uploaded item
     * @throws OneDriveResumableUploadException if service indicated uploading
     *             failed or the failure threshold was reached
     * @throws IOException if file cannot be read
     */
    public Item uploadFragments(OneDriveFile file, UploadSession session)
        throws OneDriveResumableUploadException, IOException {

        int unknownFailureCount = 0;
        final ExponentionalBackOffWaitStrategy waitStrategy = new ExponentionalBackOffWaitStrategy();
        final Set<Range> ranges = Range.getRanges(maxFragmentSize,
                                                  file.getLength());

        // loop over ranges until all fragments are uploaded
        while (ranges.size() > 0) {
            Range range = ranges.iterator().next();
            Response response = streamFragment(file, range, session);
            if (api.equalsStatus(response, Status.ACCEPTED)) {
                ranges.remove(range);
                waitStrategy.reset();
                unknownFailureCount = 0;
                LOG.debug("Successfully uploaded file fragment {}, for: {}",
                          range.getContentRangeHeader(), file.getName());
            } else if (api.equalsStatus(response, Status.OK)
                       || api.equalsStatus(response, Status.CREATED)) {
                LOG.debug("Successfully uploaded all file fragments for: {}",
                          file.getName());
                return response.readEntity(Item.class);
            } else
                if (api.equalsStatus(response,
                                     Status.REQUESTED_RANGE_NOT_SATISFIABLE)) {
                ranges.remove(range);
                LOG.debug("Fragment: {} is already uploaded, skipping this fragment",
                          range.getContentRangeHeader());
                // could request UploadStatus and get expected range and upload
                // that, if this not exists fail completely
            } else if (api.equalsStatus(response, Status.CONFLICT)) {
                OneDriveError e = response.readEntity(OneDriveError.class);
                throw new OneDriveException(api
                    .formatError(response.getStatus(),
                                 "File: " + file.getName()
                                                       + " already exists, skipping upload"),
                                            e);
            } else if (api.equalsStatus(response, Status.NOT_FOUND)) {
                throw new OneDriveResumableUploadException(api
                    .formatError(response.getStatus(),
                                 "Upload failed for file: " + file.getName()));
            } else
                if (api.equalsStatus(response,
                                     new Status[] { Status.INTERNAL_SERVER_ERROR,
                                                    Status.BAD_GATEWAY,
                                                    Status.SERVICE_UNAVAILABLE,
                                                    Status.GATEWAY_TIMEOUT })) {
                LOG.debug("Failure uploading fragment: {} for file: {}, resuming after {} ms.",
                          new Object[] { range.getContentRangeHeader(),
                                         file.getName(),
                                         waitStrategy.getDuration() });
                unknownFailureCount++;
                waitStrategy.sleep();
            } else {
                unknownFailureCount++;
                waitStrategy.sleep();
                LOG.debug("Unknown failure: {} while uploading fragment: {} for file: {}",
                          new Object[] { response.getStatus(),
                                         range.getContentRangeHeader(),
                                         file.getName() });
                if (unknownFailureCount > unknownFailureThreshold) {
                    throw new OneDriveResumableUploadException(api
                        .formatError(response.getStatus(),
                                     "Too many unknown failures while trying to upload file: "
                                                           + file.getName()));
                }
            }
        }
        throw new OneDriveException("Failure uploading file: "
                                    + file.getName());
    }

    /**
     * Cancel the upload session
     * 
     * @param session UploadSession
     */
    public void cancelSession(UploadSession session) {
        Response response = api.getClient().target(session.getUploadUrl())
            .request().delete();
        api.handleError(response, Status.NO_CONTENT,
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
    static boolean shouldUploadAsLargeContent(OneDriveContent content) {
        try {
            if (content.isLarger(FRAGMENTSIZE_100MB)) {
                if (content instanceof OneDriveFile) {
                    throw new OneDriveException("Resumable uploading of content larger than 100MB is only supported"
                                                + "for OneDriveFile instances");
                }
                return true;
            }
        } catch (IOException e) {
            throw new OneDriveException("Failure getting file size", e);
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
     * Logs a warning if optimal fragmentsize settings is not met as according
     * to https://dev.onedrive.com/items/upload_large_files.htm
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
