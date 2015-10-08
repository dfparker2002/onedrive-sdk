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

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.NotModifiedException;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.OneDriveError;

/**
 * Action to request Item metadata
 * 
 * @author yucca.io
 */
public class MetadataAction extends AbstractAction implements Callable<Item> {

    private final Logger LOG = LoggerFactory.getLogger(MetadataAction.class);

    private final ItemAddress itemAddress;

    private final String eTag;

    private final QueryParameters parameters;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item for which metadata is requested
     */
    public MetadataAction(OneDriveAPIConnection api, ItemAddress itemAddress) {
        super(api);
        this.itemAddress = itemAddress;
        this.eTag = null;
        this.parameters = null;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item for which metadata is requested
     * @param eTag String an optional etag value of the cached item, if set and
     *            the tag matches the upstream item, this metadata is not
     *            returned but instead NotModifiedException is thrown. If
     *            null/empty than no etag validation is done
     */
    public MetadataAction(OneDriveAPIConnection api, ItemAddress itemAddress,
                          String eTag) {
        super(api);
        this.itemAddress = itemAddress;
        this.eTag = eTag;
        this.parameters = null;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item for which metadata is requested
     * @param eTag String an optional etag value of the cached item, if set and
     *            the tag matches the upstream item, this metadata is not
     *            returned but instead NotModifiedException is thrown. If
     *            null/empty than no etag validation is done
     * @param parameters influences the way item results are returned, if null
     *            the default listing is returned
     */
    public MetadataAction(OneDriveAPIConnection api, ItemAddress itemAddress,
                          String eTag, QueryParameters parameters) {
        super(api);
        this.itemAddress = itemAddress;
        this.eTag = eTag;
        this.parameters = parameters;
    }

    @Override
    public Item call() throws OneDriveException {
        return metadata();
    }

    /**
     * Get metadata for an Item
     * 
     * @return Item
     * @throws NotModifiedException if an eTag was provided and matched, meaning
     *             the Item has not changed, should only be catched if eTag was
     *             provided
     */
    private Item metadata() throws NotModifiedException {
        LOG.info("Get metadata for item: {}", itemAddress);
        WebTarget target = api.webTarget()
            .path(itemAddress.getPathWithAddress())
            .resolveTemplateFromEncoded(ITEM_ADDRESS,
                                        itemAddress.getAddress());
        if (parameters != null) {
            target = parameters.configure(target);
        }
        Response response = target.request()
            .header(HEADER_IF_NONE_MATCH, createEtag(eTag)).get();
        handleNotModified(response);
        handleError(response, Status.OK,
                    "Failure getting metadata for item: " + itemAddress);
        return response.readEntity(Item.class);
    }

    /**
     * Get metadata by URI
     * 
     * @param api OneDriveAPIConnection
     * @param uri URI to an Item or as returned in the Location header
     * @return Item
     */
    public static Item byURI(URI uri, OneDriveAPIConnection api) {
        Response response = api.webTarget(uri).request().get();
        if (response.getStatus() != Status.OK.getStatusCode()) {
            OneDriveError e = response.readEntity(OneDriveError.class);
            throw new OneDriveException("Failure acquiring metadata for item: "
                                        + uri, response.getStatus(), e);
        }
        return response.readEntity(Item.class);
    }

}
