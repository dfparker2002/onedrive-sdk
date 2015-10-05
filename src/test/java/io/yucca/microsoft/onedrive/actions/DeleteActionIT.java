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

import org.junit.Test;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;

public class DeleteActionIT extends AbstractActionIT {

    @Test
    public void testDeleteById() {
        ItemAddress parentAddress = new IdAddress(apiTestFolderId);
        CreateAction caction = new CreateAction(api,
                                                TestMother.FOLDER_FOR_DELETION,
                                                parentAddress,
                                                ConflictBehavior.FAIL);
        Item folder = caction.call();
        DeleteAction action = new DeleteAction(api, new IdAddress(folder));
        action.call();
    }

    @Test
    public void testDeleteByIdETag() {
        ItemAddress parentAddress = new IdAddress(apiTestFolderId);
        CreateAction caction = new CreateAction(api,
                                                TestMother.FOLDER_FOR_DELETION,
                                                parentAddress,
                                                ConflictBehavior.FAIL);
        Item folder = caction.call();
        DeleteAction action = new DeleteAction(api, new IdAddress(folder),
                                               folder.geteTag());
        action.call();
    }

    @Test
    public void testDeleteByPath() {
        ItemAddress parentAddress = new IdAddress(apiTestFolderId);
        CreateAction caction = new CreateAction(api,
                                                TestMother.FOLDER_FOR_DELETION,
                                                parentAddress,
                                                ConflictBehavior.FAIL);
        Item folder = caction.call();
        DeleteAction action = new DeleteAction(api, new PathAddress(folder));
        action.call();
    }

    @Test
    public void testDeleteByPathETag() {
        ItemAddress parentAddress = new IdAddress(apiTestFolderId);
        CreateAction caction = new CreateAction(api,
                                                TestMother.FOLDER_FOR_DELETION,
                                                parentAddress,
                                                ConflictBehavior.FAIL);
        Item folder = caction.call();
        DeleteAction action = new DeleteAction(api, new PathAddress(folder),
                                               folder.geteTag());
        action.call();
    }

}
