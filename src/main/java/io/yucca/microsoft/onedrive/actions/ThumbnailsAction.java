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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.resources.ThumbnailSet;

/**
 * Action to acquire a item representing a special folder (i.e. Documents,
 * Photos)
 * 
 * @author yucca.io
 */
public class ThumbnailsAction extends AbstractAction
    implements Callable<ThumbnailSet> {

    private static final Logger LOG = LoggerFactory
        .getLogger(ThumbnailsAction.class);

    public static final String ACTION = "thumbnails";

    private final ItemAddress itemAddress;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress
     */
    public ThumbnailsAction(OneDriveAPIConnection api,
                            ItemAddress itemAddress) {
        super(api);
        this.itemAddress = itemAddress;
    }

    /**
     * Get Thumbnails
     * 
     * @return ThumbnailSet
     */
    @Override
    public ThumbnailSet call() {
        return thumbnails();
    }

    private ThumbnailSet thumbnails() {
        LOG.info("Getting thumbsnails for item: {}", itemAddress);
        Response response = api.webTarget()
            .path(itemAddress.getPathWithAddress(ACTION))
            .resolveTemplateFromEncoded(ITEM_ADDRESS, itemAddress.getAddress())
            .request().get();
        handleError(response, Status.OK,
                    "Failure acquiring thumbnails for item: " + itemAddress);
        return response.readEntity(ThumbnailSet.class);
    }

}
