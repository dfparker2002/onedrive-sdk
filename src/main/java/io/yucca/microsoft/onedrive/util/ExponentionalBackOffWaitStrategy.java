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

import java.util.concurrent.TimeUnit;

/**
 * ExponentionalBackOffWaitStrategy, defaults to one second and increases
 * exponentionally on each call to sleep()
 * <p>
 * TODO add support for definable TimeUnit
 * </p>
 *
 * @author yucca.io
 */
public class ExponentionalBackOffWaitStrategy {

    private long initialDuration = 1000;

    private long duration = 1000;

    private TimeUnit unit = TimeUnit.MILLISECONDS;

    /**
     * Constructor
     */
    public ExponentionalBackOffWaitStrategy() {
    }

    /**
     * Constructor
     * 
     * @param duration long duration in ms to sleep initially
     */
    public ExponentionalBackOffWaitStrategy(long duration) {
        this.initialDuration = duration;
        this.duration = duration;
    }

    /**
     * Sleep
     * 
     * @return long next duration in ms
     */
    public long sleep() {
        try {
            this.unit.sleep(duration);
        } catch (InterruptedException e) {
        }
        this.duration = (duration == 1000)
            ? 2000 : duration * (duration / 1000);
        return this.duration;
    }

    /**
     * Reset to initial duration
     */
    public void reset() {
        this.duration = this.initialDuration;
    }

    /**
     * Get next duration;
     * 
     * @return long next duration in ms
     */
    public long getDuration() {
        return this.duration;
    }

}
