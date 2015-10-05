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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.OneDriveFile;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.facets.FileFacet;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * Action to upload an Item as multipart. Only files below 100MB can be uploaded
 * by this action, larger files should be uploaded with UploadResumableAction
 * 
 * @author yucca.io
 */
public class UploadMultipartAction extends AbstractAction
    implements Callable<Item> {

    public static final String ACTION = "children";

    public static final MediaType MULTIPART_RELATED_TYPE = new MediaType("multipart",
                                                                         "related");

    private final OneDriveFile content;

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
    public UploadMultipartAction(OneDriveAPIConnection api,
                                 OneDriveFile content,
                                 ItemAddress parentAddress,
                                 ConflictBehavior behavior) {
        super(api);
        this.content = content;
        this.parentAddress = parentAddress;
        this.behavior = behavior;
    }

    /**
     * Upload an Item as multipart
     * 
     * @return OneDriveContent
     */
    @Override
    public Item call() throws OneDriveException {
        return upload();
    }

    /**
     * Upload an Item as multipart
     * 
     * @return Item uploaded content
     */
    private Item upload() {
        String address = parentAddress.getAddress();
        // TODO add check for file above 100MB
        MultiPart multipart = createMultipart(content, behavior);
        Status[] successCodes = { Status.CREATED };
        Response response = api.webTarget()
            .path(parentAddress.getPathWithAddress(ACTION))
            .resolveTemplateFromEncoded(ItemAddress.ITEM_ADDRESS, address)
            .request().post(Entity.entity(multipart, MULTIPART_RELATED_TYPE));
        handleError(response, successCodes,
                    "Failure uploading file: " + content.getName()
                                            + " as multipart into folder: "
                                            + address);
        return response.readEntity(Item.class);
    }

    /**
     * Create a MultiPart from an OneDriveFile
     * 
     * @param content OneDriveFile
     * @param behavior behaviour if a naming conflict occurs
     * @return MultiPart
     */
    public MultiPart createMultipart(OneDriveFile content,
                                     ConflictBehavior behavior) {
        MultiPart multipart = new MultiPart();
        BodyPart metadataPart = new BodyPart(api
            .mapToJson(newMetadataMultiPartBody(content.getName(), behavior)),
                                             MediaType.APPLICATION_JSON_TYPE);
        metadataPart.getHeaders().putSingle("Content-ID", "<metadata>");
        multipart.bodyPart(metadataPart);

        FileDataBodyPart contentPart = new FileDataBodyPart("file", content
            .getFile().toFile());
        contentPart.getHeaders().putSingle("Content-ID", "<content>");
        multipart.bodyPart(contentPart);
        return multipart;
    }

    /**
     * Create a metadata body for multipart upload
     * 
     * @param name String
     * @param behaviour ConflictBehavior
     * @return Map<String, Object>
     */
    Map<String, Object> newMetadataMultiPartBody(String name,
                                                 ConflictBehavior behaviour) {
        String conflictBehavior = (behaviour == null)
            ? ConflictBehavior.FAIL.getName() : behaviour.getName();
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("file", new FileFacet());
        map.put("@content.sourceUrl", "cid:content");
        map.put(QueryParameters.CONFLICT_BEHAVIOR, conflictBehavior);
        return map;
    }
}
