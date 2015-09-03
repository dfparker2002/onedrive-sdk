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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.configuration.ConfigurationException;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.oauth2.TokenResult;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import io.yucca.microsoft.onedrive.facets.FolderFacet;
import io.yucca.microsoft.onedrive.facets.PermissionFacet;
import io.yucca.microsoft.onedrive.resources.AsyncOperationStatus;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Drive;
import io.yucca.microsoft.onedrive.resources.ErrorCodes;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.ItemCollection;
import io.yucca.microsoft.onedrive.resources.ItemIterable;
import io.yucca.microsoft.onedrive.resources.ItemReference;
import io.yucca.microsoft.onedrive.resources.LinkType;
import io.yucca.microsoft.onedrive.resources.OneDriveError;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;
import io.yucca.microsoft.onedrive.resources.SyncResponse;
import io.yucca.microsoft.onedrive.resources.ThumbnailSet;
import io.yucca.microsoft.onedrive.resources.UploadSession;
import io.yucca.microsoft.onedrive.util.JulFacade;
import io.yucca.microsoft.onedrive.util.URLHelper;

/**
 * Represents an authenticated connection to the OneDrive API
 * 
 * @author yucca.io
 */
public class OneDriveAPIConnection {

    private static final Logger LOG = LoggerFactory
        .getLogger(OneDriveAPIConnection.class);

    public static final String ONEDRIVE_URL = "https://api.onedrive.com/v1.0";

    public static final String ONEDRIVE_BUSINESS_URL = "https://{tenant}-my.sharepoint.com/_api/v2.0";

    public static final String HEADER_IF_MATCH = "if-match";

    public static final String HEADER_IF_NONE_MATCH = "if-none-match";

    private final OneDriveConfiguration configuration;

    private Client client;

    private ObjectMapper mapper;

    private OneDriveOAuthHelper oauthHelper;

    /**
     * Constructs the connection to the OneDrive API, the authorization is
     * delayed until the first API request
     * 
     * @param configurationFile String
     * @throws ConfigurationException if configuration is invalid
     * @throws FileNotFoundException if configuration file does not exist
     */
    public OneDriveAPIConnection(OneDriveConfiguration configuration)
        throws FileNotFoundException, ConfigurationException {
        this.configuration = configuration;
        initialiseClient();
    }

    /**
     * Initialize Jersey Client
     */
    private void initialiseClient() {
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        this.mapper = ClientFactory.createMapper(configuration,
                                                 jacksonProvider);
        this.client = ClientFactory.create(configuration, jacksonProvider);
        this.oauthHelper = new OneDriveOAuthHelper(configuration, client);
        if (configuration.isDebugLogging()) {
            this.client.register(new LoggingFilter(new JulFacade(LOG), true));
        }
    }

    /**
     * Copy Item by itemId to a folder
     * 
     * @param ItemId String itemId
     * @param name String name of copied reference, if left empty the original
     *            name is used
     * @param parentRef ItemReference reference to parent folder (item)
     * @return URI to copied Item
     */
    public Item copyById(String itemId, String name, ItemReference parentRef) {
        Map<String, Object> map = newParentRefBody(name, parentRef);
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/items/{item-id}/action.copy")
            .resolveTemplateFromEncoded("item-id", itemId).request()
            .header("Prefer", "respond-async").post(Entity.json(map));
        handleError(response, Status.ACCEPTED,
                    "Failed to copy item: " + itemId + " to: " + parentRef);
        try {
            return pollForCompletion(response.getLocation(), itemId,
                                     "action.copy", 1, TimeUnit.SECONDS);
        } catch (URISyntaxException e) {
            throw new OneDriveException("Result URI of copy.action is invalid",
                                        e);
        }
    }

    /**
     * Copy Item by itemId to a folder
     * 
     * @param itemId String
     * @param name String name of copied reference, if left empty original name
     *            is used
     * @param parentPath String path of parent folder
     * @return Item copied folder
     */
    public Item copyById(String itemId, String name, String parentPath)
        throws URISyntaxException {
        ItemReference parentRef = new ItemReference();
        parentRef.setPath(parentPath);
        return copyById(itemId, name, parentRef);
    }

    /**
     * Copy Item identified by path to a folder
     * 
     * @param path String path that is relative to the root folder i.e.
     *            "/drive/root:/"
     * @param name String name of copied reference, if {@code null} the original
     *            name is used
     * @param parentRef ItemReference reference to parent folder (item)
     * @return Item copied folder
     */
    public Item copyByPath(String path, String name, ItemReference parentRef) {
        Map<String, Object> map = newParentRefBody(name, parentRef);
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/root:/{item-path}:/action.copy")
            .resolveTemplateFromEncoded("item-path", path).request()
            .header("Prefer", "respond-async").post(Entity.json(map));
        handleError(response, Status.ACCEPTED,
                    "Failed to copy item: " + path + " to: " + parentRef);
        try {
            return pollForCompletion(response.getLocation(), path,
                                     "action.copy", 1, TimeUnit.SECONDS);
        } catch (URISyntaxException e) {
            throw new OneDriveException("Result URI of copy.action is invalid",
                                        e);
        }
    }

    /**
     * Poll an URL for completion of an server-side asynchronous action.
     * Preferably this should done in a background task with callbacks
     * 
     * @param uri URI monitoring URI to action operation status
     * @param itemId String id of item on which action is performed
     * @param duration long duration between poll requests
     * @param unit TimeUnit duration unit
     * @return Item copied folder
     */
    private Item pollForCompletion(URI uri, String itemId, String action,
                                   long duration, TimeUnit unit)
                                       throws URISyntaxException {
        int errorcount = 5;
        while (true) {
            Response response = getClient().target(uri).request().get();
            if (equalsStatus(response, Status.ACCEPTED)) {
                AsyncOperationStatus status = response
                    .readEntity(AsyncOperationStatus.class);
                LOG.info(status.toString());
            } else if (equalsStatus(response, Status.SEE_OTHER)) {
                // 303 See Other is never returned on completion instead 200 Ok
                // with the Item as response body. Understanding is untested XXX
                LOG.info("Operation: {} for item: {} completed.", action,
                         itemId);
                return getMetadataByURI(response.getLocation());
            } else if (equalsStatus(response, Status.OK)) {
                LOG.info("Operation: {} for item: {} completed.", action,
                         itemId);
                return response.readEntity(Item.class);
            } else if (equalsStatus(response, Status.INTERNAL_SERVER_ERROR)) {
                AsyncOperationStatus status = response
                    .readEntity(AsyncOperationStatus.class);
                throw new OneDriveException(formatError(response.getStatus(),
                                                        status.toString()));
            } else {
                if (--errorcount < 0) {
                    throw new OneDriveException(formatError(response
                        .getStatus(), "Too many polling errors, aborting the polling for the completion of action: "
                                      + action + " on item:" + itemId));
                }
                LOG.debug("Poll for completion of action: {} on item: {} failed, retrying.",
                          action, itemId);
            }
            try {
                Thread.sleep(unit.toMillis(duration));
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Create a folder in the root of the drive
     * 
     * @param name String name of folder to create
     * @param behaviour ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then defaults to {@link ConflictBehavior#FAIL}
     * @return Item created folder
     */
    public Item createFolderInRoot(String name, ConflictBehavior behaviour) {
        return createFolderById(name, "", behaviour, "/drive/root/children");
    }

    /**
     * Create a folder
     * 
     * @param name String name of folder to create
     * @param parentId String id of parent folder.
     * @param behaviour ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then defaults to {@link ConflictBehavior#FAIL}
     * @return Item created folder
     */
    public Item createFolderById(String name, String parentId,
                                 ConflictBehavior behaviour) {
        return createFolderById(name, parentId, behaviour,
                                "/drive/items/{parent-id}/children");
    }

    private Item createFolderById(String name, String parentId,
                                  ConflictBehavior behaviour, String path) {
        Map<String, Object> map = newFolderBody(name, behaviour);
        Response response = getClient().target(ONEDRIVE_URL).path(path)
            .resolveTemplateFromEncoded("parent-id", parentId).request()
            .post(Entity.json(mapToJson(map)));
        handleError(response, Status.CREATED,
                    "Failure creating folder: " + name + " in parent folder: "
                                              + parentId);
        return response.readEntity(Item.class);
    }

    /**
     * Create a folder
     * 
     * @param name String name of folder to create
     * @param parentPath String path to parent folder relative to the root
     *            folder i.e. "/drive/root:/".
     * @param behaviour ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then defaults to {@link ConflictBehavior#FAIL}
     * @return Item of created folder
     */
    public Item createFolderByPath(String name, String parentPath,
                                   ConflictBehavior behaviour) {
        Map<String, Object> map = newFolderBody(name, behaviour);
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/root:/{parent-path}:/children")
            .resolveTemplateFromEncoded("parent-path", parentPath).request()
            .post(Entity.json(mapToJson(map)));
        handleError(response, Status.CREATED,
                    "Failure creating folder: " + name + " in parent folder: "
                                              + parentPath);
        return response.readEntity(Item.class);
    }

    /**
     * Create a sharing link for an Item
     * 
     * @param itemId String
     * @param type LinkType view for read-only, edit for read-write links, if
     *            {@code null} defaults to {@link LinkType#VIEW}
     * @return PermissionFacet facet with the link information
     */
    public PermissionFacet createLinkById(String itemId, LinkType type) {
        final LinkType linkType = (type == null) ? LinkType.VIEW : type;
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/items/{item-id}/action.createLink")
            .resolveTemplateFromEncoded("item-id", itemId)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.json(linkType));
        Status[] successCodes = { Status.CREATED, Status.OK };
        handleError(response, successCodes,
                    "Failure creating sharing link for item: " + itemId);
        return response.readEntity(PermissionFacet.class);
    }

    /**
     * Create a sharing link for an Item
     * 
     * @param path String path that is relative to the root folder i.e.
     *            "/drive/root:/"
     * @param type LinkType view for read-only, edit for read-write links, if
     *            {@code null} defaults to {@link LinkType#VIEW}
     * @return PermissionFacet facet with the link information
     */
    public PermissionFacet createLinkByPath(String path, LinkType type) {
        final LinkType linkType = (type == null) ? LinkType.VIEW : type;
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/root:/{item-path}:/action.createLink")
            .resolveTemplateFromEncoded("item-path", path)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.json(linkType));
        Status[] successCodes = { Status.CREATED, Status.OK };
        handleError(response, successCodes,
                    "Failure creating sharing link for item: " + path);
        return response.readEntity(PermissionFacet.class);
    }

    /**
     * Delete Item by Id
     * 
     * @param itemId String
     * @param eTag String an optional etag value of the cached item, if set this
     *            must match the etag value of remote item for deletion to
     *            succeed. If @{code null} than no etag validation is done
     */
    public void deleteById(String itemId, String eTag) {
        EntityTag tag = createEtag(eTag);
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/items/{item-id}")
            .resolveTemplateFromEncoded("item-id", itemId)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .header(HEADER_IF_MATCH, tag).delete();
        handleError(response, Status.NO_CONTENT,
                    "Failure deleting item: " + itemId);
    }

    /**
     * Delete Item by Id
     * 
     * @param itemId String
     */
    public void deleteById(String itemId) {
        deleteById(itemId, null);
    }

    /**
     * Delete Item by path
     * 
     * @param path String path to item relative to the root folder i.e.
     *            "/drive/root:/"
     * @param eTag String an optional etag value of the cached item, if set this
     *            must match the etag value of remote item for deletion to
     *            succeed. If @{code null} than no etag validation is done
     */
    public void deleteByPath(String path, String eTag) {
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/root:/{path}")
            .resolveTemplateFromEncoded("path", path).request()
            .header(HEADER_IF_MATCH, createEtag(eTag)).delete();
        handleError(response, Status.NO_CONTENT,
                    "Failure deleting item: " + path);
    }

    /**
     * Delete Item by path
     * 
     * @param path String path to item relative to the root folder i.e.
     *            "/drive/root:/"
     */
    public void deleteByPath(String path) {
        deleteByPath(path, null);
    }

    /**
     * Download Item by Id
     * <p>
     * TODO: allow for range header
     * </p>
     * 
     * @param itemId String
     * @param eTag String an optional etag value of the cached item, if set and
     *            the tag matches the upstream item an NotModifiedException is
     *            thrown. If @{code null} than no etag validation is done
     * @return OneDriveContent
     * @throws NotModifiedException if an eTag was provided and matched, meaning
     *             the Item has not changed
     */
    public OneDriveContent downloadById(String itemId, String eTag) {
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/items/{item-id}/content")
            .resolveTemplateFromEncoded("item-id", itemId).request()
            .header(HEADER_IF_NONE_MATCH, createEtag(eTag)).get();
        handleNotModified(response);
        handleError(response, Status.OK, "Failure downloading item: " + itemId);
        return response.readEntity(OneDriveContent.class);
    }

    /**
     * Download Item by path
     * <p>
     * TODO: allow for range header
     * </p>
     * 
     * @param path String path of item relative to the root folder i.e.
     *            "/drive/root:/"
     * @param eTag String an optional etag value of the cached item, if set and
     *            the tag matches the upstream item, an NotModifiedException is
     *            thrown. If @{code null} than no etag validation is done
     * @return OneDriveContent
     * @throws NotModifiedException if an eTag was provided and matched, meaning
     *             the Item has not changed
     */
    public OneDriveContent downloadByPath(String path, String eTag) {
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/root:/{item-path}:/content")
            .resolveTemplateFromEncoded("item-path", path).request()
            .header(HEADER_IF_NONE_MATCH, createEtag(eTag)).get();
        handleNotModified(response);
        handleError(response, Status.OK, "Failure downloading item: " + path);
        return response.readEntity(OneDriveContent.class);
    }

    /**
     * List children in folder by Id
     * 
     * @param path String itemId
     * @param eTag String an optional etag value of the cached item, if set and
     *            the tag matches the upstream item an NotModifiedException is
     *            thrown. If @{code null} than no etag validation is done
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned
     * @return ItemCollection
     * @throws NotModifiedException if an eTag was provided and matched, meaning
     *             the folder has not changed
     */
    public ItemIterable listChildrenById(String itemId, String eTag,
                                         QueryParameters parameters) {
        WebTarget target = getClient().target(ONEDRIVE_URL)
            .path("/drive/items/{item-id}/children")
            .resolveTemplateFromEncoded("item-id", itemId);
        if (parameters != null) {
            target = parameters.configure(target);
        }
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
            .header(HEADER_IF_NONE_MATCH, createEtag(eTag)).get();
        handleNotModified(response);
        handleError(response, Status.OK,
                    "Failed to list children for item:" + itemId);
        return response.readEntity(ItemCollection.class).setApi(this);
    }

    /**
     * List children in the root drive
     * 
     * @return ItemCollection
     */
    public ItemIterable listChildrenInRoot() {
        return listChildrenInRoot(null);
    }

    /**
     * List children in the root drive
     * 
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned
     * @return ItemCollection
     */
    public ItemIterable listChildrenInRoot(QueryParameters parameters) {
        return listChildrenByPath("", null, parameters);
    }

    /**
     * List children by URL, used to get the next item collection using
     * {@link ItemCollection#getNextLink()}
     * 
     * @param uri String
     * @return ItemIterable
     */
    public ItemIterable listChildren(String uri) {
        Response response = getClient().target(uri)
            .request(MediaType.APPLICATION_JSON_TYPE).get();
        handleError(response, Status.OK,
                    "Failed to list children by URL:" + uri);
        return response.readEntity(ItemCollection.class).setApi(this);
    }

    /**
     * List children in path
     * 
     * @param itemPath String path that is relative to the root folder i.e.
     *            "/drive/root:/"
     * @param eTag String an optional etag value of the cached item, if set and
     *            the tag matches the upstream item an NotModifiedException is
     *            thrown. If @{code null} than no etag validation is done
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned
     * @return ItemCollection
     * @throws NotModifiedException if an eTag was provided and matched, meaning
     *             the folder has not changed
     */
    public ItemIterable listChildrenByPath(String itemPath, String eTag,
                                           QueryParameters parameters) {
        return listChildrenByPath(itemPath,
                                  "/drive/root:/{item-path}:/children", eTag,
                                  parameters);
    }

    private ItemIterable listChildrenByPath(String itemPath, String path,
                                            String eTag,
                                            QueryParameters parameters) {
        WebTarget target = getClient().target(ONEDRIVE_URL).path(path)
            .resolveTemplateFromEncoded("item-path", itemPath);
        if (parameters != null) {
            target = parameters.configure(target);
        }
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
            .header(HEADER_IF_NONE_MATCH, createEtag(eTag)).get();
        handleNotModified(response);
        handleError(response, Status.OK,
                    "Failed to list children for path: " + itemPath);
        return response.readEntity(ItemCollection.class).setApi(this);
    }

    /**
     * Download by URI
     * 
     * @param uri URI to an Item or as returned in the Location header
     * @return OneDriveContent
     */
    public OneDriveContent getContent(URI uri) {
        Response response = getClient().target(uri)
            .request(MediaType.APPLICATION_OCTET_STREAM).get();
        handleError(response, Status.FOUND, "Failure getting item: " + uri);
        return response.readEntity(OneDriveContent.class);
    }

    /**
     * Get an access token.
     * 
     * @return TokenResult
     */
    TokenResult getAccessToken() {
        oauthHelper.requestAccessToken();
        return oauthHelper.getAccessToken();
    }

    /**
     * Get authorized client.
     * 
     * <pre>
     * The modifier is explicitly package local for usage by {@link OneDriveAPIResumableUpload}
     * </pre>
     * 
     * @return Client
     */
    Client getClient() {
        return oauthHelper.getClient();
    }

    /**
     * Get default Drive properties
     * 
     * @return Drive
     */
    public Drive getDefaultDrive() {
        Response response = getClient().target(ONEDRIVE_URL).path("/drive")
            .request(MediaType.APPLICATION_JSON_TYPE).get();
        handleError(response, Status.OK,
                    "Failed to get default drive properties");
        return response.readEntity(Drive.class);
    }

    /**
     * Get drive properties
     * 
     * @param driveId String identifier of the drive
     * @return Drive
     */
    public Drive getDrive(String driveId) {
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drives/{drive-id}")
            .resolveTemplateFromEncoded("drive-id", driveId)
            .request(MediaType.APPLICATION_JSON_TYPE).get();
        handleError(response, Status.OK,
                    "Failed to get properties for drive: " + driveId);
        return response.readEntity(Drive.class);
    }

    /**
     * Get metadata for an Item
     * 
     * @param itemId String
     * @param eTag String an optional etag value of the cached item, if set and
     *            the tag matches the upstream item, this metadata is not
     *            returned but instead NotModifiedException is thrown. If
     *            null/empty than no etag validation is done
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned
     * @return Item
     * @throws NotModifiedException if an eTag was provided and matched, meaning
     *             the Item has not changed, should only be catched if eTag was
     *             provided
     */
    public Item getMetadataById(String itemId, String eTag,
                                QueryParameters parameters)
                                    throws NotModifiedException {
        WebTarget target = getClient().target(ONEDRIVE_URL)
            .path("/drive/items/{item-id}")
            .resolveTemplateFromEncoded("item-id", itemId);
        if (parameters != null) {
            target = parameters.configure(target);
        }
        Response response = target.request()
            .header(HEADER_IF_NONE_MATCH, createEtag(eTag)).get();
        handleNotModified(response);
        handleError(response, Status.OK,
                    "Failure getting metadata for item: " + itemId);
        return response.readEntity(Item.class);
    }

    /**
     * Get metadata for an Item
     * 
     * @param itemId String
     * @return Item
     */
    public Item getMetadataById(String itemId) {
        return getMetadataById(itemId, null, null);
    }

    /**
     * Get metadata for an Item
     * 
     * @param Item item
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned
     * @return Item
     */
    public Item getMetadata(Item item, QueryParameters parameters) {
        try {
            return getMetadataById(item.getId(), item.geteTag(), parameters);
        } catch (NotModifiedException e) {
            return item;
        }
    }

    /**
     * Get metadata for an Item
     * 
     * @param itemPath String path that is relative to the root folder i.e.
     *            "/drive/root:/"
     * @param eTag String an optional etag value of the cached item, if set and
     *            the tag matches the upstream item, this metadata is not
     *            returned but instead NotModifiedException is thrown. If
     *            null/empty than no etag validation is done
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned
     * @return Item
     * @throws NotModifiedException if an eTag was provided and matched, meaning
     *             the Item has not changed, should only be catched if eTag was
     *             provided
     */
    public Item getMetadataByPath(String itemPath, String eTag,
                                  QueryParameters parameters)
                                      throws NotModifiedException {
        WebTarget target = getClient().target(ONEDRIVE_URL)
            .path("/drive/root:/{item-path}")
            .resolveTemplateFromEncoded("item-path", itemPath);
        if (parameters != null) {
            target = parameters.configure(target);
        }
        Response response = target.request()
            .header(HEADER_IF_NONE_MATCH, createEtag(eTag)).get();
        handleNotModified(response);
        handleError(response, Status.OK,
                    "Failure getting metadata for item: " + itemPath);
        return response.readEntity(Item.class);
    }

    /**
     * Get metadata by URI
     * 
     * @param uri URI to an Item or as returned in the Location header
     * @return Item
     */
    public Item getMetadataByURI(URI uri) {
        Response response = getClient().target(uri).request().get();
        handleError(response, Status.OK, "Failure getting item: " + uri);
        return response.readEntity(Item.class);
    }

    /**
     * Get a special folder
     * 
     * @param folder SpecialFolder
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned
     * @return Item
     */
    public Item getSpecialFolder(SpecialFolder folder,
                                 QueryParameters parameters) {
        WebTarget target = getClient().target(ONEDRIVE_URL)
            .path("/drive/special/{special-folder-name}")
            .resolveTemplateFromEncoded("special-folder-name",
                                        folder.getName());
        if (parameters != null) {
            target = parameters.configure(target);
        }
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
            .get();
        handleError(response, Status.OK,
                    "Failed to get special folder: " + folder.getName());
        return response.readEntity(Item.class);
    }

    /**
     * Move Item by itemId to a folder
     * 
     * @param ItemId String identifier of item to move
     * @param name String name of copied reference, if {@code null} the original
     *            name is used
     * @param parentRef ItemReference reference to parent folder
     * @return Item moved Item
     */
    public Item moveById(String itemId, String name, ItemReference parentRef) {
        Map<String, Object> map = newParentRefBody(name, parentRef);
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/items/{item-id}")
            .resolveTemplateFromEncoded("item-id", itemId).request()
            // https://stackoverflow.com/questions/22355235/patch-request-using-jersey-client
            .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
            .method("PATCH", Entity.json(map));
        handleError(response, Status.OK, "Failure moving item: " + itemId
                                         + " to parent folder: " + parentRef);
        return response.readEntity(Item.class);
    }

    /**
     * Move an Item to new parent path and update all writable metadata
     * properties set in the Item
     * 
     * @param ItemId String identifier of item to move
     * @param name String name of copied reference, if {@code null} the original
     *            name is used
     * @param parentPath String path of parent relative to the root folder i.e.
     *            "/drive/root:/"
     * @return Item moved Item
     */
    public Item moveByParentPath(String itemId, String name,
                                 String parentPath) {
        final String fullPath = "/drive/root:/" + parentPath;
        ItemReference parentRef = new ItemReference();
        parentRef.setPath(fullPath);
        return moveById(itemId, name, parentRef);
    }

    /**
     * Search for items matching a query
     * 
     * @param itemId String identifier of folder in which to search
     * @param query String search query
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned
     * @return ItemCollection
     */
    public ItemIterable searchById(String itemId, String query,
                                   QueryParameters parameters) {
        WebTarget target = getClient().target(ONEDRIVE_URL)
            .path("/drive/items/{item-id}/view.search")
            .resolveTemplateFromEncoded("item-id", itemId)
            .queryParam("q", URLHelper.encodeURIComponent(query));
        if (parameters != null) {
            target = parameters.configure(target, QueryParameters.EXPAND);
        }
        Response response = target.request().get();
        handleError(response, Status.OK,
                    "Failure searching for items that match query: " + query
                                         + " within item: " + itemId);
        return response.readEntity(ItemCollection.class).setApi(this);
    }

    /**
     * Search in a folder Drive for items matching a query
     * 
     * @param parentPath String path of folder to search in, if {@code null} the
     *            root drive is searched
     * @param query String search query
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned
     * @return ItemCollection results
     */
    public ItemIterable searchByPath(String parentPath, String query,
                                     QueryParameters parameters) {
        return searchByPath(parentPath, query,
                            "/drive/root:/{item-path}:/view.search",
                            parameters);
    }

    /**
     * Search in the root drive for items matching a query
     * 
     * @param query String search query
     * @param parameters QueryParameters influences the way item results are
     *            returned, if null the default listing is returned
     * @return ItemCollection results
     */
    public ItemIterable searchInRoot(String query, QueryParameters parameters) {
        return searchByPath("", query, "/drive/root/view.search", parameters);
    }

    /**
     * Search in the root drive for items matching a query
     * 
     * @param query String search query
     * @return ItemCollection results
     */
    public ItemIterable searchInRoot(String query) {
        return searchInRoot(query, null);
    }

    private ItemIterable searchByPath(String itemPath, String query,
                                      String path, QueryParameters parameters) {
        WebTarget target = getClient().target(ONEDRIVE_URL).path(path)
            .resolveTemplateFromEncoded("item-path", itemPath)
            .queryParam("q", URLHelper.encodeURIComponent(query));
        if (parameters != null) {
            target = parameters.configure(target, QueryParameters.EXPAND);
        }
        Response response = target.request().get();
        handleError(response, Status.OK,
                    "Failure searching for items that match query: " + query
                                         + " within: " + path);
        return response.readEntity(ItemCollection.class).setApi(this);
    }

    /**
     * Enumerate the sync changes for a folder for a specific stated, which can
     * be used to synchronise a local copy of the drive.
     * <p>
     * TODO Handle HTTP 410 Gone error
     * </p>
     * 
     * @param itemId String identifier of folder
     * @param token String The last token returned from the previous call to
     *            view.changes. If {@code null}, view.changes will return the
     *            current state of the drive
     * @return SyncResponse
     */
    public SyncResponse syncChangesById(String itemId, String token) {
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/items/{item-id}/view.delta")
            .queryParam("token", token)
            .resolveTemplateFromEncoded("item-id", itemId).request().get();
        handleError(response, Status.OK,
                    "Failure acquiring sync changes for folder: " + itemId);
        return (SyncResponse)response.readEntity(SyncResponse.class)
            .setApi(this);
    }

    /**
     * Enumerate the sync changes for a folder for a specific stated, which can
     * be used to synchronise a local copy of the drive.
     * 
     * @param parentPath String path that is relative to the root folder i.e.
     *            "/drive/root:/"
     * @param token String The last token returned from the previous call to
     *            view.changes. If {@code null}, view.changes will return the
     *            current state of the hierarchy.
     * @return SyncResponse
     */
    public SyncResponse syncChangesByPath(String parentPath, String token) {
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/root:/{item-path}:/view.delta")
            .resolveTemplateFromEncoded("item-path", parentPath)
            .queryParam("token", token).request().get();
        handleError(response, Status.OK,
                    "Failure acquiring sync changes for path: " + parentPath);
        return (SyncResponse)response.readEntity(SyncResponse.class)
            .setApi(this);
    }

    /**
     * Get Thumbnails
     * 
     * @param ItemId String identifier of item
     * @return ThumbnailSet
     */
    public ThumbnailSet thumbnailsById(String itemId) {
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/items/{item-id}/thumbnails")
            .resolveTemplateFromEncoded("item-id", itemId).request().get();
        handleError(response, Status.OK,
                    "Failure acquiring thumbnails for item: " + itemId);
        return response.readEntity(ThumbnailSet.class);
    }

    /**
     * Update the all writable metadata properties of an Item.
     * <p>
     * This sets parentReference to null, because updating by itemId and parent
     * Ref is not allowed
     * </p>
     * <p>
     * FIXME: update triggers a bug when then if-match header is set with a
     * bogus value, then "Malformed If-Match header" is returned
     * {@link https://github.com/OneDrive/onedrive-api-docs/issues/131}
     * </p>
     * 
     * @param eTag String an optional etag value of the cached item, if set this
     *            must match the etag value of remote item for updating to
     *            succeed. If @{code null} than no etag validation is done
     * @param item Item containing properties to update
     * @return Item updated Item
     */
    public Item update(final Item item, String eTag) {
        if (item.getParentReference() != null) {
            item.setParentReference(null);
        }
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/items/{item-id}")
            .resolveTemplateFromEncoded("item-id", item.getId()).request()
            .header(HEADER_IF_MATCH, createEtag(eTag))
            // patch method is not default available in jersey 2, so use a
            // workaround:
            // {@link
            // https://stackoverflow.com/questions/22355235/patch-request-using-jersey-client}
            .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
            .method("PATCH", Entity.json(item));
        handleError(response, Status.OK,
                    "Failure updating item: " + item.getName());
        return response.readEntity(Item.class);
    }

    /**
     * Upload an Item by path, only for files below 100MB, larger files should
     * be uploaded with uploadResumableBy*
     * 
     * <pre>
     * on uploading of a new file the statuscode 201 CREATED is returned 
     * on uploading of an existing file with ConflictBehavior.REPLACE
     *  the statuscode 200 OK is returned
     * </pre>
     * 
     * @param content OneDriveContent
     * @param parentPath String path to parent folder relative to the root
     *            folder i.e. "/drive/root:/".
     * @param behavior ConflictBehavior behaviour if a naming conflict occurs
     * @return Item representing uploaded content
     */
    public Item uploadByPath(OneDriveContent content, String parentPath,
                             ConflictBehavior behavior) {
        String conflictBehavior = (behavior == null)
            ? null : behavior.getName();

        Status[] successCodes = { Status.CREATED, Status.OK };
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/root:/{path}/{filename}:/content")
            .resolveTemplateFromEncoded("path", parentPath)
            .resolveTemplateFromEncoded("filename", content.getName())
            .queryParam("@name.conflictBehavior", conflictBehavior)
            .request(MediaType.TEXT_PLAIN)
            .put(Entity.entity(content, MediaType.APPLICATION_OCTET_STREAM));
        handleError(response, successCodes,
                    "Failure uploading file: " + content.getName() + " into: "
                                            + parentPath);
        return response.readEntity(Item.class);
    }

    /**
     * Upload an Item by path
     * 
     * <pre>
     * Defaults to ConflictBehavior.RENAME, on a naming conflict the creating fails.
     * </pre>
     * 
     * @param content OneDriveContent
     * @param parentPath String path to parent folder relative to the root
     *            folder i.e. "/drive/root:/". If null/empty the root folder is
     *            assumed.
     * @return representing uploaded content
     */
    public Item uploadByPath(OneDriveContent content, String parentPath) {
        return uploadByPath(content, parentPath, null);
    }

    /**
     * Upload an Item by parentId
     * 
     * <pre>
     * on uploading of a new file statuscode 201 CREATED is returned 
     * on uploading of an existing file with ConflictBehavior.REPLACE
     *  statuscode 200 OK is returned
     * </pre>
     * 
     * @param content OneDriveContent
     * @param parentId String
     * @param behavior ConflictBehavior behaviour if a naming conflict occurs
     * @return Item represented uploaded content
     */
    public Item uploadByParentId(OneDriveContent content, String parentId,
                                 ConflictBehavior behavior) {

        if (OneDriveAPIResumableUpload.shouldUploadAsLargeContent(content)) {
            return uploadResumableByParent((OneDriveFile)content, parentId,
                                           behavior);
        }

        String conflictBehavior = (behavior == null)
            ? null : behavior.getName();

        Status[] successCodes = { Status.CREATED, Status.OK };
        Response response = getClient().target(ONEDRIVE_URL)
            .path("/drive/items/{parent-id}:/{filename}:/content")
            .resolveTemplateFromEncoded("parent-id", parentId)
            .resolveTemplateFromEncoded("filename", content.getName())
            .queryParam("@name.conflictBehavior", conflictBehavior)
            .request(MediaType.TEXT_PLAIN)
            .put(Entity.entity(content, MediaType.APPLICATION_OCTET_STREAM));
        handleError(response, successCodes,
                    "Failure uploading file: " + content.getName()
                                            + " into folder: " + parentId);
        return response.readEntity(Item.class);
    }

    /**
     * 
     * <pre>
     * 1. retry on 500, 502, 503, 504 with exponential backoff strategy: done
     * 2. for other errors use a retry counter with maximum: done
     * 3. on 404, restart upload entirely: done
     * 4. ranges should be rounded by 320K: done
     * 
     * TODO
     * Handle commit errors
     * </pre>
     * 
     * @param content OneDriveContent to upload
     * @param parentId String
     * @param behavior ConflictBehavior behaviour if a naming conflict occurs
     * @return Item uploaded content
     */
    public Item uploadResumableByParent(OneDriveFile content, String parentId,
                                        ConflictBehavior behavior) {
        OneDriveAPIResumableUpload uploader = new OneDriveAPIResumableUpload(this);
        UploadSession session = uploader.createSessionById(content, parentId,
                                                           behavior);
        try {
            return uploader.uploadFragments(content, session);
        } catch (IOException e) {
            throw new OneDriveException("Failure uploading of file: "
                                        + content.getName()
                                        + ", file does not exist");
        } catch (OneDriveResumableUploadException e) {
            uploader.cancelSession(session);
            throw new OneDriveException("Failure uploading of file: "
                                        + content.getName()
                                        + ", session must be restarted", e);
        }
    }

    /**
     * Closes the client and all webTargets
     */
    public void close() {
        client.close();
    }

    /**
     * Logout from OneDrive API
     */
    public void logOut() {
        oauthHelper.logOut();
    }

    /**
     * Handles a error if successCode is not returned
     * 
     * @param response Response
     * @param successCode Status indicating success response
     * @param errorMessage String in case of failure
     */
    void handleError(final Response response, Status successStatus,
                     String errorMessage) {
        if (equalsStatus(response, successStatus) == false) {
            OneDriveError e = response.readEntity(OneDriveError.class);
            throw new OneDriveException(formatError(response.getStatus(),
                                                    errorMessage),
                                        e);
        }
    }

    /**
     * Handles a error if one of the successCode is not returned
     * 
     * @param response Response
     * @param successCodes Status[] status indicating success response
     * @param errorMessage String in case of failure
     */
    void handleError(final Response response, Status[] successCodes,
                     String errorMessage) {
        if (equalsStatus(response, successCodes)) {
            return;
        }
        OneDriveError e = response.readEntity(OneDriveError.class);
        throw new OneDriveException(formatError(response.getStatus(),
                                                errorMessage),
                                    e);
    }

    /**
     * Handle a possible 304 Not Modified status code if an eTag was provided in
     * the request and matched the upstream value.
     * 
     * @param response
     * @throws NotModifiedException if 304 Not Modified is returned
     */
    void handleNotModified(Response response) throws NotModifiedException {
        if (response.getStatus() == Status.NOT_MODIFIED.getStatusCode()) {
            throw new NotModifiedException();
        }
    }

    /**
     * Determine if statusCode of Response equals a status
     * 
     * @param response Response
     * @param Status status
     * @return true if equal
     */
    boolean equalsStatus(final Response response, Status status) {
        return response.getStatus() == status.getStatusCode();
    }

    /**
     * Determine if statusCode of Response equals one of the status codes
     * 
     * @param response Response
     * @param successCodes Status[]
     * @return true if equal
     */
    boolean equalsStatus(final Response response, Status[] successCodes) {
        for (Status code : successCodes) {
            if (response.getStatus() == code.getStatusCode()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Format a status code and error message
     * 
     * @param status int code
     * @param message String
     * @return String
     */
    String formatError(int status, String message) {
        throw new OneDriveException(message + ", reason: " + status + " "
                                    + ErrorCodes.getMessage(status));
    }

    /**
     * Maps a Map<String, Object> to JSON
     * 
     * @param map Map<String, Object>
     * @return String json
     */
    String mapToJson(final Map<String, Object> map) {
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new OneDriveException("Failure mapping to JSON", e);
        }
    }

    /**
     * Create parentRef POST body
     * 
     * @param name String
     * @param parentRef ItemReference
     * @return Map<String, Object>
     */
    private Map<String, Object> newParentRefBody(String name,
                                                 ItemReference parentRef) {
        Map<String, Object> map = new HashMap<>();
        map.put("parentReference", parentRef);
        if (name != null && !name.isEmpty()) {
            map.put("name", name);
        }
        return map;
    }

    /**
     * Create createFolder POST body
     * 
     * @param name String
     * @param behaviour ConflictBehavior
     * @return Map<String, Object>
     */
    private Map<String, Object> newFolderBody(String name,
                                              ConflictBehavior behaviour) {
        String conflictBehavior = (behaviour == null)
            ? ConflictBehavior.FAIL.getName() : behaviour.getName();
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("folder", new FolderFacet());
        map.put("@name.conflictBehavior", conflictBehavior);
        return map;
    }

    /**
     * Create an EntityTag
     * 
     * @param etag String value
     * @return null if etag is null or empty
     */
    private EntityTag createEtag(String etag) {
        return (etag == null || etag.isEmpty()) ? null : new EntityTag(etag);
    }
}
