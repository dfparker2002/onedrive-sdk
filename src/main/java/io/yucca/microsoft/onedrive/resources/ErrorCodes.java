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

/**
 * @author yucca.io
 */
public enum ErrorCodes {

    BADREQUEST(
        400,
        "Cannot process the request because it is malformed or incorrect."),
    UNAUTHORIZED(
        401,
        "Required authentication information is either missing or not valid for the resource."),
    FORBIDDEN(
        403,
        "Access is denied to the requested resource. The user might not have enough permission.");

    private int code;

    private String message;

    private ErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessage(int code) {
        for (ErrorCodes e : values()) {
            if (e.code == code) {
                return e.getMessage();
            }
        }
        return "Unknown error code";
    }
}
