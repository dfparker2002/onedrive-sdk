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

import io.yucca.microsoft.onedrive.NotModifiedException;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.resources.Item;

public class DownloadActionIT extends AbstractActionIT {

    @Test
    public void testDownloadById() throws NotModifiedException {
        ItemAddress itemAddress = new IdAddress(uploadedItemId);
        DownloadAction action = new DownloadAction(api, itemAddress);
        assertNotNull(action.call());
    }

    @Test
    public void testDownloadByPath() throws NotModifiedException {
        ItemAddress itemAddress = new PathAddress(TestMother.FOLDER_APITEST
                                                  + "/"
                                                  + TestMother.ITEM_UPLOAD_1);
        DownloadAction action = new DownloadAction(api, itemAddress);
        assertNotNull(action.call());
    }

    @Test
    public void testDownloadByURL()
        throws NotModifiedException, URISyntaxException {
        ItemAddress itemAddress = new PathAddress(TestMother.FOLDER_APITEST
                                                  + "/"
                                                  + TestMother.ITEM_UPLOAD_1);
        Item item = new MetadataAction(api, itemAddress).call();
        assertNotNull(DownloadAction.byURI(api, item.getDownloadUrl().toURI()));
    }

    @Test(expected = OneDriveException.class)
    public void testDownloadError() throws NotModifiedException {
        ItemAddress itemAddress = new PathAddress("Unknown.docx");
        DownloadAction action = new DownloadAction(api, itemAddress);
        action.call();
    }

    @Test(expected = NotModifiedException.class)
    public void testDownloadByIdETagMatch() throws NotModifiedException {
        ItemAddress itemAddress = new IdAddress(uploadedItemId);
        Item item = new MetadataAction(api, itemAddress).call();
        assertNotNull(item);
        DownloadAction action = new DownloadAction(api, itemAddress,
                                                   item.geteTag());
        assertNotNull(action.call());
    }

}
