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

import static org.junit.Assert.*;

import org.junit.Test;

import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.facets.FileFacet;
import io.yucca.microsoft.onedrive.resources.facets.FolderFacet;

public class OneDriveItemFactoryTest {

    @Test
    public void testBuildFile() {
        Item item = new Item();
        item.setId("1");
        item.setFile(new FileFacet());
        OneDriveItem odi = OneDriveItemFactory.build(null, item);
        assertNotNull(odi);
        assertTrue(odi instanceof OneDriveItem);
    }

    @Test
    public void testBuildFolder() {
        Item item = new Item();
        item.setId("1");
        item.setFolder(new FolderFacet());
        OneDriveItem odi = OneDriveItemFactory.build(null, item);
        assertNotNull(odi);
        assertTrue(odi instanceof OneDriveFolder);
    }

    @Test(expected = OneDriveException.class)
    public void testBuildUnknown() {
        Item item = new Item();
        item.setId("1");
        OneDriveItemFactory.build(null, item);
    }

}
