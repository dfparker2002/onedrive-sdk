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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.NotModifiedException;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.resources.ErrorCodes;
import io.yucca.microsoft.onedrive.resources.ItemReference;
import io.yucca.microsoft.onedrive.resources.OneDriveError;

/**
 * AbstractAction, provides generic functonality for actions
 * 
 * @author yucca.io
 */
public abstract class AbstractAction {

    public static final String HEADER_IF_MATCH = "if-match";

    public static final String HEADER_IF_NONE_MATCH = "if-none-match";

    public static final String HEADER_PREFER = "Prefer";

    public static final String RESPOND_ASYNC = "respond-async";

    public static final String METHOD_PATCH = "PATCH";

    protected final OneDriveAPIConnection api;

    protected AbstractAction(OneDriveAPIConnection api) {
        this.api = api;
    }

    /**
     * Handles a error if successCode is not returned
     * 
     * @param response Response
     * @param successStatus Status indicating success response
     * @param errorMessage String in case of failure
     */
    protected void handleError(Response response, Status successStatus,
                               String errorMessage) {
        if (equalsStatus(response, successStatus) == false) {
            OneDriveError e = response.readEntity(OneDriveError.class);
            throw new OneDriveException(formatError(response.getStatus(),
                                                    errorMessage, e),
                                        e);
        }
    }

    /**
     * Handles a error if one of the successCode is not returned
     * 
     * @param response Response
     * @param successStatus Status[] status indicating success response
     * @param errorMessage String in case of failure
     */
    protected void handleError(Response response, Status[] successStatus,
                               String errorMessage) {
        if (equalsStatus(response, successStatus)) {
            return;
        }
        OneDriveError e = response.readEntity(OneDriveError.class);
        throw new OneDriveException(formatError(response.getStatus(),
                                                errorMessage, e),
                                    e);
    }

    /**
     * Handle a possible 304 Not Modified status code if an eTag was provided in
     * the request and matched the upstream value.
     * 
     * @param response
     * @throws NotModifiedException if 304 Not Modified is returned
     */
    protected void handleNotModified(Response response)
        throws NotModifiedException {
        if (response.getStatus() == Status.NOT_MODIFIED.getStatusCode()) {
            throw new NotModifiedException();
        }
    }

    /**
     * Determine if statusCode of Response equals a status
     * 
     * @param response Response
     * @param status Status
     * @return true if equal
     */
    protected boolean equalsStatus(Response response, Status status) {
        return response.getStatus() == status.getStatusCode();
    }

    /**
     * Determine if statusCode of Response equals one of the status codes
     * 
     * @param response Response
     * @param successStatus Status[]
     * @return true if equal
     */
    protected boolean equalsStatus(Response response, Status[] successStatus) {
        for (Status code : successStatus) {
            if (response.getStatus() == code.getStatusCode()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Format a status code and error message
     * 
     * @param status int
     * @param message String
     * @param cause OneDriveError
     * @return String
     */
    protected String formatError(int status, String message,
                                 OneDriveError cause) {
        throw new OneDriveException(message + ", reason: " + status + " "
                                    + ErrorCodes.getMessage(status)
                                    + ", cause: " + cause);
    }

    /**
     * Format a status code and error message
     * 
     * @param status int
     * @param message String
     * @return String
     */
    protected String formatError(int status, String message) {
        throw new OneDriveException(message + ", reason: " + status + " "
                                    + ErrorCodes.getMessage(status));
    }

    /**
     * Create parentRef POST body
     * 
     * @param name String
     * @param parentRef ItemReference
     * @return Map<String, Object>
     */
    protected Map<String, Object> newParentRefBody(String name,
                                                   ItemReference parentRef) {
        Map<String, Object> map = new HashMap<>();
        map.put(ItemAddress.PARENT_REFERENCE, parentRef);
        if (name != null && !name.isEmpty()) {
            map.put("name", name);
        }
        return map;
    }

    protected ItemReference getItemReference(ItemAddress address) {
        ItemReference ref = new ItemReference();
        ref.setPath(address.absolutePath());
        return ref;
    }

    /**
     * Create an EntityTag
     * 
     * @param etag String value
     * @return null if etag is null or empty
     */
    protected EntityTag createEtag(String etag) {
        return (etag == null || etag.isEmpty()) ? null : new EntityTag(etag);
    }

    /**
     * Maps a Map<String, Object> to JSON
     * 
     * @param map Map<String, Object>
     * @return String json
     */
    public String toJson(Map<String, Object> map) {
        try {
            return api.getMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new OneDriveException("Failure mapping to JSON", e);
        }
    }
}
