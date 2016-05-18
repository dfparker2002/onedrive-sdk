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
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.yucca.microsoft.onedrive.resources.Item;

/**
 * ItemCollection, an iterable collection of items stored in OneDrive
 *
 * @author yucca.io
 */
public class ItemCollection implements ItemIterable {

    /**
     * <pre>
     * Needed to prevent a org.codehaus.jackson.map.JsonMappingException: Conflicting setter definitions for property "config"....
     * </pre>
     */
    @JsonIgnore
    protected ItemProvider provider;

    @JsonDeserialize(as = LinkedList.class)
    private List<Item> value;

    @JsonProperty("@odata.nextLink")
    private URL nextLink;

    public List<Item> getValue() {
        return value;
    }

    public void setValue(List<Item> value) {
        this.value = value;
    }

    @Override
    public URL getNextLink() {
        return nextLink;
    }

    public void setNextLink(URL nextLink) {
        this.nextLink = nextLink;
    }

    @JsonIgnore
    @Override
    public ItemIterable setProvider(ItemProvider provider) {
        this.provider = provider;
        return this;
    }

    @Override
    public ItemIterator iterator() {
        if (provider == null) {
            throw new IllegalStateException("ItemProvider must be set");
        }
        return new ItemIterator(provider, this);
    }

    @Override
    public Iterator<Item> innerIterator() {
        return value.iterator();
    }

    @Override
    public boolean hasNextCollection() {
        return !(this.nextLink == null || this.nextLink.toString().isEmpty());
    }

}
