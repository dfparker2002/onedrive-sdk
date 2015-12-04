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
package io.yucca.microsoft.onedrive.synchronize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.yucca.microsoft.onedrive.OneDriveFile;
import io.yucca.microsoft.onedrive.resources.Drive;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.facets.FileFacet;
import io.yucca.microsoft.onedrive.resources.facets.HashesFacet;

public class LocalFileImplTest {

    public static final String PATH_TEST_LOCALDRIVE = "src/test/resources/synchronize/localdrive";

    public static final String PATH_TEST_LOCALFOLDER = "src/test/resources/synchronize/localdrive/root";

    public static final String PATH_TEST_LOCALFILE = "src/test/resources/synchronize/localdrive/root/localfile-1.pdf";

    public static final String ITEM_ID = "1D230B56A9E3686";

    public static final String ITEM_ROOTID = "1D230B56A9E3686A";

    public static final String ITEM_PARENTID = "1D230B56A9E3686P";

    public static final String ITEM_NAME = "localfile-1.pdf";

    public static final String ITEM_NAMENEW = "onedrivefile-3.pdf";

    public static final String ITEM_PARENTNAME = "root";

    private LocalDriveRepository repository;

    private LocalFile file;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException, ParseException {
        repository = new FileSystemRepository(Paths.get(PATH_TEST_LOCALDRIVE),
                                              getDrive());
        LocalFolder folder = initializeParentFolder(testFolder);
        OneDriveFile content = new OneDriveFile("src/test/resources/files/test-upload-3.pdf");
        file = new LocalFileImpl(folder.getPath().resolve(ITEM_NAME), getItem(),
                                 content, repository);
        file.create();
    }

    @Test
    public void testExists() throws IOException {
        assertTrue(file.exists());
    }

    @Test
    public void testUpdate() throws IOException {
        file.update(getItem());
        assertTrue(file.exists());
    }

    @Test
    public void testRelateWith() throws IOException, ParseException {
        file.relateWith(getItem());
        assertEquals(file.getId(), ITEM_ID);
        assertEquals(file.getName(), ITEM_NAME);
    }

    @Test
    public void testRename() throws IOException, ParseException {
        file.rename(ITEM_NAMENEW);
        assertTrue(file.exists());
    }

    @Test
    public void testDelete() throws IOException, ParseException {
        file.delete();
        assertFalse(file.exists());
    }

    @Test
    public void testGetContent() throws FileNotFoundException {
        assertNotNull(file.getContent());
    }

    @Test
    public void testLastModificationStatusOlder() {
        assertEquals(ModificationStatus.OLDER,
                     file.lastModificationStatus(getItemNewer()));
    }

    @Test
    public void testLastModificationStatusNotModified() {
        assertEquals(ModificationStatus.NOTMODIFIED,
                     file.lastModificationStatus(getItem()));
    }

    @Test
    public void testIsContentModified() throws IOException {
        assertTrue(file.isContentModified(getItem()));
    }

    @Test
    public void testGetId() {
        assertEquals(ITEM_ID, file.getId());
    }

    @Test
    public void testGetParentId() throws IOException {
        assertEquals(ITEM_PARENTID, file.getParentId());
    }

    @Test
    public void testGetName() {
        assertEquals(ITEM_NAME, file.getName());
    }

    @Test
    public void testHasId() {
        assertTrue(file.hasId());
    }

    @Test
    public void testGetOneDriveContent() throws FileNotFoundException {
        OneDriveFile content = file.getContent();
        assertNotNull(content);
    }

    private Item getItem() {
        Item item = new Item();
        item.setId(ITEM_ID);
        item.setName(ITEM_NAME);
        item.setLastModifiedDateTime("2015-01-02T12:00:00.10Z");
        item.setCreatedDateTime("2015-01-01T12:00:00.10Z");
        HashesFacet hf = new HashesFacet();
        hf.setSha1Hash("0xCaa");
        FileFacet ff = new FileFacet();
        ff.setHashes(hf);
        item.setFile(ff);
        return item;
    }

    private Item getItemNewer() {
        Item item = new Item();
        item.setId(ITEM_ID);
        item.setName(ITEM_NAMENEW);
        item.setLastModifiedDateTime("2099-01-02T12:00:00.10Z");
        item.setCreatedDateTime("2099-01-01T12:00:00.10Z");
        HashesFacet hf = new HashesFacet();
        hf.setSha1Hash("0xCaa");
        FileFacet ff = new FileFacet();
        ff.setHashes(hf);
        item.setFile(ff);
        return item;
    }

    private LocalFolder initializeParentFolder(TemporaryFolder testFolder)
        throws IOException {
        Path path = Paths.get(testFolder.getRoot().getAbsolutePath());
        Item item = new Item();
        item.setId(ITEM_PARENTID);
        item.setName(path.getFileName().toString());
        item.setLastModifiedDateTime("2099-01-02T12:00:00.10Z");
        item.setCreatedDateTime("2099-01-01T12:00:00.10Z");
        LocalFolder parentFolder = new LocalFolderImpl(path, item, repository);
        parentFolder.update(item);
        return parentFolder;
    }

    private Drive getDrive() {
        Drive d = new Drive();
        d.setId(ITEM_ROOTID);
        return d;
    }

}
