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

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

public class ISO8061Test {

    private static final String ISO8601_DATETIME = "2015-01-01T12:00:00.10+0000";

    private static final String ISO8601_DATETIME_EXP = "2015-01-01T12:00:00.010Z";

    private static final long ISO8601_DATETIME_MS = 1420113600010L;

    private Calendar cal;

    @Before
    public void setUp() {
        cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("Z"));
        cal.setTimeInMillis(ISO8601_DATETIME_MS);
    }

    @Test
    public void testFromCalendar() {
        String iso8601 = ISO8061.fromCalendar(cal);
        assertEquals(ISO8601_DATETIME_EXP, iso8601);

    }

    @Test
    public void testToCalendar() throws ParseException {
        Calendar c = ISO8061.toCalendar(ISO8601_DATETIME);
        assertEquals(cal, c);
    }

    @Test
    public void testToMS() throws ParseException {
        assertEquals(ISO8601_DATETIME_MS, ISO8061.toMS(ISO8601_DATETIME));
    }
}
