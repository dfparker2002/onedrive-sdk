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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ItemTest {

    private Item item;

    @Before
    public void setUp() {
        item = new Item();
        item.setName("APITest");
        item.setId("100");
        ItemReference parentReference = new ItemReference();
        parentReference.setPath("/drive/root:");
        item.setParentReference(parentReference);
    }

    @Test
    public void testGetAbsolutePath() {
        assertEquals("/drive/root:/APITest", item.getAbsolutePath());
    }

    @Test
    public void testGetRelativePath() {
        assertEquals("/APITest", item.getRelativePath());
    }

}
