/**
 * Copyright 2016 Rob Sessink
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

import java.util.List;
import java.util.concurrent.Callable;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.resources.Drive;

/**
 * Action listing information for all drives
 * 
 * @author yucca.io
 */
public class DrivesAction extends AbstractAction
    implements Callable<List<Drive>> {

    private static final Logger LOG = LoggerFactory
        .getLogger(DrivesAction.class);

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     */
    public DrivesAction(OneDriveAPIConnection api) {
        super(api);
    }

    /**
     * Create a link to an Item
     * 
     * @return List<Drive>
     */
    @Override
    public List<Drive> call() {
        return drives();
    }

    private List<Drive> drives() {
        String path = "/drives";
        LOG.info("Getting information for all available drives: {}", path);
        WebTarget target = api.webTarget().path(path);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
            .get();
        handleError(response, Status.OK, "Failed to get available drives");
        return response.readEntity(new GenericType<List<Drive>>() {
        });
    }

}
