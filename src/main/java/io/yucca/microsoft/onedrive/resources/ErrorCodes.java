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
 * ErrorCodes possibly returned by OneDrive API
 * 
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
        "Access is denied to the requested resource. The user might not have enough permission."),
    NOTFOUND(404, "The requested resource doesn’t exist."),
    METHOD_NOT_ALLOWED(
        405,
        "The HTTP method in the request is not allowed on the resource."),
    NOT_ACCEPTABLE(
        406,
        "This service doesn’t support the format requested in the Accept header."),
    CONFLICT(
        409,
        "The current state conflicts with what the request expects. For example, the specified parent folder might not exist."),
    GONE(410, "The requested resource is no longer available at the server."),
    LENGTH_REQUIRED(411, "A Content-Length header is required on the request."),
    PRECONDITION_FAILED(
        412,
        "A precondition provided in the request (such as an if-match header) does not match the resource's current state."),
    REQUEST_ENTITY_TOO_LARGE(
        413,
        "The request size exceeds the maximum limit."),
    UNSUPPORTED_MEDIA_TYPE(
        415,
        "The content type of the request is a format that is not supported by the service."),
    REQUEST_RANGE_NOT_SATISFIABLE(
        416,
        "The specified byte range is invalid or unavailable."),
    UNPROCESSABLE_ENTITY(
        422,
        "Cannot process the request because it is semantically incorrect."),
    TOO_MANY_REQUESTS(
        429,
        "Client application has been throttled and should not attempt to repeat the request until an amount of time has elapsed."),
    INTERNAL_SERVER_ERROR(
        500,
        "There was an internal server error while processing the request."),
    NOT_IMPLEMENTED(501, "The requested feature isn’t implemented."),
    SERVICE_UNAVAILABLE(
        503,
        "The service is temporarily unavailable. You may repeat the request after a delay. There may be a Retry-After header."),
    INSUFFICIENT_STORAGE(507, "The maximum storage quota has been reached."),
    BANDWITH_LIMIT_EXCEEDED(
        509,
        "Your app has been throttled for exceeding the maximum bandwidth cap. Your app can retry the request again after more time has elapsed.");

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
