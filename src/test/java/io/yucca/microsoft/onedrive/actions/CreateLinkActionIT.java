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

import java.net.URISyntaxException;

import org.junit.Test;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.resources.LinkType;

public class CreateLinkActionIT extends AbstractActionIT {

    @Test
    public void testCreateLinkById() throws URISyntaxException {
        ItemAddress itemAddress = ItemAddress.idBased(uploadedItemId);
        CreateLink action = new CreateLink(api, itemAddress, LinkType.VIEW);
        assertNotNull(action.call());
    }

    @Test
    public void testCreateLinkByPath() throws URISyntaxException {
        ItemAddress itemAddress = ItemAddress
            .pathBased(TestMother.FOLDER_APITEST + "/"
                       + TestMother.ITEM_UPLOAD_1);
        CreateLink action = new CreateLink(api, itemAddress, LinkType.VIEW);
        assertNotNull(action.call());
    }

}
