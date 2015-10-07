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

import java.net.URL;
import java.util.Iterator;

import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.ItemIterator;

/**
 * ItemIterable defines an collection of Items that can be iterated
 * 
 * @author yucca.io
 */
public interface ItemIterable extends Iterable<Item> {

    /**
     * Get the link to next page
     * 
     * @return URL
     */
    URL getNextLink();

    /**
     * Get Iterator for the paged collection
     * 
     * @return ItemIterator
     */
    @Override
    ItemIterator iterator();

    /**
     * Get iterator for the inner Item collection
     * 
     * @return Iterator<Item>
     */
    Iterator<Item> innerIterator();

    /**
     * Inject the OneDriveAPIConnection.
     * 
     * @param api OneDriveAPIConnection connection to the OneDrive API used in
     *            fetching next pages
     * @return ItemIterable
     */
    ItemIterable setApi(OneDriveAPIConnection api);

    /**
     * See if collection has a next page
     * 
     * @return boolean true
     */
    boolean hasNextCollection();
}
