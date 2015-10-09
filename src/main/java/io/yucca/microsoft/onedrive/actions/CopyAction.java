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
import java.util.Map;
import java.util.concurrent.Callable;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.addressing.PathAddress;

/**
 * Action to copy an Item to a folder
 * 
 * @author yucca.io
 */
public class CopyAction extends AbstractAction implements Callable<URI> {

    private static final Logger LOG = LoggerFactory.getLogger(CopyAction.class);

    public static final String ACTION = "action.copy";

    private final ItemAddress itemAddress;

    private final String name;

    private final ItemAddress parentAddress;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item to be copied
     * @param name String optional new name of copied item, if left empty the
     *            original name is used
     * @param parentAddress ItemAddress of parent folder to which the item is
     *            copied
     */
    public CopyAction(OneDriveAPIConnection api, ItemAddress itemAddress,
                      String name, ItemAddress parentAddress) {
        super(api);
        this.itemAddress = itemAddress;
        this.name = name;
        this.parentAddress = parentAddress;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item to be copied
     * @param name String optional new name of copied item, if left empty the
     *            original name is used
     * @param parentPath String relative path of the parent folder to which the
     *            item is copied
     */
    public CopyAction(OneDriveAPIConnection api, ItemAddress itemAddress,
                      String name, String parentPath) {
        super(api);
        this.itemAddress = itemAddress;
        this.name = name;
        this.parentAddress = new PathAddress(parentPath);
    }

    /**
     * Copy Item to a folder
     * 
     * @return URI location to get of asynchronous job status, used in polling
     */
    @Override
    public URI call() throws OneDriveException {
        return copy();
    }

    /**
     * Copy Item to a folder
     * 
     * @return URI location to get of asynchronous job status, used in polling
     */
    private URI copy() {
        LOG.info("Copying item: {} to folder: {}", itemAddress, parentAddress);
        Map<String, Object> map = newParentRefBody(name,
                                                   getItemReference(parentAddress));
        Response response = api.webTarget()
            .path(itemAddress.getPathWithAddress(ACTION))
            .resolveTemplateFromEncoded(ITEM_ADDRESS,
                                        itemAddress.getAddress())
            .request().header(HEADER_PREFER, RESPOND_ASYNC)
            .post(Entity.json(map));
        handleError(response,
                    Status.ACCEPTED, "Failed to copy item: " + itemAddress
                                     + " to: "
                                     + parentAddress.getPathWithAddress());
        return response.getLocation();
    }


}
