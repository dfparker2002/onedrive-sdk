/**
 * Copyright 2016 Rob Sessink
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

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.yucca.microsoft.onedrive.NotModifiedException;
import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.resources.LinkType;
import io.yucca.microsoft.onedrive.resources.facets.PermissionFacet;

public class PermissionActionIT extends AbstractActionIT {

    private PermissionFacet permission;

    @Before
    public void createPermission() {
        ItemAddress itemAddress = new IdAddress(uploadedItemId);
        CreateLink action = new CreateLink(api, itemAddress, LinkType.VIEW);
        permission = action.call();
        assertNotNull(permission);
    }

    @Test
    public void testGetPermissionsById() throws NotModifiedException {
        ItemAddress itemAddress = new IdAddress(uploadedItemId);
        PermissionAction action = new PermissionAction(api, itemAddress,
                                                         null);
        List<PermissionFacet> permissions = action.call();
        assertNotNull(permissions);
    }

    @Test
    public void testGetPermissionsByPath() throws NotModifiedException {
        ItemAddress itemAddress = new PathAddress(TestMother.FOLDER_APITEST
                                                  + "/"
                                                  + TestMother.ITEM_UPLOAD_1);
        PermissionAction action = new PermissionAction(api, itemAddress,
                                                         null);
        List<PermissionFacet> permissions = action.call();
        assertNotNull(permissions);
    }

    @Test
    public void testGetPermissionsSelect() throws NotModifiedException {
        ItemAddress itemAddress = new IdAddress(uploadedItemId);
        String[] select = new String[] { "link" };
        PermissionAction action = new PermissionAction(api, itemAddress,
                                                         select, null);
        List<PermissionFacet> permissions = action.call();
        assertNotNull(permissions);
    }

}
