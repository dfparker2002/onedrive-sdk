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

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ItemCollection;
import io.yucca.microsoft.onedrive.ItemIterable;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.RootAddress;
import io.yucca.microsoft.onedrive.util.URLHelper;

/**
 * Action to search for items matching a query
 * 
 * @author yucca.io
 */
public class SearchAction extends AbstractAction
    implements Callable<ItemIterable> {

    private static final Logger LOG = LoggerFactory
        .getLogger(SearchAction.class);

    public static final String ACTION = "view.search";

    private final ItemAddress parentAddress;

    private final String query;

    private final QueryParameters parameters;

    /**
     * Constructor,
     * 
     * @param api OneDriveAPIConnection
     * @param query String search query
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned
     */
    public SearchAction(OneDriveAPIConnection api, String query,
                        QueryParameters parameters) {
        super(api);
        this.parentAddress = new RootAddress();
        this.query = query;
        this.parameters = parameters;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param parentAddress ItemAddress of folder in which to search
     * @param query String search query
     */
    public SearchAction(OneDriveAPIConnection api, ItemAddress parentAddress,
                        String query) {
        super(api);
        this.parentAddress = parentAddress;
        this.query = query;
        this.parameters = null;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param parentAddress ItemAddress of folder in which to search
     * @param query String search query
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned. If the top
     *            QueryParameter is passed this will be removed because this
     *            triggers a bug in OneDrive API
     */
    public SearchAction(OneDriveAPIConnection api, ItemAddress parentAddress,
                        String query, QueryParameters parameters) {
        super(api);
        this.parentAddress = parentAddress;
        this.query = query;
        this.parameters = parameters;
    }

    /**
     * Search for items matching a query
     * 
     * @return ItemIterable matching items
     */
    @Override
    public ItemIterable call() {
        return search();
    }

    private ItemIterable search() {
        LOG.info("Searching for items in folder: {} matching query: {}, with query parameter: {}",
                 parentAddress, query, parameters);
        WebTarget target = api.webTarget()
            .path(parentAddress.getPathWithAddress(ACTION))
            .resolveTemplateFromEncoded(ITEM_ADDRESS,
                                        parentAddress.getAddress())
            .queryParam("q", URLHelper.encodeURIComponent(query));
        if (parameters != null) {
            // XXX
            // https://github.com/OneDrive/onedrive-api-docs/issues/250
            // when the top parameter is set, the last page of the total
            // resultset contains no values, leading to a
            // NoSuchElementException. Therefor top parameter is removed
            target = parameters.configure(target,
                                          new String[] { QueryParameters.EXPAND,
                                                         QueryParameters.TOP });
        }
        Response response = target.request().get();
        handleError(response, Status.OK,
                    "Failure searching for items that match query: " + query
                                         + " within folder: " + parentAddress);
        return response.readEntity(ItemCollection.class).setApi(api);
    }

}
