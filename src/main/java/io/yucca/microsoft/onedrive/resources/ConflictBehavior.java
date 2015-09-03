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

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * ConflictBehavior
 *
 * @author yucca.io
 */
public enum ConflictBehavior {

    RENAME("rename"), REPLACE("replace"), FAIL("fail");

    private String name;

    private ConflictBehavior(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @JsonCreator
    public static ConflictBehavior create(String value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        for (ConflictBehavior v : values()) {
            if (v.getName().equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }

}
