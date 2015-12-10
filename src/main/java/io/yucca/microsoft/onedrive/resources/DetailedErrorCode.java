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
 * Detailed ErrorCodes possibly returned by OneDrive API. Only the detailed
 * error codes that are handled are defined here. See
 * See <a href="https://dev.onedrive.com/misc/errors.htm">https://dev.onedrive.com/misc/errors.htm</a>
 * 
 * @author yucca.io
 */
public enum DetailedErrorCode {

    RSYNCHAPPLYDIFFERENCES(
        "resyncApplyDifferences",
        "Resync required. Replace any local items with the server's version (including deletes) if you're sure that the service was up to date with your local changes when you last sync'd. Upload any local changes that the server doesn't know about."),
    RSYNCHREQUIRED("resyncRequired", "Resync is required."),
    RSYNCHUPLOADDIFFERENCES(
        "resyncUploadDifferences",
        "Resync required. Upload any local items that the service did not return, and upload any files that differ from the server's version (keeping both copies if you're not sure which one is more up-to-date).");

    private String code;

    private String message;

    private DetailedErrorCode(String code, String message) {
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
    public static DetailedErrorCode create(String code) {
        for (DetailedErrorCode e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

    public static String getMessage(String code) {
        for (DetailedErrorCode e : values()) {
            if (e.code.equals(code)) {
                return e.getMessage();
            }
        }
        return "Unhandled error code";
    }
}
