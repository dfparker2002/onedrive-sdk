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

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.PathUtil;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.addressing.RootAddress;
import io.yucca.microsoft.onedrive.resources.ItemCollection;
import io.yucca.microsoft.onedrive.resources.ItemIterable;
import io.yucca.microsoft.onedrive.util.URLHelper;

/**
 * Action to search for items matching a query
 * 
 * @author yucca.io
 */
public class SearchAction extends AbstractAction
    implements Callable<ItemIterable> {

    public static final String ACTION = "view.search";

    private final ItemAddress parentAddress;

    private final String query;

    private final QueryParameters parameters;

    /**
     * Search in the root drive for items matching a query
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
     * Search
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
     *            returned, if null the default listing is returned
     */
    public SearchAction(OneDriveAPIConnection api, ItemAddress itemAddress,
                        String query, QueryParameters parameters) {
        super(api);
        this.parentAddress = itemAddress;
        this.query = query;
        this.parameters = parameters;
    }

    /**
     * Search for items matching a query
     * 
     * @return ItemCollection matching items
     */
    @Override
    public ItemIterable call() throws OneDriveException {
        return search();
    }

    /**
     * Search for items matching a query
     * 
     * @return ItemCollection matching items
     */
    private ItemIterable search() {
        String address = parentAddress.getAddress();
        WebTarget target = api.webTarget()
            .path(parentAddress.getPathWithAddress(ACTION))
            .resolveTemplateFromEncoded(PathUtil.ITEM_ADDRESS, address)
            .queryParam("q", URLHelper.encodeURIComponent(query));
        if (parameters != null) {
            // XXX when the top parameter is set, the last page of the total
            // resultset contains no values, leading to NoSuchElementException
            // must file a bug
            target = parameters.configure(target,
                                          new String[] { QueryParameters.EXPAND,
                                                         QueryParameters.TOP });
        }
        Response response = target.request().get();
        handleError(response, Status.OK,
                    "Failure searching for items that match query: " + query
                                         + " within item: " + address);
        return response.readEntity(ItemCollection.class).setApi(api);
    }

}
