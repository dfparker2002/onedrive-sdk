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
package io.yucca.microsoft.onedrive.resources;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import io.yucca.microsoft.onedrive.resources.OneDriveError.InnerError;

public class OneDriveErrorTest {

    private OneDriveError error;

    @Before
    public void setUp() {
        InnerError root = new InnerError("notAllowed",
                                         "The action is not allowed by the system.");
        InnerError inner = new InnerError("accessDenied",
                                          "The caller doesn't have permission to perform the action.");
        root.setInnererror(inner);
        error = new OneDriveError(root);
    }

    @Test
    public void testEqualsErrorRoot() {
        assertTrue(error.equalsError(ErrorCodes.NOT_ALLOWED));
    }

    @Test
    public void testEqualsErrorInner() {
        assertTrue(error.equalsError(ErrorCodes.ACCESDENIED));
    }

    @Test
    public void testNotEqualsError() {
        assertFalse(error.equalsError(ErrorCodes.ACTIVITY_LIMIT_REACHED));
    }

}
