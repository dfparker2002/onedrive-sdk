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

import java.util.Map;
import java.util.concurrent.Callable;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * Action to move an Item to a folder
 * 
 * @author yucca.io
 */
public class MoveAction extends AbstractAction implements Callable<Item> {

    private final Logger LOG = LoggerFactory.getLogger(MoveAction.class);

    private final ItemAddress itemAddress;

    private final String name;

    private final ItemAddress parentAddress;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item to be copied
     * @param name String optional new name of moved item, if left empty the
     *            original name is used
     * @param parentAddress ItemAddress of parent folder to which the item is
     *            moved
     */
    public MoveAction(OneDriveAPIConnection api, ItemAddress itemAddress,
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
     * @param itemAddress ItemAddress
     * @param name String name of copied reference, if left empty the original
     *            name is used
     * @param parentPath String reference to parent folder
     */
    public MoveAction(OneDriveAPIConnection api, ItemAddress itemAddress,
                      String name, String parentPath) {
        super(api);
        this.itemAddress = itemAddress;
        this.name = name;
        this.parentAddress = new PathAddress(parentPath);
    }

    /**
     * Move Item to a folder
     * 
     * @return Item moved Item
     */
    @Override
    public Item call() throws OneDriveException {
        return move();
    }

    /**
     * Move Item by itemId to a folder
     * 
     * @param ItemId String identifier of item to move
     * @param name String name of copied reference, if {@code null} the original
     *            name is used
     * @param parentRef ItemReference reference to parent folder
     * @return Item moved Item
     */
    private Item move() {
        LOG.info("Moving item: {} to folder: {}", itemAddress, parentAddress);
        Map<String, Object> map = newParentRefBody(name, parentAddress
            .getItemReference());
        Response response = api.webTarget()
            .path(itemAddress.getPathWithAddress())
            .resolveTemplateFromEncoded(ItemAddress.ITEM_ADDRESS,
                                        itemAddress.getAddress())
            .request()
            // https://stackoverflow.com/questions/22355235/patch-request-using-jersey-client
            .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
            .method(METHOD_PATCH, Entity.json(map));
        handleError(response, Status.OK,
                    "Failure moving item: " + itemAddress
                                         + " to parent folder: "
                                         + parentAddress);
        return response.readEntity(Item.class);
    }

}
