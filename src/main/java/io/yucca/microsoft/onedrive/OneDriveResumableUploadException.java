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

/**
 * OneDriveUploadException thrown if a resumable upload has failed and must be
 * restarted
 * 
 * @author yucca.io
 */
public class OneDriveResumableUploadException extends Exception {

    private static final long serialVersionUID = 7872452367431829458L;

    public OneDriveResumableUploadException() {
        super();
    }

    public OneDriveResumableUploadException(String message, Throwable cause,
                                   boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public OneDriveResumableUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public OneDriveResumableUploadException(String message) {
        super(message);
    }

    public OneDriveResumableUploadException(Throwable cause) {
        super(cause);
    }

}
