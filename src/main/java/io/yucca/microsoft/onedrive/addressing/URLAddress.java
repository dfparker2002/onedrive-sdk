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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import io.yucca.microsoft.onedrive.Addressing;

/**
 * URLAddress, addressing of items or external resources by URL
 * 
 * @author yucca.io
 */
public class URLAddress extends AbstractItemAddress {

    /**
     * Constructor
     * 
     * @param url URL location of an item or external resource
     */
    public URLAddress(URL url) {
        super(url.toString());
        init();
    }

    /**
     * Constructor
     * 
     * @param url String location of an item or external resource
     */
    public URLAddress(String url) throws MalformedURLException {
        super(url);
        init();
    }

    /**
     * Constructor
     * 
     * @param uri URI location of an item or external resource
     * @throws MalformedURLException
     */
    public URLAddress(URI uri) throws MalformedURLException {
        super(uri.toURL().toString());
        init();
    }

    private void init() {
        this.method = Addressing.URL;
        this.seperatorStart = "";
        this.seperatorEnd = "";
        this.basePath = "";
        this.addressWithFileName = "";
    }

}
