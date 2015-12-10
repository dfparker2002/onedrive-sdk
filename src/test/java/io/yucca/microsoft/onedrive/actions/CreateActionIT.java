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
package io.yucca.microsoft.onedrive.actions;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.addressing.RootAddress;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;

public class CreateActionIT extends AbstractActionIT {

    @Test
    public void testCreateFolderByPath() {
        ItemAddress parentAddress = new PathAddress(TestMother.FOLDER_APITEST);
        CreateAction action = new CreateAction(api, TestMother.FOLDER_CREATE,
                                               parentAddress,
                                               ConflictBehavior.FAIL);
        assertNotNull(action.call());
    }

    @Test
    public void testCreateFolderById() {
        ItemAddress parentAddress = new IdAddress(apiTestFolderId);
        CreateAction action = new CreateAction(api, TestMother.FOLDER_CREATE,
                                               parentAddress,
                                               ConflictBehavior.FAIL);
        assertNotNull(action.call());
    }

    @Test
    public void testCreateFolderInRoot() {
        CreateAction action = new CreateAction(api, TestMother.FOLDER_CREATE,
                                               new RootAddress(),
                                               ConflictBehavior.RENAME);
        Item item = action.call();
        assertNotNull(item);
        new DeleteAction(api, new IdAddress(item.getId())).call();
    }

}
