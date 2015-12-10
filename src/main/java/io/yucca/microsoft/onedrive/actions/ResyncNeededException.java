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

import javax.ws.rs.core.Response;

import io.yucca.microsoft.onedrive.resources.DetailedErrorCode;
import io.yucca.microsoft.onedrive.resources.ErrorCode;
import io.yucca.microsoft.onedrive.resources.OneDriveError;

/**
 * ResyncNeededException, thrown if a client tries to reuse an old token after
 * being disconnected for a long time, or if server state has changed and a new
 * token is required.
 *
 * @author yucca.io
 */
public class ResyncNeededException extends Exception {

    private static final long serialVersionUID = 7042469321451501550L;

    private final OneDriveError error;

    private final URI nextLink;

    /**
     * Constructor
     * 
     * @param errorResponse Response
     */
    public ResyncNeededException(Response errorResponse) {
        this.error = errorResponse.readEntity(OneDriveError.class);
        this.nextLink = errorResponse.getLocation();
    }

    /**
     * @return Returns the errorCode.
     */
    public ErrorCode getErrorCode() {
        return ErrorCode.create(error.getError().getCode());
    }

    /**
     * @return Returns the detailedErrorCode.
     */
    public DetailedErrorCode getDetailedErrorCode() {
        return DetailedErrorCode
            .create(error.getError().getInnerError().getCode());
    }

    /**
     * @return Returns the nextLink.
     */
    public URI getNextLink() {
        return nextLink;
    }

}
