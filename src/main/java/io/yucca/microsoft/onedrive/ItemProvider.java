package io.yucca.microsoft.onedrive;
/**
 * Copyright 2016 Rob Sessink
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

import java.net.URI;

/**
 * ItemProvider
 * 
 * @author yucca.io
 */
public interface ItemProvider {

    /**
     * Provides an ItemIterable to a collection identifier by URI
     * 
     * @param uri URI identifier to a collection of items
     * @return ItemIterable
     */
    ItemIterable byURI(URI uri);

}
