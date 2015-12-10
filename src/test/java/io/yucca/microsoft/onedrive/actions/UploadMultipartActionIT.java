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

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import org.junit.Test;

import io.yucca.microsoft.onedrive.OneDriveFile;
import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;

public class UploadMultipartActionIT extends AbstractActionIT {

    @Test
    public void testMultipartUploadByParentId()
        throws FileNotFoundException, OneDriveResumableUploadException {
        OneDriveFile file = new OneDriveFile(Paths
            .get(TestMother.ITEM_UPLOAD_3_PATH), TestMother.ITEM_UPLOAD_3);
        ItemAddress parentAddress = new IdAddress(apiTestFolderId);
        UploadMultipartAction action = new UploadMultipartAction(api, file,
                                                                 parentAddress,
                                                                 ConflictBehavior.FAIL);
        assertNotNull(action.call());
    }

    @Test
    public void testMultipartUploadByParentPath()
        throws FileNotFoundException, OneDriveResumableUploadException {
        OneDriveFile file = new OneDriveFile(Paths
            .get(TestMother.ITEM_UPLOAD_3_PATH), TestMother.ITEM_UPLOAD_3);
        ItemAddress parentAddress = new PathAddress(TestMother.FOLDER_APITEST);
        UploadMultipartAction action = new UploadMultipartAction(api, file,
                                                                 parentAddress,
                                                                 ConflictBehavior.FAIL);
        assertNotNull(action.call());
    }

}
