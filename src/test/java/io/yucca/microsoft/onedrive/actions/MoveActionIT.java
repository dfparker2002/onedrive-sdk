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
import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;

public class MoveActionIT extends AbstractActionIT {

    private static final String DOCUMENT_NEWNAME = "Document11.docx";

    @Test
    public void testMoveAndRenameById() throws NotModifiedException {
        createMoved();
        ItemAddress itemAddress = new IdAddress(uploadedItemId);
        ItemAddress movedAddress = new PathAddress(TestMother.FOLDER_APITEST + "/"
                       + TestMother.FOLDER_MOVED);
        MoveAction action = new MoveAction(api, itemAddress, DOCUMENT_NEWNAME,
                                           movedAddress);
        assertNotNull(action.call());
    }

    @Test
    public void testMoveAndRenameByPath() throws NotModifiedException {
        ItemAddress itemAddress = new PathAddress(TestMother.FOLDER_APITEST + "/"
                       + TestMother.ITEM_UPLOAD_1);
        ItemAddress movedAddress = new PathAddress(TestMother.FOLDER_APITEST + "/"
                       + TestMother.FOLDER_MOVED);
        MoveAction action = new MoveAction(api, itemAddress, DOCUMENT_NEWNAME,
                                           movedAddress);
        assertNotNull(action.call());
    }

    public void createMoved() {
        ItemAddress parentAddress = new PathAddress(TestMother.FOLDER_APITEST);
        new CreateAction(api, TestMother.FOLDER_MOVED, parentAddress,
                         ConflictBehavior.FAIL).call();
    }

}
