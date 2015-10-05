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

import java.net.URI;
import java.util.concurrent.Callable;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.NotModifiedException;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.PathUtil;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.OneDriveError;

/**
 * Action to upload an Item. Only files below 100MB can be uploaded by this
 * action, larger files should be uploaded with UploadResumableAction
 * 
 * @author yucca.io
 */
public class UploadAction extends AbstractAction implements Callable<Item> {

    public static final String ACTION = "content";

    private final OneDriveContent content;

    private final ItemAddress parentAddress;

    private final ConflictBehavior behavior;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param content OneDriveContent
     * @param parentAddress ItemAddress to parent folder relative to the root
     *            folder i.e. "/drive/root:/".
     * @param behavior ConflictBehavior behaviour if a naming conflict occurs
     */
    public UploadAction(OneDriveAPIConnection api, OneDriveContent content,
                        ItemAddress parentAddress, ConflictBehavior behavior) {
        super(api);
        this.content = content;
        this.parentAddress = parentAddress;
        this.behavior = behavior;
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
     * Upload an Item with content size below 100MB, larger files should be
     * uploaded with {@see UploadResumableAction}
     * 
     * <pre>
     * on uploading of a new file the statuscode 201 CREATED is returned 
     * on uploading of an existing file with ConflictBehavior.REPLACE
     *  the statuscode 200 OK is returned
     * </pre>
     * 
     * @return Item representing uploaded content
     */
    public Item upload() {
        String address = parentAddress.getAddress();
        String conflictBehavior = (behavior == null)
            ? null : behavior.getName();

        String path = parentAddress.getPathWithAddressAndFilename(ACTION);         
        Status[] successCodes = { Status.CREATED, Status.OK };
        Response response = api.webTarget()
            .path(path)
            .resolveTemplateFromEncoded(PathUtil.ITEM_ADDRESS, address)
            .resolveTemplateFromEncoded(PathUtil.FILENAME, content.getName())
            .queryParam(QueryParameters.CONFLICT_BEHAVIOR, conflictBehavior)
            .request(MediaType.TEXT_PLAIN)
            .put(Entity.entity(content, MediaType.APPLICATION_OCTET_STREAM));
        handleError(response, successCodes,
                    "Failure uploading file: " + content.getName() + " into: "
                                            + address);
        return response.readEntity(Item.class);
    }

    /**
     * XXX Upload by URI
     * 
     * @param api OneDriveAPIConnection
     * @param uri URI to an Item or as returned in the Location header
     * @return OneDriveContent
     */
    public static OneDriveContent byURI(OneDriveAPIConnection api, URI uri) {
        Response response = api.webTarget(uri)
            .request(MediaType.APPLICATION_OCTET_STREAM).get();
        if (response.getStatus() != Status.FOUND.getStatusCode()) {
            OneDriveError e = response.readEntity(OneDriveError.class);
            throw new OneDriveException("Failure downloading item: " + uri,
                                        response.getStatus(), e);
        }
        return response.readEntity(OneDriveContent.class);
    }
}
