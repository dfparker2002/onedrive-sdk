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

import io.yucca.microsoft.onedrive.actions.Addressing;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.ItemReference;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

/**
 * ItemAdress, defines an Item address and method of adressing like id, path,
 * root or specialfolder based.
 * 
 * @author yucca.io
 */
public class ItemAddress {

    private String address;

    private Addressing method;

    /**
     * Constructor
     * 
     * @param address String item identifier or a path that is relative to the
     *            root folder i.e. "/drive/root:"
     * @param method Addressing method that relates to the address parameter
     */
    public ItemAddress(String address, Addressing method) {
        this.address = address;
        this.method = method;
    }

    public String getAddress() {
        return address;
    }

    public Addressing getMethod() {
        return method;
    }

    /**
     * Build a base path based on underlying addressing method, like
     * /drive/items or /drive/root
     * 
     * @return String path
     */
    public String getPath() {
        return PathUtil.buildPath(method);
    }

    /**
     * Build a base path based on underlying addressing method for action, like
     * /drive/items/children or /drive/root/children
     * 
     * @param action String
     * @return String path
     */
    public String getPath(String action) {
        return PathUtil.buildPath(action, method);
    }

    /**
     * Build a path with an address parameter based on underlying addressing
     * method, like /drive/items/{item-address} or /drive/root:/{item-address}
     * 
     * @return String path
     */
    public String getPathWithAddress() {
        return PathUtil.buildAddressPath(null, method);
    }

    /**
     * Build a path with an address parameter based on underlying addressing
     * method for action, like /drive/items/{item-address}/children or
     * /drive/root:/{item-address}:/children
     * 
     * @param action String
     * @return String path
     */
    public String getPathWithAddress(String action) {
        return PathUtil.buildAddressPath(action, method);
    }

    /**
     * Build a path with address and filename parameters based on underlying
     * addressing method for action, like
     * /drive/items/{item-address}:/{filename}:/content or
     * /drive/root:/{item-address}/{filename}:/content
     * 
     * @param action String
     * @return String path
     */
    public String getPathWithAddressAndFilename(String action) {
        return PathUtil.buildAddressPathWithFilename(action, method);
    }

    /**
     * Construct an Id based address
     * 
     * @param itemId String item identifier
     * @return ItemAddress
     */
    public static ItemAddress idBased(String itemId) {
        return new ItemAddress(itemId, Addressing.ID);
    }

    /**
     * Construct an Id based address
     * 
     * @param item Item
     * @return ItemAddress
     */
    public static ItemAddress idBased(Item item) {
        return new ItemAddress(item.getId(), Addressing.ID);
    }

    /**
     * Construct a {@link Addressing#PATH} based address
     * 
     * @param itemPath String item path that is relative to the root folder i.e.
     *            "/drive/root:"
     * @return ItemAddress
     */
    public static ItemAddress pathBased(String itemPath) {
        return new ItemAddress("/" + itemPath, Addressing.PATH);
    }

    /**
     * Construct an ItemAddress for Item, using {@link Addressing#PATH}
     * addressing
     * 
     * @param item Item
     * @return ItemAddress
     */
    public static ItemAddress pathBased(Item item) {
        return new ItemAddress("/" + item.getRelativePath(), Addressing.PATH);
    }

    /**
     * Construct an ItemAddress for a child in Item, using
     * {@link Addressing#PATH} addressing
     * 
     * @param item Item
     * @param address String relative address of child within the item
     * @return ItemAddress
     */
    public static ItemAddress pathBased(Item item, String address) {
        return new ItemAddress("/" + item.getRelativePath() + "/" + address,
                               Addressing.PATH);
    }

    /**
     * Construct an ItemAddress for the root drive, using
     * {@link Addressing#ROOT} addressing
     * 
     * @return ItemAddress
     */
    public static ItemAddress rootAddress() {
        return new ItemAddress("/", Addressing.ROOT);
    }

    /**
     * Construct an an ItemAddress for a special folder, using
     * {@link Addressing#SPECIAL} addressing
     * 
     * @return ItemAddress
     */
    public static ItemAddress specialAddress() {
        return new ItemAddress("", Addressing.SPECIAL);
    }

    /**
     * Construct an ItemAddress for a special folder, using
     * {@link Addressing#SPECIAL} addressing
     * 
     * @return ItemAddress
     */
    public static ItemAddress specialAddress(SpecialFolder folder) {
        return new ItemAddress(folder.getName(), Addressing.SPECIAL);
    }

    /**
     * @return ItemReference
     */
    public ItemReference getItemReference() {
        ItemReference ref = new ItemReference();
        ref.setPath(PathUtil.itemPath(method, address));
        return ref;
    }

}
