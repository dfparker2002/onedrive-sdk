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
 * BasicBackOffWaitStrategy, defaults to one second and increases
 * exponentionally on each call to sleep()
 *
 * @author yucca.io
 */
public class SimpleBackOffWaitStrategy {

    private static final long DEFAULT_INITIAL = 1000L;

    private final long initialDuration;

    private long duration;

    private TimeUnit unit = TimeUnit.MILLISECONDS;

    /**
     * Constructor
     */
    public SimpleBackOffWaitStrategy() {
        this.initialDuration = DEFAULT_INITIAL;
        this.duration = DEFAULT_INITIAL;
    }

    /**
     * Constructor
     * 
     * @param duration long initially sleep time in ms
     */
    public SimpleBackOffWaitStrategy(long duration) {
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
            unit.sleep(duration);
        } catch (InterruptedException e) {
            return duration;
        }
        return increase();
    }

    private long increase() {
        duration += duration;
        return duration;
    }

    /**
     * Reset to initial duration
     */
    public void reset() {
        duration = initialDuration;
    }

    /**
     * Get next duration;
     * 
     * @return long next duration in ms
     */
    public long getDuration() {
        return duration;
    }

}
