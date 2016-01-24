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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.resources.facets.PermissionFacet;

/**
 * Action to update an permission on an Item.
 * 
 * @author yucca.io
 */
public class UpdatePermissionAction extends AbstractAction
    implements Callable<PermissionFacet> {

    public static final String ACTION = "permissions";

    private static final Logger LOG = LoggerFactory
        .getLogger(UpdatePermissionAction.class);

    private final ItemAddress itemAddress;

    private final PermissionFacet permission;

    private final String eTag;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress to update
     * @param permission PermissionFacet permission to update with the new
     *            role(s) set
     */
    public UpdatePermissionAction(OneDriveAPIConnection api,
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
     * @param itemAddress ItemAddress
     * @param permission PermissionFacet permission to update with the new
     *            role(s) set
     * @param eTag String an optional etag value of the cached item, if set this
     *            must match the etag value of remote item for updating to
     *            succeed. If @{code null} than no etag validation is done
     */
    public UpdatePermissionAction(OneDriveAPIConnection api,
                                  ItemAddress itemAddress,
                                  PermissionFacet permission, String eTag) {
        super(api);
        this.itemAddress = itemAddress;
        this.permission = permission;
        this.eTag = eTag;
    }

    /**
     * Update an permission of an Item
     * 
     * @return PermissionFacet updated permission
     */
    @Override
    public PermissionFacet call() throws OneDriveException {
        return update();
    }

    /**
     * Update an permission of an Item
     * 
     * @return PermissionFacet updated permission
     */
    private PermissionFacet update() {
        LOG.info("Updating permission: {} on item: {}", itemAddress);
        Response response = api.webTarget()
            .path(itemAddress.getPathWithAddress(ACTION, "{permission-id}"))
            .resolveTemplateFromEncoded(ITEM_ADDRESS, itemAddress.getAddress())
            .resolveTemplateFromEncoded(PERMISSION_ID, permission.getId())
            .request().header(HEADER_IF_MATCH, createEtag(eTag))
            .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
            .method(METHOD_PATCH, Entity.json(newRolesBody(permission)));
        handleError(response, Status.OK,
                    "Failure updating permissions: " + permission + " on item: "
                                         + itemAddress);
        return response.readEntity(PermissionFacet.class);
    }

    /**
     * Create roles POST body
     * 
     * @param permission PermissionFacet
     * @return Map<String, Object>
     */
    protected Map<String, Object> newRolesBody(PermissionFacet permission) {
        Map<String, Object> map = new HashMap<>();
        map.put("roles", permission.getRoles());
        return map;
    }

}
