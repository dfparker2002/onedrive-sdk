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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.apache.commons.configuration.ConfigurationException;
import org.glassfish.jersey.client.oauth2.TokenResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.yucca.microsoft.onedrive.QueryParameters.Builder;
import io.yucca.microsoft.onedrive.facets.PermissionFacet;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Drive;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.ItemIterable;
import io.yucca.microsoft.onedrive.resources.ItemReference;
import io.yucca.microsoft.onedrive.resources.LinkType;
import io.yucca.microsoft.onedrive.resources.Order;
import io.yucca.microsoft.onedrive.resources.Relationship;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;
import io.yucca.microsoft.onedrive.resources.SyncResponse;
import io.yucca.microsoft.onedrive.resources.ThumbnailSet;

public class OneDriveAPIConnectionIT {

    private static final String DOCUMENT_NEWNAME = "Document11.docx";

    private static final String CONFIGURATIONFILE = "src/test/resources/onedrive-integrationtest.properties";

    private OneDriveAPIConnection api;

    private OneDriveConfiguration configuration;

    private String uploadedItemId;

    private String apiTestFolderId;

    @Before()
    public void setUp() throws FileNotFoundException, ConfigurationException {
        this.configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
        this.api = new OneDriveAPIConnection(configuration);

        // create test directory and file
        this.apiTestFolderId = TestMother.createAPITestFolder(api).getId();
        this.uploadedItemId = TestMother.uploadTestItem(api).getId();
    }

    @Test
    public void testGetToken() {
        TokenResult token = api.getAccessToken();
        assertNotNull(token);
        assertNotNull(token);
        assertNotNull(token.getAccessToken());
        assertNotNull(token.getExpiresIn());
        assertNotNull(token.getRefreshToken());
        assertNotNull(token.getTokenType());
    }

    @Test
    public void testLogout() {
        api.logOut();
    }

    // test exception on logout failure

    @Test
    public void testGetDefaultDrive() {
        Drive drive = api.getDefaultDrive();
        assertNotNull(drive);
    }

    @Test
    public void testGetDrive() {
        Drive def = api.getDefaultDrive();
        assertNotNull(def);
        Drive drive = api.getDrive(def.getId());
        assertNotNull(drive);
    }

    @Test
    public void testCreateLinkById() throws URISyntaxException {
        PermissionFacet link = api.createLinkById(uploadedItemId,
                                                  LinkType.VIEW);
        assertNotNull(link);
    }

    @Test
    public void testCreateLinkByPath() throws URISyntaxException {
        PermissionFacet link = api
            .createLinkByPath(TestMother.FOLDER_APITEST + "/"
                              + TestMother.ITEM_UPLOAD_1, LinkType.VIEW);
        assertNotNull(link);
    }

    @Test
    public void testCopyById() throws URISyntaxException {
        ItemReference folder = new ItemReference();
        folder.setPath(TestMother.FOLDER_APITEST);
        Item item = api.copyById(uploadedItemId, "copied1.docx", folder);
        assertNotNull(item);
    }

    @Test
    public void testCopyByPath() throws URISyntaxException {
        ItemReference folder = new ItemReference();
        folder.setPath(TestMother.FOLDER_APITEST);
        Item item = api.copyByPath(TestMother.FOLDER_APITEST + "/"
                                   + TestMother.ITEM_UPLOAD_1, "copied1.docx",
                                   folder);
        assertNotNull(item);
    }

    @Test
    public void testDeleteById() {
        Item folder = api.createFolderByPath(TestMother.FOLDER_FOR_DELETION,
                                             TestMother.FOLDER_APITEST,
                                             ConflictBehavior.FAIL);
        api.deleteById(folder.getId());
    }

    @Test
    public void testDeleteByIdETag() {
        Item folder = api.createFolderByPath(TestMother.FOLDER_FOR_DELETION,
                                             TestMother.FOLDER_APITEST,
                                             ConflictBehavior.FAIL);
        api.deleteById(folder.getId(), folder.geteTag());
    }

    @Test
    public void testDeleteByPath() {
        api.createFolderByPath(TestMother.FOLDER_FOR_DELETION,
                               TestMother.FOLDER_APITEST,
                               ConflictBehavior.FAIL);
        api.deleteByPath(TestMother.FOLDER_APITEST + "/"
                         + TestMother.FOLDER_FOR_DELETION);
    }

    @Test
    public void testDeleteByPathETag() {
        Item folder = api.createFolderByPath(TestMother.FOLDER_FOR_DELETION,
                                             TestMother.FOLDER_APITEST,
                                             ConflictBehavior.FAIL);
        api.deleteByPath(TestMother.FOLDER_APITEST + "/"
                         + TestMother.FOLDER_FOR_DELETION, folder.geteTag());
    }

    @Test
    public void testListRootChildren() throws NotModifiedException {
        ItemIterable items = api.listChildrenInRoot();
        assertNotNull(items);
    }

    @Test
    public void testListChildrenById() throws NotModifiedException {
        ItemIterable items = api.listChildrenById(apiTestFolderId, null, Builder
            .newQueryParameters().orderby("name", Order.ASC).build());
        assertNotNull(items);
    }

    @Test(expected = NotModifiedException.class)
    public void testListChildrenByIdETagMatch() throws NotModifiedException {
        Item folder = api.getMetadataById(apiTestFolderId);
        api.listChildrenById(apiTestFolderId, folder.geteTag(), Builder
            .newQueryParameters().orderby("name", Order.ASC).build());
    }

    @Test
    public void testCreateFolder() {
        Item folder = api.createFolderByPath(TestMother.FOLDER_CREATE,
                                             TestMother.FOLDER_APITEST,
                                             ConflictBehavior.FAIL);
        assertNotNull(folder);
    }

    @Test
    public void testCreateFolderById() {
        Item folder = api.createFolderById(TestMother.FOLDER_CREATE,
                                           apiTestFolderId,
                                           ConflictBehavior.FAIL);
        assertNotNull(folder);
    }

    @Test
    public void testCreateFolderInRoot() {
        Item folder = api.createFolderInRoot(TestMother.FOLDER_CREATE,
                                             ConflictBehavior.RENAME);
        assertNotNull(folder);
        api.deleteByPath(TestMother.FOLDER_CREATE);
    }

    @Test
    public void testDownloadById() throws NotModifiedException {
        OneDriveContent f = api.downloadById(uploadedItemId, null);
        assertNotNull(f);
    }

    @Test
    public void testDownloadByPath() throws NotModifiedException {
        OneDriveContent f = api.downloadByPath(TestMother.FOLDER_APITEST + "/"
                                               + TestMother.ITEM_UPLOAD_1,
                                               null);
        assertNotNull(f);
    }

    @Test(expected = OneDriveException.class)
    public void testDownloadError() throws NotModifiedException {
        api.downloadByPath("Unknown.docx", null);
    }

    @Test(expected = NotModifiedException.class)
    public void testDownloadByIdETagMatch() throws NotModifiedException {
        Item item = api.getMetadataById(uploadedItemId);
        api.downloadById(uploadedItemId, item.geteTag());
    }

    @Test
    public void testGetMetadataById() throws NotModifiedException {
        Item item = api.getMetadataById(uploadedItemId);
        assertNotNull(item);
    }

    @Test
    public void testGetMetadataByIdExpand() throws NotModifiedException {
        Item item = api.getMetadataById("root", null, Builder
            .newQueryParameters().expand(Relationship.CHILDREN).build());
        assertNotNull(item);
        assertNotNull(item.getChildren());
    }

    @Test(expected = NotModifiedException.class)
    public void testGetMetadataByETagMatch() throws NotModifiedException {
        Item item = api.getMetadataById(uploadedItemId);
        api.getMetadataById(uploadedItemId, item.geteTag(), null);
    }

    @Test
    public void testGetMetadataByPath() throws NotModifiedException {
        Item item = api.getMetadataByPath(TestMother.FOLDER_APITEST, null,
                                          Builder.newQueryParameters()
                                              .expand(Relationship.CHILDREN)
                                              .build());
        assertNotNull(item);
        assertNotNull(item.getChildren());
    }

    // TODO testgetMetadataByURI(URI uri)

    @Test
    public void testGetSpecialFolder() {
        Item item = api.getSpecialFolder(SpecialFolder.DOCUMENTS, Builder
            .newQueryParameters().expand(Relationship.CHILDREN).build());
        assertNotNull(item);
    }

    @Test
    public void testUpdate() throws NotModifiedException {
        Item item = api.getMetadataById(uploadedItemId);
        item.setName(DOCUMENT_NEWNAME);
        assertNotNull(item);
        Item updated = api.update(item, null);
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
        Item item = api.getMetadataById(uploadedItemId);
        assertNotNull(item);
        api.update(item, "changedETag");
    }

    // testMoveById
    // testMoveByPath

    @Test
    public void testMoveAndRename() throws NotModifiedException {
        api.createFolderByPath(TestMother.FOLDER_MOVED,
                               TestMother.FOLDER_APITEST,
                               ConflictBehavior.FAIL);
        Item item = api.getMetadataById(uploadedItemId);
        assertNotNull(item);
        ItemReference folder = new ItemReference();
        folder
            .setPath(TestMother.FOLDER_APITEST + "/" + TestMother.FOLDER_MOVED);
        Item moved = api.moveById(item.getId(), DOCUMENT_NEWNAME, folder);
        assertNotNull(moved);
    }

    @Test
    public void testSearchById() {
        ItemIterable result = api.searchById(apiTestFolderId, "is", null);
        assertNotNull(result);
    }

    @Test
    public void testSearchByIdRoot() {
        ItemIterable result = api.searchInRoot("is");
        assertNotNull(result);
        for (Item item : result) {
            assertNotNull(item);
        }
    }

    @Test
    public void testSearchByPath() {
        ItemIterable result = api.searchByPath(TestMother.FOLDER_APITEST, "is",
                                               null);
        assertNotNull(result);
    }

    @Test
    public void testSyncChangesById() {
        SyncResponse result = api.syncChangesById(apiTestFolderId, null);
        assertNotNull(result);
        assertNotNull(result.getToken());
        for (Item item : result) {
            assertNotNull(item);
        }
    }

    @Test
    public void testSyncChangesByPath() {
        SyncResponse result = api.syncChangesByPath(TestMother.FOLDER_APITEST,
                                                    null);
        assertNotNull(result);
        assertNotNull(result.getToken());
    }

    @Test
    public void testThumbnailsById() {
        ThumbnailSet result = api.thumbnailsById(uploadedItemId);
        assertNotNull(result);
    }

    @Test
    public void testUploadByPath() throws FileNotFoundException {
        Item item = TestMother.uploadTestItem(api);
        assertNotNull(item);
    }

    @Test
    public void testUploadByPathOneDriveInputStream()
        throws FileNotFoundException {
        FileInputStream is = new FileInputStream(new File(TestMother.ITEM_UPLOAD_3_PATH));
        OneDriveContent content = new OneDriveInputStream(is,
                                                          TestMother.ITEM_UPLOAD_3);
        Item item = api.uploadByPath(content, TestMother.FOLDER_APITEST);
        assertNotNull(item);
    }

    @Test
    public void testUploadByParentId() throws FileNotFoundException {
        OneDriveFile file = new OneDriveFile(Paths
            .get(TestMother.ITEM_UPLOAD_2_PATH), TestMother.ITEM_UPLOAD_2);
        Item item = api.uploadByParentId(file, apiTestFolderId,
                                         ConflictBehavior.FAIL);
        assertNotNull(item);
    }

    @Test
    public void testResumableUploadByParentId()
        throws FileNotFoundException, OneDriveResumableUploadException {
        OneDriveFile file = new OneDriveFile(Paths
            .get(TestMother.ITEM_UPLOAD_3_PATH), TestMother.ITEM_UPLOAD_3);
        Item item = api.uploadResumableByParent(file, apiTestFolderId,
                                                ConflictBehavior.REPLACE);
        assertNotNull(item);
    }

    @Test
    public void testItemIterator() throws NotModifiedException {
        ItemIterable items = api.listChildrenInRoot();
        for (Item item : items) {
            assertNotNull(item);
        }
    }

    @After
    public void tearDown() {
        if (apiTestFolderId != null) {
            api.deleteById(apiTestFolderId);
        }
        api.close();
    }
}
