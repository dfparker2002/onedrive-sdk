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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.SyncResponse;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.RootAddress;

/**
 * Action enumerate the changes for a folder for a specific state, which can be
 * used to synchronise a local copy of the drive or vise-versa.
 * 
 * @author yucca.io
 */
public class SyncAction extends AbstractAction
    implements Callable<SyncResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(SyncAction.class);

    public static final String ACTION = "view.delta";

    private final ItemAddress parentAddress;

    private final String token;

    private final String top;

    /**
     * Enumerate the sync changes for the root drive for a specific state
     * 
     * @param api OneDriveAPIConnection
     * @param token String The last token returned from the previous call to
     *            view.changes. If {@code null}, view.changes will return the
     *            current state of the drive
     * @param top String The desired number of items to return in the next page.
     *            If {@code null}, the defaults items are returned
     */
    public SyncAction(OneDriveAPIConnection api, String token, String top) {
        super(api);
        this.parentAddress = new RootAddress();
        this.token = token;
        this.top = top;
    }

    /**
     * Enumerate the sync changes for a folder item
     * 
     * @param api OneDriveAPIConnection
     * @param parentAddress ItemAddress address of folder for which to enumerate
     *            changes
     * @param token String The last token returned from the previous call to
     *            view.changes. If {@code null}, view.changes will return the
     *            current state of the drive
     * @param top String The desired number of items to return in the next page.
     *            If {@code null}, the defaults items are returned
     */
    public SyncAction(OneDriveAPIConnection api, ItemAddress parentAddress,
                      String token, String top) {
        super(api);
        this.parentAddress = parentAddress;
        this.token = token;
        this.top = top;
    }

    /**
     * Enumerate the sync changes for a folder for a specific stated, which can
     * be used to synchronise a local copy of the drive.
     * 
     * @return SyncResponse matching items
     * @throws ResyncNeededException if a new delta synchronization is needed
     */
    @Override
    public SyncResponse call() throws ResyncNeededException {
        return sync();
    }

    private SyncResponse sync() throws ResyncNeededException {
        LOG.info("Enumerate the synchronization changes for folder: {}",
                 parentAddress);
        Response response = api.webTarget()
            .path(parentAddress.getPathWithAddress(ACTION))
            .queryParam(QueryParameters.TOKEN, token)
            .queryParam(QueryParameters.TOP, top)
            .resolveTemplateFromEncoded(ITEM_ADDRESS,
                                        parentAddress.getAddress())
            .request().get();
        if (equalsStatus(response, Status.GONE)) {
            throw new ResyncNeededException(response);
        }
        handleError(response, Status.OK,
                    "Failure enumerating changes for folder: " + parentAddress);
        return (SyncResponse)response.readEntity(SyncResponse.class)
            .setApi(api);
    }

    /**
     * Get a SyncResponse by URL, used if ResyncNeededException, indicating to
     * start a fresh delta enumeration from scratch
     * 
     * @param api OneDriveAPIConnection
     * @param uri URI to as returned in the Location header
     * @return SyncResponse
     */
    public static SyncResponse byURI(OneDriveAPIConnection api, URI uri) {
        Response response = api.webTarget(uri)
            .request(MediaType.APPLICATION_JSON_TYPE).get();
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new OneDriveException("Failure acquiring enumerating changes for URI: "
                                        + uri, response.getStatus());
        }
        return (SyncResponse)response.readEntity(SyncResponse.class)
            .setApi(api);
    }

}
