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
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.resources.Item;

public class DownloadActionIT extends AbstractActionIT {

    @Test
    public void testDownloadById() throws NotModifiedException {
        ItemAddress itemAddress = ItemAddress.idBased(uploadedItemId);
        DownloadAction action = new DownloadAction(api, itemAddress);
        assertNotNull(action.call());
    }

    @Test
    public void testDownloadByPath() throws NotModifiedException {
        ItemAddress itemAddress = ItemAddress
            .pathBased(TestMother.FOLDER_APITEST + "/"
                       + TestMother.ITEM_UPLOAD_1);
        DownloadAction action = new DownloadAction(api, itemAddress);
        assertNotNull(action.call());
    }

    @Test(expected = OneDriveException.class)
    public void testDownloadError() throws NotModifiedException {
        ItemAddress itemAddress = ItemAddress.pathBased("Unknown.docx");
        DownloadAction action = new DownloadAction(api, itemAddress);
        action.call();
    }

    @Test(expected = NotModifiedException.class)
    public void testDownloadByIdETagMatch() throws NotModifiedException {
        ItemAddress itemAddress = ItemAddress.idBased(uploadedItemId);
        Item item = new MetadataAction(api, itemAddress).call();
        assertNotNull(item);
        DownloadAction action = new DownloadAction(api, itemAddress,
                                                   item.geteTag());
        assertNotNull(action.call());
    }

}
