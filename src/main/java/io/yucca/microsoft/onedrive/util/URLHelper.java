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
package io.yucca.microsoft.onedrive.util;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * URLHelper
 * 
 * @author yucca.io
 */
public final class URLHelper {

    /**
     * Extract query parameters from an URL
     * 
     * <pre>
     * Based on: https://stackoverflow.com/questions/13592236/parse-the-uri-string-into-name-value-collection-in-java
     * </pre>
     * 
     * @param url URL
     * @return Map<String, String> query parameter map
     */
    public static Map<String, String> splitQuery(URL url) {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                query_pairs
                    .put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                         URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                assert false : "should never happen";
            }
        }
        return query_pairs;
    }

    /**
     * Encode a String
     * <p>
     * based on:
     * {@link https://stackoverflow.com/questions/14321873/java-url-encoding-urlencoder-vs-uri}
     * </p>
     * 
     * @param s String
     * @return String encoded
     */
    public static String encodeURIComponent(String s) {
        String result = s;
        try {
            result = URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20")
                .replaceAll("\\%21", "!").replaceAll("\\%27", "'")
                .replaceAll("\\%28", "(").replaceAll("\\%29", ")")
                .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            assert false : "should never happen";
        }
        return result;
    }
}
