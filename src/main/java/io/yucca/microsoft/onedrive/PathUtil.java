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

import javax.ws.rs.client.WebTarget;

import io.yucca.microsoft.onedrive.actions.Addressing;

/**
 * Helper for construction resource paths used in {@link WebTarget} and to
 * construct item paths
 * 
 * <pre>
 * TODO
 * 1. Add support for path based on driveId
 * </pre>
 * 
 * @author yucca.io
 */
public final class PathUtil {

    public static final String ITEM_ADDRESS = "item-address";

    public static final String FILENAME = "filename";

    public static final String PARENT_REFERENCE = "parentReference";

    public static final String SPECIAL_FOLDER_NAME = "special-folder-name";

    public static final String DRIVE_ROOT = "/drive/root";

    public static final String DRIVE_ITEMS = "/drive/items";

    public static final String DRIVE_SPECIAL = "/drive/special";

    /**
     * Build an resource path based on addressing method
     * 
     * @param method Addressing method
     * @return String path
     */
    public static String buildPath(Addressing method) {
        return buildPath(null, method);
    }

    /**
     * Build a resource path for an action based on addressing method
     * 
     * @param action String name
     * @param method Addressing method
     * @return String path
     */
    public static String buildPath(String action, Addressing method) {
        String base = getBasePath(method);
        if (action != null) {
            base += "/" + action;
        }
        return base;
    }

    /**
     * Build a resource path based on addressing method
     * 
     * @param action String name
     * @param method Addressing method
     * @return String path
     */
    public static String buildAddressPath(String action, Addressing method) {
        String base = getBasePath(method) + seperatorStart(method)
                      + "{item-address}";
        if (action != null) {
            base += seperatorEnd(method) + action;
        }
        return base;
    }

    /**
     * Build a resource path including filename parameter based on addressing
     * method
     * 
     * @param action String name
     * @param method Addressing method
     * @return String path
     */
    public static String buildAddressPathWithFilename(String action,
                                                      Addressing method) {
        String base = getBasePath(method) + seperatorStart(method)
                      + addressWithFilenameParameter(method);
        if (action != null) {
            base += ":/" + action;
        }
        return base;
    }

    static String addressWithFilenameParameter(Addressing method) {
        switch (method) {
        case ID:
            return "{item-address}:/{filename}";
        case ROOT:
            return "{item-address}{filename}";
        case PATH:
        case SPECIAL:
            return "{item-address}/{filename}";
        default:
            throw new IllegalArgumentException("Invalid addressing method");
        }
    }

    /**
     * Get resource base path based on addressing method
     * 
     * @param method Addressing
     * @return String
     */
    static String getBasePath(Addressing method) {
        switch (method) {
        case ID:
            return DRIVE_ITEMS;
        case PATH:
        case ROOT:
            return DRIVE_ROOT;
        case SPECIAL:
            return DRIVE_SPECIAL + "/{special-folder-name}";
        default:
            throw new IllegalArgumentException("Invalid addressing method");
        }
    }

    /**
     * Get start seperator
     * 
     * @param method Addressing
     * @return String
     */
    static String seperatorStart(Addressing method) {
        switch (method) {
        case ID:
            return "/";
        case PATH:
        case ROOT:
        case SPECIAL:
            return ":";
        default:
            throw new IllegalArgumentException("Invalid addressing method");
        }
    }

    /**
     * Get end seperator
     * 
     * @param method Addressing
     * @return String
     */
    static String seperatorEnd(Addressing method) {
        switch (method) {
        case ID:
            return "/";
        case PATH:
        case ROOT:
        case SPECIAL:
            return ":/";
        default:
            throw new IllegalArgumentException("Invalid addressing method");
        }
    }

    /**
     * Get the absolute item path based on adressing method
     * 
     * @param method Addressing method
     * @param address Item address
     * @return String absolute item path
     */
    public static String itemPath(Addressing method, String address) {
        return PathUtil.buildPath(method) + seperatorStart(method) + address;
    }

}
