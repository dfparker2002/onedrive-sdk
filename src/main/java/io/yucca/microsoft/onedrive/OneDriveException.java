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
package io.yucca.microsoft.onedrive;

import io.yucca.microsoft.onedrive.resources.ErrorCodes;
import io.yucca.microsoft.onedrive.resources.OneDriveError;

/**
 * OneDriveException, exception thrown on failure in using the OneDrive API
 *
 * @author yucca.io
 */
public class OneDriveException extends RuntimeException {

    private static final long serialVersionUID = -7206345485593647831L;

    private final OneDriveError error;

    public OneDriveException() {
        super();
        this.error = null;
    }

    public OneDriveException(String message, Throwable cause) {
        super(message, cause);
        this.error = null;
    }

    public OneDriveException(String message) {
        super(message);
        this.error = null;
    }

    public OneDriveException(String message, OneDriveError error) {
        super(message);
        this.error = error;
    }

    public OneDriveException(String message, int status) {
        super(message + ", reason: " + status + " "
              + ErrorCodes.getMessage(status));
        this.error = null;
    }

    public OneDriveError getError() {
        return error;
    }

}
