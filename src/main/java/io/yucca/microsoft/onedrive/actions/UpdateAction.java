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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * Action to update the all writable metadata properties of an Item.
 * 
 * @author yucca.io
 */
public class UpdateAction extends AbstractAction implements Callable<Item> {

    private final Logger LOG = LoggerFactory.getLogger(UpdateAction.class);

    private final Item item;

    private final String eTag;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param item Item containing properties to update
     */
    public UpdateAction(OneDriveAPIConnection api, Item item) {
        super(api);
        this.item = item;
        this.eTag = null;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param item Item containing properties to update
     * @param eTag String an optional etag value of the cached item, if set this
     *            must match the etag value of remote item for updating to
     *            succeed. If @{code null} than no etag validation is done
     */
    public UpdateAction(OneDriveAPIConnection api, Item item, String eTag) {
        super(api);
        this.item = item;
        this.eTag = eTag;
    }

    /**
     * Update the all writable metadata properties of an Item
     * 
     * @return Item updated Item
     */
    @Override
    public Item call() throws OneDriveException {
        return update();
    }

    /**
     * Update the all writable metadata properties of an Item
     * <p>
     * This sets parentReference to null, because updating by itemId and parent
     * Ref is not allowed
     * </p>
     * <p>
     * FIXME: update triggers a bug when then if-match header is set with a
     * bogus value, then "Malformed If-Match header" is returned
     * {@link https://github.com/OneDrive/onedrive-api-docs/issues/131}
     * </p>
     * 
     * @return Item updated Item
     */
    private Item update() {
        ItemAddress itemAddress = new IdAddress(item);
        if (item.getParentReference() != null) {
            item.setParentReference(null);
        }
        LOG.info("Updating item: {}", itemAddress);
        Response response = api.webTarget()
            .path(itemAddress.getPathWithAddress())
            .resolveTemplateFromEncoded(ITEM_ADDRESS,
                                        itemAddress.getAddress())
            .request().header(HEADER_IF_MATCH, createEtag(eTag))
            // patch method is not default available in jersey 2, so use a
            // workaround:
            // {@link
            // https://stackoverflow.com/questions/22355235/patch-request-using-jersey-client}
            .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
            .method(METHOD_PATCH, Entity.json(item));
        handleError(response, Status.OK,
                    "Failure updating item: " + itemAddress);
        return response.readEntity(Item.class);
    }

}
