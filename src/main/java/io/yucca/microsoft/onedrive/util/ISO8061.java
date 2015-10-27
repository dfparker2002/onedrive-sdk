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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * ISO8061
 * 
 * @author yucca.io
 */
public final class ISO8061 {

    private ISO8061() {
    }

    /**
     * Convert Calendar to ISO 8601 String in format
     * "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
     * 
     * @param calendar Calendar
     * @return String
     */
    public static String fromCalendar(final Calendar calendar) {
        calendar.setTimeZone(TimeZone.getTimeZone("Z"));
        return String.format("%tFT%<tT.%<tLZ", calendar, calendar, calendar);
    }

    /**
     * Convert ms (epoch) to ISO 8601 String in format
     * "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
     * 
     * @param calendar Calendar
     * @return String
     */
    public static String fromMillis(long ms) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Z"));
        calendar.setTimeInMillis(ms);
        return String.format("%tFT%<tT.%<tLZ", calendar, calendar, calendar);
    }

    /**
     * Get current date and time formatted as ISO 8601 String
     * 
     * @return String
     */
    public static String now() {
        return fromCalendar(GregorianCalendar.getInstance());
    }

    /**
     * Convert ISO 8601 String to Calendar
     * 
     * @param iso8601 String in format "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
     * @return Calendar
     * @throws ParseException if parsing fails
     */
    public static Calendar toCalendar(final String iso8601)
        throws ParseException {
        String s = iso8601.replace("Z", "+0000");
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(s);
        Calendar calendar = GregorianCalendar
            .getInstance(TimeZone.getTimeZone("Z"));
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Convert ISO 8601 String to MS
     * 
     * @param iso8601 String in format "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
     * @return long time in milliseconds
     * @throws ParseException if parsing fails
     */
    public static long toMS(final String iso8601) throws ParseException {
        return toCalendar(iso8601).getTimeInMillis();
    }

}
