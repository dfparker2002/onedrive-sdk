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
import io.yucca.microsoft.onedrive.NotModifiedException;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.QueryParameters.Builder;
import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.ItemIterable;
import io.yucca.microsoft.onedrive.resources.Order;

public class ListChildrenActionIT extends AbstractActionIT {

    @Test
    public void testListRootChildren() throws NotModifiedException {
        ListChildrenAction action = new ListChildrenAction(api, null);
        assertNotNull(action.call());
    }

    @Test
    public void testListChildrenByPath() throws NotModifiedException {
        ItemAddress parentAddress = new PathAddress(TestMother.FOLDER_APITEST);
        QueryParameters params = Builder.newQueryParameters()
            .orderby("name", Order.ASC).build();
        ListChildrenAction action = new ListChildrenAction(api, parentAddress,
                                                           null, params);
        assertNotNull(action.call());
    }

    @Test(expected = NotModifiedException.class)
    public void testListChildrenByIdETagMatch() throws NotModifiedException {
        ItemAddress parentAddress = new IdAddress(apiTestFolderId);
        MetadataAction caction = new MetadataAction(api, parentAddress);
        Item folder = caction.call();

        QueryParameters params = Builder.newQueryParameters()
            .orderby("name", Order.ASC).build();
        ListChildrenAction action = new ListChildrenAction(api, parentAddress,
                                                           folder.geteTag(),
                                                           params);
        assertNotNull(action.call());
    }

    @Test
    public void testItemIterator() throws NotModifiedException {
        ListChildrenAction action = new ListChildrenAction(api, null);
        ItemIterable items = action.call();
        for (Item item : items) {
            assertNotNull(item);
        }
    }
}
