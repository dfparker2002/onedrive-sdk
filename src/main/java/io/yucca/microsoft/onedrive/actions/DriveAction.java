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

import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.resources.Drive;

/**
 * Action to get drive properties
 * 
 * @author yucca.io
 */
public class DriveAction extends AbstractAction implements Callable<Drive> {

    private final String driveId;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     */
    public DriveAction(OneDriveAPIConnection api) {
        super(api);
        this.driveId = null;
    }

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection
     * @param driveId String identifier of the drive
     */
    public DriveAction(OneDriveAPIConnection api, String driveId) {
        super(api);
        this.driveId = driveId;
    }

    /**
     * Create a link to an Item
     * 
     * @return Drive
     */
    @Override
    public Drive call() throws OneDriveException {
        return drive();
    }

    /**
     * Get default Drive properties
     * 
     * @return Drive
     */
    public Drive drive() {
        String path = (driveId == null) ? "/drive" : "/drives/{drive-id}";
        String name = (driveId == null) ? "default" : driveId;
        WebTarget target = api.webTarget().path(path);
        if (driveId != null) {
            target = target.resolveTemplateFromEncoded("drive-id", driveId);
        }
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
            .get();
        handleError(response, Status.OK,
                    "Failed to get properties for drive:" + name);
        return response.readEntity(Drive.class);
    }

}
