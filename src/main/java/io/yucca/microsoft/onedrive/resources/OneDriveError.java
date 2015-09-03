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
 * OneDriveError
 *
 * @author yucca.io
 */
public class OneDriveError {

    private InnerError error;

    @JsonCreator
    public OneDriveError() {
    }

    public OneDriveError(InnerError error) {
        this.error = error;
    }

    public InnerError getError() {
        return error;
    }

    public void setError(InnerError error) {
        this.error = error;
    }

    public static class InnerError {

        private String code;

        private String message;

        private InnerError innererror;

        public InnerError() {
        }

        @JsonCreator
        public InnerError(String code) {
            this.code = code;
        }

        public InnerError(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public InnerError(String code, String message, InnerError innererror) {
            this.code = code;
            this.message = message;
            this.innererror = innererror;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setInnererror(InnerError innererror) {
            this.innererror = innererror;
        }

        public InnerError getInnerError() {
            return innererror;
        }

    }

}
