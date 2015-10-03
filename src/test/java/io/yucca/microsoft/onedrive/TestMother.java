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
package io.yucca.microsoft.onedrive;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import io.yucca.microsoft.onedrive.actions.CreateAction;
import io.yucca.microsoft.onedrive.actions.DeleteAction;
import io.yucca.microsoft.onedrive.actions.UploadAction;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.Order;
import io.yucca.microsoft.onedrive.resources.Relationship;

public class TestMother {

    public static final String FOLDER_APITEST = "APITest";

    public static final String FOLDER_CREATE = "createFolder";

    public static final String FOLDER_COPY = "copyFolder";

    public static final String FOLDER_MOVED = "movedFolder";

    public static final String FOLDER_RENAMED = "renamedFolder";

    public static final String FOLDER_FOR_DELETION = "folderForDeletion";

    public static final String ITEM_FILE_1 = "file1.txt";

    public static final String ITEM_UPLOAD_1_PATH = "src/test/resources/files/test-upload-1.txt";

    public static final String ITEM_UPLOAD_1 = "test-upload-1.txt";

    public static final String ITEM_UPLOAD_2_PATH = "src/test/resources/files/test-upload-2.docx";

    public static final String ITEM_UPLOAD_2 = "test-upload-2.docx";

    public static final String ITEM_UPLOAD_1_COPY = "test-upload-1-copy.txt";

    public static final String ITEM_UPLOAD_3_PATH = "src/test/resources/files/test-upload-3.pdf";

    public static final String ITEM_UPLOAD_3 = "test-upload-3.pdf";

    public static QueryParameters fullQueryParameters() {
        return QueryParameters.Builder.newQueryParameters()
            .expand(Relationship.CHILDREN)
            .select(new String[] { "name", "createdBy" }).top(10)
            .orderby("name", Order.ASC).build();
    }

    public static QueryParameters listChildrenQueryParameters() {
        return QueryParameters.Builder.newQueryParameters()
            .select(new String[] { "name", "createdBy" }).top(10)
            .orderby("name", Order.ASC).build();
    }

    public static QueryParameters searchQueryParameters() {
        return QueryParameters.Builder.newQueryParameters()
            .expand(Relationship.CHILDREN)
            .select(new String[] { "name", "createdBy" })
            .orderby("name", Order.ASC).build();
    }

    public static Item createAPITestFolder(OneDriveAPIConnection api) {
        deleteAPITestFolder(api);
        CreateAction action = new CreateAction(api, TestMother.FOLDER_APITEST,
                                               ItemAddress.rootAddress(),
                                               ConflictBehavior.FAIL);
        return action.call();
    }

    public static void deleteAPITestFolder(OneDriveAPIConnection api) {
        ItemAddress itemAddress = ItemAddress
            .pathBased(TestMother.FOLDER_APITEST);
        try {
            new DeleteAction(api, itemAddress).call();
        } catch (OneDriveException e) {
            // ignore 404 if folder did not exist
        }
    }

    public static Item uploadTestItem(OneDriveAPIConnection api)
        throws FileNotFoundException {
        OneDriveFile file = new OneDriveFile(Paths
            .get(TestMother.ITEM_UPLOAD_1_PATH), TestMother.ITEM_UPLOAD_1);
        ItemAddress parentAddress = ItemAddress
            .pathBased(TestMother.FOLDER_APITEST);
        UploadAction action = new UploadAction(api, file, parentAddress,
                                               ConflictBehavior.FAIL);
        return action.call();
    }

}
