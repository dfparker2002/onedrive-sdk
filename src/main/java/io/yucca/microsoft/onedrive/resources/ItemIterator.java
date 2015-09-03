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

import java.util.Iterator;
import java.util.NoSuchElementException;

import io.yucca.microsoft.onedrive.OneDriveAPIConnection;

/**
 * ItemIterator, allows iterating over an ItemIterable
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
     * @param api OneDriveAPIConnection connection to the OneDrive API, used in
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

    @Override
    public boolean hasNext() {
        return innerIterator.hasNext() || page.hasNextCollection();
    }

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
        this.page = api.listChildren(collection.getNextLink());
        this.innerIterator = page.innerIterator();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
