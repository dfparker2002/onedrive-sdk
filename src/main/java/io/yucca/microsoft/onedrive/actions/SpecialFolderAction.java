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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.addressing.SpecialAddress;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

/**
 * Action to acquire a item representing a special folder (i.e. Documents,
 * Photos)
 * 
 * @author yucca.io
 */
public class SpecialFolderAction extends AbstractAction
    implements Callable<Item> {

    private final Logger LOG = LoggerFactory
        .getLogger(SpecialFolderAction.class);

    private final SpecialFolder folder;

    private final QueryParameters parameters;

    /**
     * Constructor
     * 
     * @param folder SpecialFolder
     */
    public SpecialFolderAction(OneDriveAPIConnection api,
                               SpecialFolder folder) {
        super(api);
        this.folder = folder;
        this.parameters = null;
    }

    /**
     * Constructor
     * 
     * @param folder SpecialFolder
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned
     */
    public SpecialFolderAction(OneDriveAPIConnection api, SpecialFolder folder,
                               QueryParameters parameters) {
        super(api);
        this.folder = folder;
        this.parameters = parameters;
    }

    /**
     * Get a special folder
     * 
     * @return Item
     */
    @Override
    public Item call() throws OneDriveException {
        return specialFolder();
    }

    /**
     * Get a special folder
     * 
     * @return Item
     */
    public Item specialFolder() {
        String path = new SpecialAddress(folder).getPath();
        LOG.info("Getting metadata for special folder: {}", path);
        WebTarget target = api.webTarget().path(path)
            .resolveTemplateFromEncoded(ItemAddress.SPECIAL_FOLDER_NAME,
                                        folder.getName());
        if (parameters != null) {
            target = parameters.configure(target);
        }
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
            .get();
        handleError(response, Status.OK,
                    "Failed to get special folder: " + folder.getName());
        return response.readEntity(Item.class);
    }

}
