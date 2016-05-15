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
package io.yucca.microsoft.onedrive.actions;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.yucca.microsoft.onedrive.ItemIterable;
import io.yucca.microsoft.onedrive.addressing.DriveIdAddress;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.resources.Item;

public class CreateSharedActionIT extends AbstractActionIT {

    private static final String SHARED_FOLDER = "shared folder";

    private Item shared;

    @Before
    public void setUpFixture() {
        Item item = getApiFolderMetadata();
        CreateSharedAction action = new CreateSharedAction(api, SHARED_FOLDER,
                                                           item);
        shared = action.call();
        assertNotNull(shared);
        assertNotNull(shared.getRemoteItem());
    }

    @Test
    public void testListSharedById() {
        ItemAddress address = new DriveIdAddress(shared);
        ListChildrenAction list = new ListChildrenAction(api, address, null);
        ItemIterable items = list.call();
        for (Item item : items) {
            assertNotNull(item);
        }
    }

    @After
    public void testDeleteShared() {
        DeleteAction delete = new DeleteAction(api,
                                               new PathAddress(SHARED_FOLDER));
        delete.call();
    }

}
