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
package io.yucca.microsoft.onedrive.util;

import org.slf4j.Logger;

/**
 * JulFacade facade to log Java logging via a slf4j Logger
 * 
 * @author yucca.io
 */
public class JulFacade extends java.util.logging.Logger {

    private final Logger LOG;

    private boolean debug = true;

    public JulFacade(Logger LOG) {
        super(LOG.getName(), null);
        this.LOG = LOG;
    }

    public JulFacade(Logger LOG, boolean debug) {
        super(LOG.getName(), null);
        this.debug = debug;
        this.LOG = LOG;
    }

    @Override
    public void info(String msg) {
        if (debug) {
            LOG.info(msg);
        }
    }
}
