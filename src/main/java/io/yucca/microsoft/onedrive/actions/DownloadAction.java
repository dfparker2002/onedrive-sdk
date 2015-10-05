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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.NotModifiedException;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.PathUtil;
import io.yucca.microsoft.onedrive.resources.OneDriveError;

/**
 * Action to download Item content
 * 
 * @author yucca.io
 */
public class DownloadAction extends AbstractAction
    implements Callable<OneDriveContent> {

    public static final String ACTION = "content";

    private final ItemAddress itemAddress;

    private final String eTag;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item to download
     */
    public DownloadAction(OneDriveAPIConnection api, ItemAddress itemAddress) {
        super(api);
        this.itemAddress = itemAddress;
        this.eTag = null;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param itemAddress ItemAddress of item to download
     * @param eTag String an optional etag value of the cached item, if set this
     *            must match the etag value of remote item for deletion to
     *            succeed. If @{code null} than no etag validation is done
     */
    public DownloadAction(OneDriveAPIConnection api, ItemAddress itemAddress,
                          String eTag) {
        super(api);
        this.itemAddress = itemAddress;
        this.eTag = eTag;
    }

    /**
     * Download Item content
     * 
     * @return OneDriveContent
     * @throws NotModifiedException if an eTag was provided and matched, meaning
     *             the Item has not changed
     */
    @Override
    public OneDriveContent call() throws OneDriveException {
        return download();
    }

    /**
     * Download Item content
     * <p>
     * TODO: allow for range header
     * </p>
     * 
     * @param itemId String
     * @param eTag String an optional etag value of the cached item, if set and
     *            the tag matches the upstream item an NotModifiedException is
     *            thrown. If @{code null} than no etag validation is done
     * @return OneDriveContent
     * @throws NotModifiedException if an eTag was provided and matched, meaning
     *             the Item has not changed
     */
    private OneDriveContent download() {
        String address = itemAddress.getAddress();
        Response response = api.webTarget()
            .path(itemAddress.getPathWithAddress(ACTION))
            .resolveTemplateFromEncoded(PathUtil.ITEM_ADDRESS, address)
            .request().header(HEADER_IF_NONE_MATCH, createEtag(eTag)).get();
        handleNotModified(response);
        handleError(response, Status.OK,
                    "Failure downloading item: " + address);
        return response.readEntity(OneDriveContent.class);
    }

    /**
     * Download by URI
     * 
     * @param api OneDriveAPIConnection
     * @param uri URI to an Item or as returned in the Location header
     * @return OneDriveContent
     */
    public static OneDriveContent byURI(OneDriveAPIConnection api, URI uri) {
        Response response = api.webTarget(uri)
            .request(MediaType.APPLICATION_OCTET_STREAM).get();
        if (response.getStatus() != Status.FOUND.getStatusCode()) {
            OneDriveError e = response.readEntity(OneDriveError.class);
            throw new OneDriveException("Failure downloading item: " + uri,
                                        response.getStatus(), e);
        }
        return response.readEntity(OneDriveContent.class);
    }
}
