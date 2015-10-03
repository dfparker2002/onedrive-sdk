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
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.PathUtil;
import io.yucca.microsoft.onedrive.resources.AsyncOperationStatus;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * Action to copy an Item to a folder
 * 
 * @author yucca.io
 */
public class CopyAction extends AbstractAction implements Callable<Item> {

    private static final Logger LOG = LoggerFactory.getLogger(CopyAction.class);

    public static final String COPY_ACTION = "action.copy";

    public static final int POLL_WAIT_SEC_DEFAULT = 2;

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
     * @param parentPath ItemAddress of parent folder to which the item is
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
        this.parentAddress = ItemAddress.pathBased(parentPath);
    }

    /**
     * Copy Item to a folder
     * 
     * @return Item copied Item
     */
    @Override
    public Item call() throws OneDriveException {
        return copy();
    }

    /**
     * Copy Item to a folder
     * 
     * @return Item copied Item
     */
    private Item copy() {
        String address = itemAddress.getAddress();
        Map<String, Object> map = newParentRefBody(name, parentAddress
            .getItemReference());
        Response response = api.webTarget()
            .path(itemAddress.getPathWithAddress(COPY_ACTION))
            .resolveTemplateFromEncoded(PathUtil.ITEM_ADDRESS, address)
            .request().header(HEADER_PREFER, RESPOND_ASYNC)
            .post(Entity.json(map));
        handleError(response,
                    Status.ACCEPTED, "Failed to copy item: " + address + " to: "
                                     + parentAddress.getPathWithAddress());
        try {
            return pollForCompletion(response.getLocation(), address,
                                     COPY_ACTION, POLL_WAIT_SEC_DEFAULT,
                                     TimeUnit.SECONDS);
        } catch (URISyntaxException e) {
            throw new OneDriveException("Result URI of copy.action is invalid",
                                        e);
        }
    }

    /**
     * Poll an URL for completion of an server-side asynchronous action.
     * Preferably this should done in a background task with callbacks
     * 
     * @param uri URI monitoring URI to action operation status
     * @param itemId String id of item on which action is performed
     * @param duration long duration between poll requests
     * @param unit TimeUnit duration unit
     * @return Item copied folder
     */
    private Item pollForCompletion(URI uri, String itemId, String action,
                                   long duration, TimeUnit unit)
                                       throws URISyntaxException {
        int errorcount = 5;
        while (true) {
            Response response = api.webTarget(uri).request().get();
            if (equalsStatus(response, Status.ACCEPTED)) {
                AsyncOperationStatus status = response
                    .readEntity(AsyncOperationStatus.class);
                LOG.info(status.toString());
            } else if (equalsStatus(response, Status.SEE_OTHER)) {
                // 303 See Other is never returned on completion instead 200 Ok
                // with the Item as response body. Understanding is untested XXX
                LOG.info("Operation: {} for item: {} completed.", action,
                         itemId);
                return MetadataAction.byURI(response.getLocation(), api);
            } else if (equalsStatus(response, Status.OK)) {
                LOG.info("Operation: {} for item: {} completed.", action,
                         itemId);
                return response.readEntity(Item.class);
            } else if (equalsStatus(response, Status.INTERNAL_SERVER_ERROR)) {
                AsyncOperationStatus status = response
                    .readEntity(AsyncOperationStatus.class);
                throw new OneDriveException(formatError(response.getStatus(),
                                                        status.toString()));
            } else {
                if (--errorcount < 0) {
                    throw new OneDriveException(formatError(response
                        .getStatus(), "Too many polling errors, aborting the polling for the completion of action: "
                                      + action + " on item:" + itemId));
                }
                LOG.debug("Poll for completion of action: {} on item: {} failed, retrying.",
                          action, itemId);
            }
            try {
                Thread.sleep(unit.toMillis(duration));
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

}
