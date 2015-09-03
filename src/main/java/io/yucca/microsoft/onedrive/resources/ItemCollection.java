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
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.yucca.microsoft.onedrive.OneDriveAPIConnection;

/**
 * ItemCollection, a collection of items stored in OneDrive
 *
 * @author yucca.io
 */
public class ItemCollection implements ItemIterable {

    protected OneDriveAPIConnection api;

    private LinkedList<Item> value;

    @JsonProperty("@odata.nextLink")
    private String nextLink;

    public List<Item> getValue() {
        return value;
    }

    public void setValue(LinkedList<Item> value) {
        this.value = value;
    }

    @Override
    public String getNextLink() {
        return nextLink;
    }

    public void setNextLink(String nextLink) {
        this.nextLink = nextLink;
    }

    @Override
    public ItemIterable setApi(final OneDriveAPIConnection api) {
        this.api = api;
        return this;
    }

    @Override
    public ItemIterator iterator() {
        if (api == null) {
            throw new IllegalStateException("OneDriveAPIConnection must be set");
        }
        return new ItemIterator(api, this);
    }

    @Override
    public Iterator<Item> innerIterator() {
        return value.iterator();
    }

    @Override
    public boolean hasNextCollection() {
        return !(this.nextLink == null || this.nextLink.isEmpty());
    }

}
