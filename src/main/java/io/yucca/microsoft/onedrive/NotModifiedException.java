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

/**
 * NotModifiedException indicates that a result is unmodified.
 * <p>
 * This is used by eTag header matching of the upstream content. This explicitly
 * extends RuntimeException and should only be thrown and catched if a OneDrive
 * API request is made containing an eTag value.
 * </p>
 * 
 * @author yucca.io
 */
public class NotModifiedException extends RuntimeException {

    private static final long serialVersionUID = -3509774303053612395L;

}
