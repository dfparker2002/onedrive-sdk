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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.yucca.microsoft.onedrive.util.SimpleBackOffWaitStrategy;

public class SimpleBackOffWaitStrategyTest {

    private SimpleBackOffWaitStrategy ws;
    
    @Test
    public void testSleep() {
        this.ws = new SimpleBackOffWaitStrategy();
        assertEquals(2000, ws.sleep());
        assertEquals(4000, ws.sleep());
        assertEquals(8000, ws.sleep());
    }

    @Test
    public void testSleep2000() {
        this.ws = new SimpleBackOffWaitStrategy(2000);
        assertEquals(4000, ws.sleep());
        assertEquals(8000, ws.sleep());
    }
    
    @Test
    public void testReset() {
        this.ws = new SimpleBackOffWaitStrategy();
        assertEquals(2000, ws.sleep());
        ws.reset();
        assertEquals(1000, ws.getDuration());
    }
}
