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

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import io.yucca.microsoft.onedrive.actions.ListChildrenAction;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * ItemIterator, allows iterating over an ItemIterable
 * <p>
 * When the top parameter is set as query parameter on a view.search action,
 * then the second last page contains a nextLink URL. When loading this page
 * this contains zero items which hogs the iterating logic, because not valid
 * item can be returned and NoSuchElementException is thrown. This could be
 * circumvented by checking if the page is valid in hasNextCollection()
 * </p>
 * 
 * @author yucca.io
 */
public class ItemIterator implements Iterator<Item> {

    private final OneDriveAPIConnection api;

    private ItemIterable page;

    private Iterator<Item> innerIterator;

    /**
     * Construct an ItemIterator
     * 
     * @param api OneDriveAPIConnection connection to the OneDrive API, used for
     *            fetching next pages
     * @param collection ItemIterable initial collection
     */
    public ItemIterator(final OneDriveAPIConnection api,
                        final ItemIterable collection) {
        if (api == null) {
            throw new IllegalArgumentException("OneDriveAPIConnection is null");
        }
        if (collection == null) {
            throw new IllegalArgumentException("ItemCollection is null");
        }
        this.api = api;
        this.page = collection;
        this.innerIterator = collection.innerIterator();
    }

    /**
     * Determine if the collection has a next item or has a next page
     * collection.
     * 
     * @return boolean true if item or page collection exists
     */
    @Override
    public boolean hasNext() {
        return innerIterator.hasNext() || page.hasNextCollection();
    }

    /**
     * Obtain the next item from the collection or fetch the next collection
     * over the link
     * 
     * @return Item next item
     */
    @Override
    public Item next() {
        if (innerIterator.hasNext()) {
            return innerIterator.next();
        } else if (page.hasNextCollection()) {
            loadNextCollection(page);
            return next();
        }
        throw new NoSuchElementException();
    }

    private void loadNextCollection(final ItemIterable collection) {
        try {
            this.page = ListChildrenAction
                .byURI(api, collection.getNextLink().toURI());
        } catch (URISyntaxException e) {
            throw new NoSuchElementException("URL for next collection is invalid. "
                                             + e.getMessage());
        }
        this.innerIterator = page.innerIterator();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
