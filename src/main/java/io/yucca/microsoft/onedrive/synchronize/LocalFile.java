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

import java.io.FileNotFoundException;

import io.yucca.microsoft.onedrive.OneDriveFile;

/**
 * LocalFile acts as a local replica of a file stored in OneDrive.
 * 
 * @author yucca.io
 */
public interface LocalFile extends LocalItem {

    /**
     * Get the file contents
     * 
     * @return OneDriveFile content
     * @throws FileNotFoundException if file does not exists
     */
    OneDriveFile getContent() throws FileNotFoundException;

}
