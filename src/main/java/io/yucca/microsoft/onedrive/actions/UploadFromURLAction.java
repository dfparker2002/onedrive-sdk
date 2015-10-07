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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.NotModifiedException;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.addressing.URLAddress;
import io.yucca.microsoft.onedrive.facets.FileFacet;

/**
 * Action to upload an Item from a specified URL
 * 
 * @author yucca.io
 */
public class UploadFromURLAction extends AbstractAction
    implements Callable<URI> {

    private final Logger LOG = LoggerFactory
        .getLogger(UploadFromURLAction.class);

    public static final String ACTION = "children";

    private final URLAddress url;

    private final String name;

    private final ItemAddress parentAddress;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param url URLAddress of external content to upload
     * @param name String filename under which content is stored
     * @param parentAddress ItemAddress to parent folder relative to the root
     *            folder i.e. "/drive/root:/".
     */
    public UploadFromURLAction(OneDriveAPIConnection api, URLAddress url,
                               String name, ItemAddress parentAddress) {
        super(api);
        this.url = url;
        this.name = name;
        this.parentAddress = parentAddress;
    }

    /**
     * Upload Item from a URL
     * 
     * @return URI location to get of asynchronous job status, used in polling
     * @throws NotModifiedException if an eTag was provided and matched, meaning
     *             the Item has not changed
     */
    @Override
    public URI call() throws OneDriveException {
        return upload();
    }

    private URI upload() {
        LOG.info("Uploading content from URL: {} into folder: {}",
                 url.toString(), parentAddress);
        Map<String, Object> map = newUploadURLBody(url, name);
        Status successCodes = Status.ACCEPTED;
        Response response = api.webTarget()
            .path(parentAddress.getPathWithAddress(ACTION))
            .resolveTemplateFromEncoded(ItemAddress.ITEM_ADDRESS,
                                        parentAddress.getAddress())
            .request().header(HEADER_PREFER, RESPOND_ASYNC)
            .post(Entity.json(map));
        handleError(response, successCodes, "Failure uploading file from URL: "
                                            + url + " to: " + parentAddress);
        return response.getLocation();
    }

    protected Map<String, Object> newUploadURLBody(URLAddress url,
                                                   String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("@content.sourceUrl", url.getAddress());
        map.put("name", name);
        map.put("file", new FileFacet());
        return map;
    }
}
