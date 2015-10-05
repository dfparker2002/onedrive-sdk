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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

import org.junit.Test;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.OneDriveFile;
import io.yucca.microsoft.onedrive.OneDriveInputStream;
import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;

public class UploadActionIT extends AbstractActionIT {

    @Test
    public void testUploadByParentPath() throws FileNotFoundException {
        OneDriveFile file = new OneDriveFile(Paths
            .get(TestMother.ITEM_UPLOAD_1_PATH), TestMother.ITEM_UPLOAD_1);
        ItemAddress parentAddress = new PathAddress(TestMother.FOLDER_APITEST);
        UploadAction action = new UploadAction(api, file, parentAddress,
                                               ConflictBehavior.RENAME);

        assertNotNull(action.call());
    }

    @Test
    public void testUploadByParentId() throws FileNotFoundException {
        OneDriveFile file = new OneDriveFile(Paths
            .get(TestMother.ITEM_UPLOAD_2_PATH), TestMother.ITEM_UPLOAD_2);
        ItemAddress parentAddress = new IdAddress(apiTestFolderId);
        UploadAction action = new UploadAction(api, file, parentAddress,
                                               ConflictBehavior.FAIL);
        assertNotNull(action.call());
    }

    @Test
    public void testUploadByPathOneDriveInputStream()
        throws FileNotFoundException {
        FileInputStream is = new FileInputStream(new File(TestMother.ITEM_UPLOAD_3_PATH));
        OneDriveContent content = new OneDriveInputStream(is,
                                                          TestMother.ITEM_UPLOAD_3);
        ItemAddress parentAddress = new IdAddress(apiTestFolderId);
        UploadAction action = new UploadAction(api, content, parentAddress,
                                               ConflictBehavior.FAIL);
        assertNotNull(action.call());
    }
}
