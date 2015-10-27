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

import java.io.IOException;
import java.nio.file.Path;

/**
 * LocalFolder acts as a local replica of a folder stored in OneDrive.
 * 
 * @author yucca.io
 */
public interface LocalFolder extends LocalItem {

    /**
     * Determine if this folder is the local root folder
     * 
     * @return boolean
     * @throws IOException
     */
    boolean isLocalRoot() throws IOException;

    /**
     * Create a absolute path where filename is resolved as a child of this
     * folder
     * 
     * @param filename String
     * @return Path absolute path to the child
     */
    Path resolve(String filename);

}
