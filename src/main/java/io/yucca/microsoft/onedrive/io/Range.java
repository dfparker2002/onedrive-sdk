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
package io.yucca.microsoft.onedrive.io;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A range defining a fragment of a file upload or read
 * 
 * @author yucca.io
 */
public class Range {

    private final long lower;

    private final long upper;

    private final long total;

    /**
     * Constructor
     * 
     * @param lower long lower range boundary
     * @param upper long upper range boundary
     * @param total long total length of file
     */
    public Range(long lower, long upper, long total) {
        if (lower < 0 || upper < 0 || lower >= upper || lower > total
            || upper > total) {
            throw new IllegalArgumentException("Range is invalid");
        }
        this.lower = lower;
        this.upper = upper;
        this.total = total;
    }

    public long getLower() {
        return lower;
    }

    public long getUpper() {
        return upper;
    }

    public long getLength() {
        return (lower == 0) ? upper + 1 : upper - lower + 1;
    }

    public long getTotal() {
        return total;
    }

    /**
     * Format this range for usage in 'Content-Length' header
     * 
     * @return String
     */
    public String getContentRangeHeader() {
        final StringBuilder s = new StringBuilder();
        return s.append("bytes ").append(String.valueOf(lower)).append("-")
            .append(String.valueOf(upper)).append("/")
            .append(String.valueOf(total)).toString();
    }

    /**
     * Get a set of Ranges defining file/stream fragments for uploading
     * 
     * <pre>
     * Example size: 25, length 67 results in following ranges
     * Range:  0-25, 26, 67
     * Range: 26-51, 26, 67
     * Range: 52-66, 15, 67
     * </pre>
     * 
     * @param size long maximum size of a range
     * @param length long total length of file or stream
     * @return Set<Range> LinkedHashSet
     */
    public static Set<Range> getRanges(long size, long length) {
        if (size <= 0) {
            throw new IllegalArgumentException("size is invalid: " + size);
        }
        if (length <= 0) {
            throw new IllegalArgumentException("length is invalid: " + length);
        }
        Set<Range> ranges = new LinkedHashSet<>();
        long lower = 0;
        long upper = 0;
        while (upper < (length - 1)) {
            if (lower == 0 && upper == 0) {
                // initial range
                // if size is smaller than length use size, otherwise use length
                // meaning all contents fits in one range
                if (size < length) {
                    upper = size - 1;
                } else {
                    upper = length - 1;
                }
            } else if ((upper + size) < length) {
                // inner ranges
                lower += size;
                upper += size;
            } else {
                // remainder range
                lower += size;
                upper += (length % upper) - 1;
            }
            ranges.add(new Range(lower, upper, length));
        }
        return ranges;
    }
}
