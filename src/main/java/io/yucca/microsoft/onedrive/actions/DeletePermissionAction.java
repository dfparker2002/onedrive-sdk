/**
 * Copyright 2016 Rob Sessink
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

import java.util.concurrent.Callable;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.resources.PermissionFacet;

/**
 * Action to delete a permission on an Item
 * 
 * @author yucca.io
 */
public class DeletePermissionAction extends AbstractAction
    implements Callable<Void> {

    private static final Logger LOG = LoggerFactory
        .getLogger(DeletePermissionAction.class);

    public static final String ACTION = "permissions";

    private final ItemAddress itemAddress;

    private final PermissionFacet permission;

    private final String eTag;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item for which to delete permissions
     * @param permission PermissionFacet to delete
     */
    public DeletePermissionAction(OneDriveAPIConnection api,
                                  ItemAddress itemAddress,
                                  PermissionFacet permission) {
        super(api);
        this.itemAddress = itemAddress;
        this.permission = permission;
        this.eTag = null;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item for which to delete permissions
     * @param permission PermissionFacet to delete
     * @param eTag String an optional etag value of the cached item, if set this
     *            must match the etag value of remote item for deletion to
     *            succeed. If @{code null} than no etag validation is done
     */
    public DeletePermissionAction(OneDriveAPIConnection api,
                                  ItemAddress itemAddress,
                                  PermissionFacet permission, String eTag) {
        super(api);
        this.itemAddress = itemAddress;
        this.permission = permission;
        this.eTag = eTag;
    }

    /**
     * Delete Permission
     * 
     * @throws OneDriveException
     */
    @Override
    public Void call() throws OneDriveException {
        return delete();
    }

    private Void delete() {
        LOG.info("Deleting permission: {} on item: {}", permission.getId(),
                 itemAddress);
        EntityTag tag = createEtag(eTag);
        Response response = api.webTarget()
            .path(itemAddress.getPathWithAddress(ACTION, "{permission-id}"))
            .resolveTemplateFromEncoded(ITEM_ADDRESS, itemAddress.getAddress())
            .resolveTemplateFromEncoded(PERMISSION_ID, permission.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .header(HEADER_IF_MATCH, tag).delete();
        handleError(response, Status.NO_CONTENT,
                    "Failure deleting permission: " + permission.getId()
                                                 + " on item: " + itemAddress);
        return null;
    }

}
