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

import java.util.regex.Pattern;

import io.yucca.microsoft.onedrive.util.URLHelper;

/**
 * PathEncodingUtil
 *
 * @author yucca.io
 */
public final class PathEncodingUtil {

    public static final Pattern ONEDRIVE_RESERVED = Pattern
        .compile("[/\\\\*<>\\?:\\|]");

    public static final Pattern ONEDRIVE_BUSINESS_RESERVED = Pattern
        .compile("[/\\\\*<>\\?:\\|#%]");

    private PathEncodingUtil() {
    }

    /**
     * Validate and encode a path
     * 
     * @param path String
     * @return encoded path
     */
    public static String encodePath(String path) {
        validate(path);
        return URLHelper.encodeURIComponent(path);
    }

    /**
     * Validates a file/folder name
     * 
     * @param path String
     * @throws OneDriveException if name/path is invalid
     */
    public static void validate(String path) {
        if (!isValid(path)) {
            throw new OneDriveException("Filename or path contains reserved characters and is invalid: "
                                        + path);
        }
    }

    /**
     * Validate a file/folder name, checking for reserved characters in OneDrive
     * 
     * @param path String
     * @return boolean true if valid
     */
    public static boolean isValid(String path) {
        return !ONEDRIVE_RESERVED.matcher(path).find();
    }

    /**
     * Validates a file/folder name in OneDrive Business
     * 
     * @param path String
     * @throws OneDriveException if name/path is invalid
     */
    public static void validateBussiness(String path) {
        if (!isValidBusiness(path)) {
            throw new OneDriveException("Filename or path contains reserved characters and is invalid: "
                                        + path);
        }
    }

    /**
     * Validate a file/folder name, checking for reserved characters and
     * patterns in OneDrive Business
     *
     * @param path String
     * @return boolean true if valid
     */
    public static boolean isValidBusiness(String path) {
        if (ONEDRIVE_BUSINESS_RESERVED.matcher(path).find()) {
            return false;
        }
        if (path.startsWith("~") || path.startsWith(".")) {
            return false;
        }
        return true;
    }

}
