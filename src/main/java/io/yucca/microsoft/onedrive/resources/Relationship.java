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

import java.util.List;

/**
 * Relationship of item to other resources
 *
 * @author yucca.io
 */
public enum Relationship {

    CONTENT("content"), CHILDREN("children"), THUMBNAILS("thumbnails");

    private String relationship;

    private Relationship(String relationship) {
        this.relationship = relationship;
    }

    public String getRelationship() {
        return relationship;
    }

    /**
     * Create comma seperated relationships line
     * 
     * @param relationships List<Relationship>
     * @return String
     */
    public static String commaSeperated(List<Relationship> relationships) {
        StringBuilder b = new StringBuilder();
        for (Relationship r : relationships) {
            b.append(r.getRelationship()).append(",");
        }
        return b.toString().trim();
    }
}
