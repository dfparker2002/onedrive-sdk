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

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import io.yucca.microsoft.onedrive.io.Range;

public class RangeTest {

    @Test
    public void testRange() {
        Set<Range> ranges = Range.getRanges(26, 67);
        assertEquals(3, ranges.size());
        Iterator<Range> iter = ranges.iterator();

        Range range1 = iter.next();
        assertEquals(0, range1.getLower());
        assertEquals(25, range1.getUpper());
        assertEquals(26, range1.getLength());
        assertEquals(67, range1.getTotal());

        Range range2 = iter.next();
        assertEquals(26, range2.getLower());
        assertEquals(51, range2.getUpper());
        assertEquals(26, range2.getLength());
        assertEquals(67, range2.getTotal());

        Range range3 = iter.next();
        assertEquals(52, range3.getLower());
        assertEquals(66, range3.getUpper());
        assertEquals(15, range3.getLength());
        assertEquals(67, range3.getTotal());
    }

    @Test
    public void testRangeSizeGreater() {
        Set<Range> ranges = Range.getRanges(126, 67);
        assertEquals(1, ranges.size());
        Iterator<Range> iter = ranges.iterator();

        Range range1 = iter.next();
        assertEquals(0, range1.getLower());
        assertEquals(66, range1.getUpper());
        assertEquals(67, range1.getLength());
        assertEquals(67, range1.getTotal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeSizeZero() {
        Range.getRanges(0, 67);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeLengthZero() {
        Range.getRanges(126, 0);
    }
    
}
