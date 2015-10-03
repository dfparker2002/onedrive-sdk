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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.PathUtil;
import io.yucca.microsoft.onedrive.facets.FolderFacet;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * Action to create a folder
 * 
 * @author yucca.io
 */
public class CreateAction extends AbstractAction implements Callable<Item> {

    public static final String CREATE_ACTION = "children";

    private final String name;

    private final ItemAddress parentAddress;

    private final ConflictBehavior behavior;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param name String name of item
     * @param parentAddress ItemAddress reference to parent folder in which item
     *            is created
     * @param behaviour ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then defaults to {@link ConflictBehavior#FAIL}
     */
    public CreateAction(OneDriveAPIConnection api, String name,
                        ItemAddress parentAddress, ConflictBehavior behavior) {
        super(api);
        this.name = name;
        this.parentAddress = parentAddress;
        this.behavior = behavior;
    }

    /**
     * Create a folder
     * 
     * @return Item created folder
     * @throws OneDriveException
     */
    @Override
    public Item call() throws OneDriveException {
        return create();
    }

    /**
     * Create a folder
     * 
     * @return Item created folder
     */
    private Item create() {
        String address = parentAddress.getAddress();
        Map<String, Object> map = newFolderBody(name, behavior);
        Response response = api.webTarget()
            .path(parentAddress.getPathWithAddress(CREATE_ACTION))
            .resolveTemplateFromEncoded(PathUtil.ITEM_ADDRESS, address)
            .request().post(Entity.json(api.mapToJson(map)));
        handleError(response, Status.CREATED,
                    "Failure creating folder: " + name + " in parent folder: "
                                              + address);
        return response.readEntity(Item.class);
    }

    /**
     * Create a folder in the root of the drive
     * 
     * @param api OneDriveAPIConnection
     * @param name String name of folder to create
     * @param behaviour ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then defaults to {@link ConflictBehavior#FAIL}
     * @return Item created folder
     */
    public static Item createFolderInRoot(OneDriveAPIConnection api,
                                          String name,
                                          ConflictBehavior behaviour) {
        ItemAddress parentAddress = ItemAddress.rootAddress();
        return new CreateAction(api, name, parentAddress, behaviour).create();
    }

    /**
     * Create createFolder POST body
     * 
     * @param name String
     * @param behaviour ConflictBehavior
     * @return Map<String, Object>
     */
    Map<String, Object> newFolderBody(String name, ConflictBehavior behaviour) {
        String conflictBehavior = (behaviour == null)
            ? ConflictBehavior.FAIL.getName() : behaviour.getName();
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("folder", new FolderFacet());
        map.put("@name.conflictBehavior", conflictBehavior);
        return map;
    }

}
