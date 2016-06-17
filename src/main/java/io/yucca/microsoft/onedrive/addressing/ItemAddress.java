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
package io.yucca.microsoft.onedrive.addressing;

import io.yucca.microsoft.onedrive.resources.ItemReference;

/**
 * ItemAdress, defines an Item address and method of adressing like id, path,
 * root or specialfolder based.
 *
 * @author yucca.io
 */
public interface ItemAddress {

    /**
     * Build a base resource path based on underlying addressing method, like
     * /drive/items or /drive/root
     * 
     * @return String path
     */
    String getPath();

    /**
     * Build a based resource path based on underlying addressing method for
     * action, like /drive/items/children or /drive/root:/children
     * 
     * @param action String appended to path
     * @return String path
     */
    String getPath(String action);

    /**
     * Build a path with an address parameter based on underlying addressing
     * method, like /drive/items/{item-address} or /drive/root:/{item-address}
     * 
     * @return String path
     */
    String getPathWithAddress();

    /**
     * Build a path with an address parameter based on underlying addressing
     * method for action, like /drive/items/{item-address}/children or
     * /drive/root:/{item-address}:/children
     * 
     * @param action String appended to path
     * @return String path
     */
    String getPathWithAddress(String action);

    /**
     * Build a path with an address parameter, action and action parameter based
     * on underlying addressing method for action, like
     * /drive/items/{item-address}/permissions/{permission-id} or
     * /drive/root:/{item-address}:/permissions/{permission-id}
     * 
     * @param action String appended to path
     * @param parameter String appended to action path
     * @return String path
     */
    String getPathWithAddress(String action, String parameter);

    /**
     * Build a path with address and filename parameters based on underlying
     * addressing method for action, like
     * /drive/items/{item-address}:/{filename}:/content or
     * /drive/root:/{item-address}/{filename}:/content
     * 
     * @param action String appended to path
     * @return String path
     */
    String getPathWithAddressAndFilename(String action);

    /**
     * Get address of Item
     * 
     * @return String
     */
    String getAddress();

    /**
     * Get the absolute path for the item based on adressing method
     * 
     * @return String absolute item path
     */
    String absolutePath();

    /**
     * Get ItemReference
     * 
     * @return ItemReference
     */
    ItemReference getItemReference();
}
