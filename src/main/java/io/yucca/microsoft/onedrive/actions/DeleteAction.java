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

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;

/**
 * Action to delete an item
 * 
 * @author yucca.io
 */
public class DeleteAction extends AbstractAction implements Callable<Void> {

    private final ItemAddress itemAddress;

    private final String eTag;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item to delete
     */
    public DeleteAction(OneDriveAPIConnection api, ItemAddress itemAddress) {
        super(api);
        this.itemAddress = itemAddress;
        this.eTag = null;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item to delete
     * @param eTag String an optional etag value of the cached item, if set this
     *            must match the etag value of remote item for deletion to
     *            succeed. If @{code null} than no etag validation is done
     */
    public DeleteAction(OneDriveAPIConnection api, ItemAddress itemAddress,
                        String eTag) {
        super(api);
        this.itemAddress = itemAddress;
        this.eTag = eTag;
    }

    /**
     * Delete Item
     * 
     * @throws OneDriveException
     */
    @Override
    public Void call() throws OneDriveException {
        return delete();
    }

    private Void delete() {
        String address = itemAddress.getAddress();
        EntityTag tag = createEtag(eTag);
        Response response = api.webTarget().path(itemAddress.getPathWithAddress())
            .resolveTemplateFromEncoded(ItemAddress.ITEM_ADDRESS, address)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .header(HEADER_IF_MATCH, tag).delete();
        handleError(response, Status.NO_CONTENT,
                    "Failure deleting item: " + address);
        return null;
    }

}
