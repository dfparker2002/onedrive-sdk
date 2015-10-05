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

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.SyncResponse;

public class SyncActionIT extends AbstractActionIT {

    @Test
    public void testSyncChangesById() {
        ItemAddress itemAddress = new IdAddress(apiTestFolderId);
        SyncAction action = new SyncAction(api, itemAddress, null, null);
        SyncResponse result = action.call();
        assertNotNull(result);
        assertNotNull(result.getToken());
        for (Item item : result) {
            assertNotNull(item);
        }
    }

    @Test
    public void testSyncChangesByPath() {
        ItemAddress itemAddress = new PathAddress(TestMother.FOLDER_APITEST);
        SyncAction action = new SyncAction(api, itemAddress, null, null);
        SyncResponse result = action.call();
        assertNotNull(result);
        assertNotNull(result.getToken());
        for (Item item : result) {
            assertNotNull(item);
        }
    }

}
