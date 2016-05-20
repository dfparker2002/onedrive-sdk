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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import io.yucca.microsoft.onedrive.NotModifiedException;
import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.resources.LinkType;
import io.yucca.microsoft.onedrive.resources.Permission;
import io.yucca.microsoft.onedrive.resources.Role;

public class UpdatePermissionActionIT extends AbstractActionIT {

    private Permission permission;

    @Before
    public void testCreateLinkById() throws URISyntaxException {
        ItemAddress itemAddress = new IdAddress(uploadedItemId);
        CreateLinkAction action = new CreateLinkAction(api, itemAddress, LinkType.VIEW);
        permission = action.call();
        assertNotNull(permission);
    }

    @Test
    public void testUpdatePermissionsById() throws NotModifiedException {
        ItemAddress itemAddress = new IdAddress(uploadedItemId);
        permission.setRoles(new Role[] { Role.WRITE });
        UpdatePermissionAction action = new UpdatePermissionAction(api,
                                                                   itemAddress,
                                                                   permission);
        permission = action.call();
        assertNotNull(permission);
        assertEquals(Role.WRITE, permission.getRoles()[0]);
    }

    @Test
    public void testUpdatePermissionsByPath() throws NotModifiedException {
        ItemAddress itemAddress = new PathAddress(TestMother.FOLDER_APITEST
                                                  + "/"
                                                  + TestMother.ITEM_UPLOAD_1);
        permission.setRoles(new Role[] { Role.WRITE });
        UpdatePermissionAction action = new UpdatePermissionAction(api,
                                                                   itemAddress,
                                                                   permission);
        permission = action.call();
        assertNotNull(permission);
        assertEquals(Role.WRITE, permission.getRoles()[0]);
    }

}
