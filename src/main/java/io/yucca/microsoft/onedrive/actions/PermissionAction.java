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

import java.util.List;
import java.util.concurrent.Callable;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.resources.facets.PermissionFacet;

/**
 * Action to get an Item permission
 * 
 * @author yucca.io
 */
public class PermissionAction extends AbstractAction
    implements Callable<List<PermissionFacet>> {

    private static final Logger LOG = LoggerFactory
        .getLogger(PermissionAction.class);

    public static final String ACTION = "permissions";

    private final ItemAddress itemAddress;

    private final String[] select;

    private final String eTag;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item for which to get permissions
     */
    public PermissionAction(OneDriveAPIConnection api,
                            ItemAddress itemAddress) {
        super(api);
        this.itemAddress = itemAddress;
        this.select = null;
        this.eTag = null;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item for which to get permissions
     * @param eTag String an optional etag value of the cached item, if set and
     *            the tag matches the upstream item an NotModifiedException is
     *            thrown. If @{code null} than no etag validation is done
     */
    public PermissionAction(OneDriveAPIConnection api, ItemAddress itemAddress,
                            String eTag) {
        super(api);
        this.itemAddress = itemAddress;
        this.select = null;
        this.eTag = eTag;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item for which to get permissions
     * @param select String[] Optional properties of {@link PermissionFacet} be
     *            included for each item in the response.
     * @param eTag String an optional etag value of the cached item, if set and
     *            the tag matches the upstream item an NotModifiedException is
     *            thrown. If @{code null} than no etag validation is done
     */
    public PermissionAction(OneDriveAPIConnection api, ItemAddress itemAddress,
                            String[] select, String eTag) {
        super(api);
        this.itemAddress = itemAddress;
        this.select = select;
        this.eTag = eTag;
    }

    /**
     * Get Item permissions
     * 
     * @throws OneDriveException
     */
    @Override
    public List<PermissionFacet> call() throws OneDriveException {
        return get();
    }

    private List<PermissionFacet> get() {
        LOG.info("Getting permissions for item: {}", itemAddress);
        EntityTag tag = createEtag(eTag);
        WebTarget target = api.webTarget()
            .path(itemAddress.getPathWithAddress(ACTION))
            .resolveTemplateFromEncoded(ITEM_ADDRESS, itemAddress.getAddress());
        if (select != null) {
            QueryParameters parameters = QueryParameters.Builder
                .newQueryParameters().select(select).build();
            target = parameters
                .configure(target, new String[] { QueryParameters.SELECT });
        }
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
            .header(HEADER_IF_NONE_MATCH, tag).get();
        handleError(response, Status.OK,
                    "Failure getting permissions for item: " + itemAddress);
        return response.readEntity(new GenericType<List<PermissionFacet>>() {
        });
    }

}
