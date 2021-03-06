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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ItemCollection;
import io.yucca.microsoft.onedrive.ItemIterable;
import io.yucca.microsoft.onedrive.NotModifiedException;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.RootAddress;

/**
 * Action to list children in a folder or drive item
 * 
 * @author yucca.io
 */
public class ListChildrenAction extends AbstractAction
    implements Callable<ItemIterable> {

    private static final Logger LOG = LoggerFactory.getLogger(DriveAction.class);

    public static final String ACTION = "children";

    private final ItemAddress itemAddress;

    private final String eTag;

    private final QueryParameters parameters;

    /**
     * List children in the root drive
     * 
     * @param api OneDriveAPIConnection
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned.
     */
    public ListChildrenAction(OneDriveAPIConnection api,
                              QueryParameters parameters) {
        super(api);
        this.itemAddress = new RootAddress();
        this.eTag = null;
        this.parameters = parameters;

    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of folder for which to list children
     * @param eTag String an optional etag value of the cached item, if set and
     *            the tag matches the upstream item an NotModifiedException is
     *            thrown. If @{code null} than no etag validation is done
     */
    public ListChildrenAction(OneDriveAPIConnection api,
                              ItemAddress itemAddress, String eTag) {
        super(api);
        this.itemAddress = itemAddress;
        this.eTag = eTag;
        this.parameters = null;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of folder for which to list children
     * @param eTag String an optional etag value of the cached item, if set and
     *            the tag matches the upstream item an NotModifiedException is
     *            thrown. If @{code null} than no etag validation is done
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned
     */
    public ListChildrenAction(OneDriveAPIConnection api,
                              ItemAddress itemAddress, String eTag,
                              QueryParameters parameters) {
        super(api);
        this.itemAddress = itemAddress;
        this.eTag = eTag;
        this.parameters = parameters;
    }

    /**
     * List children in folder
     * 
     * @return ItemCollection
     * @throws NotModifiedException if an eTag was provided and matched, meaning
     *             the folder has not changed
     */
    @Override
    public ItemIterable call() throws OneDriveException {
        return listChildren();
    }

    /**
     * List children in folder
     * 
     * @return ItemCollection
     * @throws NotModifiedException if an eTag was provided and matched, meaning
     *             the folder has not changed
     */
    public ItemIterable listChildren() {
        LOG.info("Listing children in folder: {} with query parameters: {}",
                 itemAddress, parameters);
        WebTarget target = api.webTarget()
            .path(itemAddress.getPathWithAddress(ACTION))
            .resolveTemplateFromEncoded(ITEM_ADDRESS, itemAddress.getAddress());
        if (parameters != null) {
            target = parameters.configure(target, QueryParameters.EXPAND);
        }
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
            .header(HEADER_IF_NONE_MATCH, createEtag(eTag)).get();
        handleNotModified(response);
        handleError(response, Status.OK,
                    "Failed to list children for item:" + itemAddress);
        return response.readEntity(ItemCollection.class).setApi(api);
    }

    /**
     * List children by URL, used to get the next item collection using
     * {@link ItemCollection#getNextLink()}
     * 
     * @param api OneDriveAPIConnection
     * @param uri URI to as returned in the Location header
     * @return ItemIterable
     */
    public static ItemIterable byURI(OneDriveAPIConnection api, URI uri) {
        Response response = api.webTarget(uri)
            .request(MediaType.APPLICATION_JSON_TYPE).get();
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new OneDriveException("Failure listing children by URL: "
                                        + uri, response.getStatus());
        }
        return response.readEntity(ItemCollection.class).setApi(api);
    }
}
