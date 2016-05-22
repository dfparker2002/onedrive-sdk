package io.yucca.microsoft.onedrive.synchronize;

import java.util.Collection;
import java.util.List;

import io.yucca.microsoft.onedrive.OneDrive;
import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.OneDriveFolder;
import io.yucca.microsoft.onedrive.OneDriveItem;
import io.yucca.microsoft.onedrive.QueryParameters;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Drive;
import io.yucca.microsoft.onedrive.resources.Identity;
import io.yucca.microsoft.onedrive.resources.QuotaFacet;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

public class OneDriveStub implements OneDrive {

    public static final String ITEM_ROOTID = "1";

    private final Drive drive;

    private final Identity identity;

    private final QuotaFacet quota;

    public OneDriveStub() {
        this.drive = new Drive();
        this.drive.setId(ITEM_ROOTID);
        this.identity = new Identity();
        this.identity.setDisplayName("John Doe");
        this.identity.setId("101");
        this.quota = new QuotaFacet();
    }

    @Override
    public OneDriveFolder createFolder(String name,
                                       ConflictBehavior behaviour) {
        return null;
    }

    @Override
    public OneDriveFolder createFolder(String name) {
        return null;
    }

    @Override
    public OneDriveFolder getRootFolder() {
        return null;
    }

    @Override
    public OneDriveFolder getSpecialFolder(SpecialFolder folder,
                                           QueryParameters parameters) {
        return null;
    }

    @Override
    public OneDriveFolder getFolder(ItemAddress address) {
        return null;
    }

    @Override
    public OneDriveFolder getFolder(String path) {
        return null;
    }

    @Override
    public OneDriveItem getItem(ItemAddress address) {
        return null;
    }

    @Override
    public OneDriveItem getItem(String path) {
        return null;
    }

    @Override
    public Collection<OneDriveItem> listChildren(QueryParameters parameters) {
        return null;
    }

    @Override
    public Collection<OneDriveItem> listChildren() {
        return null;
    }

    @Override
    public Collection<OneDriveItem> listRecent() {
        return null;
    }

    @Override
    public Collection<OneDriveItem> listShared() {
        return null;
    }

    @Override
    public Collection<OneDriveItem> sharedWithMe() {
        return null;
    }

    @Override
    public List<OneDriveItem> search(String query, QueryParameters parameters) {
        return null;
    }

    @Override
    public OneDriveItem upload(OneDriveContent content,
                               ConflictBehavior behavior) {
        return null;
    }

    @Override
    public OneDriveItem upload(OneDriveContent content) {
        return null;
    }

    @Override
    public ItemAddress getAddress() {
        return null;
    }

    @Override
    public Drive getDrive() {
        return drive;
    }

    @Override
    public List<Drive> getDrives() {
        return null;
    }

    @Override
    public Identity getUser() {
        return identity;
    }

    @Override
    public QuotaFacet getQuota() {
        return quota;
    }

    @Override
    public String getDriveId() {
        return drive.getId();
    }

}
