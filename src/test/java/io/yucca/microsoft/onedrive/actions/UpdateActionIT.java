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
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.resources.Item;

public class UpdateActionIT extends AbstractActionIT {

    private static final String DOCUMENT_NEWNAME = "Document11.docx";

    @Test
    public void testUpdate() throws NotModifiedException {
        ItemAddress parentAddress = new IdAddress(uploadedItemId);
        Item item = new MetadataAction(api, parentAddress).call();
        assertNotNull(item);
        item.setName(DOCUMENT_NEWNAME);

        UpdateAction update = new UpdateAction(api, item);
        Item updated = update.call();
        assertNotNull(updated);
    }

    /**
     * Disabled because of bug
     * {@link https://github.com/OneDrive/onedrive-api-docs/issues/131}
     * 
     * @throws NotModifiedException
     */
    // @Test(expected = OneDriveException.class)
    public void testUpdateETagUnMatched() throws NotModifiedException {
        ItemAddress parentAddress = new IdAddress(uploadedItemId);
        Item item = new MetadataAction(api, parentAddress).call();
        assertNotNull(item);

        item.seteTag("changedETag");
        UpdateAction action = new UpdateAction(api, item);
        action.call();
    }

}
