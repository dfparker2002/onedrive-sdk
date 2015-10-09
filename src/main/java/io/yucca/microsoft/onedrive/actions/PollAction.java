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
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.resources.AsyncOperationStatus;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * Action to poll an URL for completion of an server-side asynchronous action
 * (copy or upload from url).
 * 
 * @author yucca.io
 */
public class PollAction extends AbstractAction implements Callable<Item> {

    private static final Logger LOG = LoggerFactory.getLogger(PollAction.class);

    public static final int POLLING_INTERVAL = 2;

    private final URI location;

    private final String action;

    private final ItemAddress itemAddress;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param location URI resource location of asynchronous job status
     * @param itemAddress ItemAddress address of item or resource URL on which
     *            action is performed
     * @param action String asynchronous job name
     */
    public PollAction(OneDriveAPIConnection api, URI location,
                      ItemAddress itemAddress, String action) {
        super(api);
        this.location = location;
        this.itemAddress = itemAddress;
        this.action = action;
    }

    /**
     * Poll an asynchronous job for completion
     * 
     * @return Item result of asynchronous action
     */
    @Override
    public Item call() throws OneDriveException {
        return pollForCompletion(location, itemAddress, action,
                                 POLLING_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * Poll an URL for completion of an server-side asynchronous action.
     * Preferably this should done in a background task with callbacks
     * 
     * @param uri URI to monitoring action operation status
     * @param duration long duration between polling requests
     * @param unit TimeUnit duration unit
     * @return Item result of asynchronous action
     */
    private Item pollForCompletion(URI uri, ItemAddress itemAddress,
                                   String action, long duration,
                                   TimeUnit unit) {
        LOG.info("Polling completing of action: {} for item: {} ", action,
                 itemAddress);
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
                         itemAddress);
                return byURI(response.getLocation(), api);
            } else if (equalsStatus(response, Status.OK)) {
                LOG.info("Operation: {} for item: {} completed.", action,
                         itemAddress);
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
                                      + action + " on item: " + itemAddress));
                }
                LOG.debug("Poll for completion of action: {} on item: {} failed, retrying.",
                          action, itemAddress);
            }
            try {
                Thread.sleep(unit.toMillis(duration));
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

    /**
     * Get metadata by URI
     * 
     * @param api OneDriveAPIConnection
     * @param uri URI as returned in the Location header
     * @return Item
     */
    public Item byURI(URI uri, OneDriveAPIConnection api) {
        Response response = api.webTarget(uri).request().get();
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new OneDriveException("Failure acquiring metadata for item: "
                                        + uri, response.getStatus());
        }
        return response.readEntity(Item.class);
    }
}
