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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.RootAddress;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.ItemReference;

/**
 * Action to create a shared folder
 *
 * @author yucca.io
 */
public class CreateSharedAction extends AbstractAction
    implements Callable<Item> {

    private static final Logger LOG = LoggerFactory
        .getLogger(CreateSharedAction.class);

    public static final String ACTION = "children";

    private final String name;

    private final Item item;

    private final ItemAddress parentAddress;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param name String
     * @param item Item
     */
    protected CreateSharedAction(OneDriveAPIConnection api, String name,
                                 Item item) {
        super(api);
        this.name = name;
        this.item = item;
        this.parentAddress = new RootAddress();
    }

    /**
     * Create shared folder in drive root
     * 
     * @return Item
     */
    @Override
    public Item call() {
        return createShared();
    }

    private Item createShared() {
        LOG.info("Creating shared folder: {} in drive: {}", name,
                 parentAddress);
        Map<String, Object> map = sharedFolderMap(name);
        Response response = api.webTarget()
            .path(parentAddress.getPathWithAddress(ACTION))
            .resolveTemplateFromEncoded(ITEM_ADDRESS,
                                        parentAddress.getAddress())
            .request().post(Entity.json(toJson(map)));
        handleError(response, Status.CREATED,
                    "Failure creating shared folder: " + name + " in drive: "
                                              + parentAddress);
        return response.readEntity(Item.class);
    }

    /**
     * Create sharedFolder body
     * 
     * @param name String
     * @return Map<String, Object>
     */
    public Map<String, Object> sharedFolderMap(String name) {
        Map<String, Object> sharedFolder = new HashMap<>();
        sharedFolder.put("name", name);
        sharedFolder.put("remoteItem", remoteItemMap());
        return sharedFolder;
    }

    private Map<String, Object> remoteItemMap() {
        Map<String, Object> remoteItem = new HashMap<>();
        remoteItem.put("id", item.getId());
        remoteItem.put(ItemReference.PARENT_REFERENCE,
                       item.getParentReference().asDriveIdMap());
        return remoteItem;
    }

}
