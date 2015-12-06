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

import io.yucca.microsoft.onedrive.ItemIterable;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.resources.Item;

public class SearchActionIT extends AbstractActionIT {

    @Test
    public void testSearchById() {
        ItemAddress parentAddress = new IdAddress(apiTestFolderId);
        SearchAction action = new SearchAction(api, parentAddress, "is");
        assertNotNull(action.call());
    }

    @Test
    public void testSearchByPath() {
        ItemAddress parentAddress = new PathAddress(TestMother.FOLDER_APITEST);
        SearchAction action = new SearchAction(api, parentAddress, "is");
        assertNotNull(action.call());
    }

    @Test
    public void testSearchByIdRoot() {
        SearchAction action = new SearchAction(api, "is", null);
        ItemIterable result = action.call();
        assertNotNull(result);
        for (Item item : result) {
            assertNotNull(item);
        }
    }

    /**
     * Test case to determine if loading an ItemIterable.nextCollection does not
     * give back an empty collection. 
     * 
     * @throws OneDriveException
     */
    // @Test(expected = NoSuchElementException.class)
    @Test
    public void testSearchTopEmptyCollection() {
        QueryParameters parameters = QueryParameters.Builder
            .newQueryParameters().top(1).build();
        SearchAction action = new SearchAction(api, "is", parameters);
        ItemIterable result = action.call();
        assertNotNull(result);
        for (Item item : result) {
            assertNotNull(item);
        }
    }
}
