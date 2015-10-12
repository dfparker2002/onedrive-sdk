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

import java.net.MalformedURLException;
import java.net.URI;

import org.junit.Test;

import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.addressing.URLAddress;
import io.yucca.microsoft.onedrive.resources.Item;

public class UploadFromURLIT extends AbstractActionIT {

    @Test
    public void testUploadFromURL() throws MalformedURLException {
        URLAddress address = new URLAddress("https://dev.onedrive.com/");
        ItemAddress parentAddress = new PathAddress(TestMother.FOLDER_APITEST);
        UploadFromURLAction action = new UploadFromURLAction(api, address,
                                                             "onedrive.html",
                                                             parentAddress);
        URI jobLocation = action.call();
        assertNotNull(jobLocation);
        PollAction pollAction = new PollAction(api, jobLocation, address,
                                               UploadFromURLAction.ACTION);
        Item uploaded = pollAction.call();
        assertNotNull(uploaded);
    }

}
