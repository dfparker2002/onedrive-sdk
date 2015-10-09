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

import java.util.concurrent.Callable;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.resources.LinkType;
import io.yucca.microsoft.onedrive.resources.facets.PermissionFacet;

/**
 * Action to create a link
 * 
 * @author yucca.io
 */
public class CreateLink extends AbstractAction
    implements Callable<PermissionFacet> {

    private static final Logger LOG = LoggerFactory.getLogger(CreateLink.class);

    public static final String ACTION = "action.createLink";

    private final ItemAddress itemAddress;

    private final LinkType type;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item for which a link is created
     * @param type LinkType view for read-only, edit for read-write links, if
     *            {@code null} defaults to {@link LinkType#VIEW}
     */
    public CreateLink(OneDriveAPIConnection api, ItemAddress itemAddress,
                      LinkType type) {
        super(api);
        this.itemAddress = itemAddress;
        this.type = type;
    }

    /**
     * Create a link to an Item
     * 
     * @return PermissionFacet facet with the link information
     */
    @Override
    public PermissionFacet call() throws OneDriveException {
        return createLink();
    }

    /**
     * Create a link to an Item
     * 
     * @return PermissionFacet facet with the link information
     */
    private PermissionFacet createLink() {
        LinkType linkType = (type == null) ? LinkType.VIEW : type;
        LOG.info("Creating link to item: {} of type: {}", itemAddress,
                 linkType.name());
        Response response = api.webTarget()
            .path(itemAddress.getPathWithAddress(ACTION))
            .resolveTemplateFromEncoded(ITEM_ADDRESS,
                                        itemAddress.getAddress())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.json(linkType));
        Status[] successCodes = { Status.CREATED, Status.OK };
        handleError(response, successCodes,
                    "Failure creating sharing link for item: " + itemAddress);
        return response.readEntity(PermissionFacet.class);
    }

}
