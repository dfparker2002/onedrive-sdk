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
package io.yucca.microsoft.onedrive.resources;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * ErrorCodes possibly returned by OneDrive API
 * 
 * @author yucca.io
 */
public enum ErrorCodes {

    ACCESDENIED(
        "accessDenied",
        "The caller doesn't have permission to perform the action."),
    ACTIVITY_LIMIT_REACHED(
        "activityLimitReached",
        "The app or user has been throttled."),
    GENERAL_EXCEPTION("generalException", "An unspecified error has occurred."),
    INVALID_RANGE(
        "invalidRange",
        "The specified byte range is invalid or unavailable."),
    INVALID_REQUEST("invalidRequest", "The request is malformed or incorrect."),
    ITEM_NOT_FOUND("itemNotFound", "The resource could not be found."),
    MALWARE_DETECTED(
        "malwareDetected",
        "Malware was detected in the requested resource."),
    NAME_ALREADY_EXISTS(
        "nameAlreadyExists",
        "The specified item name already exists."),
    NOT_ALLOWED("notAllowed", "The action is not allowed by the system."),
    NOT_SUPPORTED(
        "notSupported",
        "The request is not supported by the system."),
    RESOURCE_MODIFIED(
        "resourceModified",
        "The resource being updated has changed since the caller last read it, usually an eTag mismatch."),
    RSYNC_REQUIRED(
        "resyncRequired",
        "The delta token is no longer valid, and the app must reset the sync state."),
    SERVICE_NOT_AVAILABLE(
        "serviceNotAvailable",
        "The service is not available. Try the request again after a delay. There may be a Retry-After header."),
    QUOTA_LIMIT_REACHED(
        "quotaLimitReached",
        "The user has reached their quota limit."),
    UNAUTHENTICATED("unauthenticated", "The caller is not authenticated.");

    private String code;

    private String message;

    private ErrorCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @JsonCreator
    public static ErrorCodes create(String code) {
        for (ErrorCodes e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

    public static String getMessage(String code) {
        for (ErrorCodes e : values()) {
            if (e.code.equals(code)) {
                return e.getMessage();
            }
        }
        return "Unknown error code";
    }
}
