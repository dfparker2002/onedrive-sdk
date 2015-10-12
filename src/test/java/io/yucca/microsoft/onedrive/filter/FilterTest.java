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
package io.yucca.microsoft.onedrive.filter;

import static org.junit.Assert.*;

import org.junit.Test;

public class FilterTest {

    private FilterCriteria price = FilterCriteria.GREATERTHAN("price", "5");

    private FilterCriteria city = FilterCriteria.EQUAL("city", "'Rotterdam'");

    private FilterCriteria color = FilterCriteria.NOTEQUAL("color", "'Green'");

    private FilterCriteria amount = FilterCriteria.LESSTHAN("amount", "100");

    private LogicalCriteria cityPriceAnd = new LogicalCriteria(price, city,
                                                               LogicalOperator.AND);

    private LogicalCriteria colorAmountAnd = new LogicalCriteria(color, amount,
                                                                 LogicalOperator.AND);

    private GroupCriteria colorAmountGroup = new GroupCriteria(colorAmountAnd);

    private GroupCriteria cityPriceGroup = new GroupCriteria(cityPriceAnd);

    @Test
    public void testFilterCriteria() {
        assertEquals("price gt 5", price.toString());
    }

    @Test
    public void testLogicalAndCriteria() {
        assertEquals("price gt 5 and city eq 'Rotterdam'",
                     cityPriceAnd.toString());
    }

    @Test
    public void testLogicalNestedCriteria() {
        LogicalCriteria nested = new LogicalCriteria(cityPriceAnd,
                                                     colorAmountAnd,
                                                     LogicalOperator.AND);
        assertEquals("price gt 5 and city eq 'Rotterdam' and color ne 'Green' and amount lt 100",
                     nested.toString());
    }

    @Test
    public void testLogicalNestedGroupCriteria() {
        LogicalCriteria nestedGroup = new LogicalCriteria(cityPriceAnd,
                                                          colorAmountGroup,
                                                          LogicalOperator.OR);

        assertEquals("price gt 5 and city eq 'Rotterdam' or (color ne 'Green' and amount lt 100)",
                     nestedGroup.toString());
    }

    @Test
    public void testGroupOr() {
        LogicalCriteria groupedOr = new LogicalCriteria(cityPriceGroup,
                                                        colorAmountGroup,
                                                        LogicalOperator.OR);
        assertEquals("(price gt 5 and city eq 'Rotterdam') or (color ne 'Green' and amount lt 100)",
                     groupedOr.toString());
    }

    @Test
    public void testBuilder() {
        Filter filter = Filter.Builder.filterBy(color).and(city).or(amount)
            .end();
        assertNotNull(filter);
        assertEquals("color ne 'Green' and city eq 'Rotterdam' or amount lt 100",
                     filter.toString());
    }

    @Test
    public void testBuilderGroupingAnd() {
        Filter filter = Filter.Builder.group(price).and(city).end().end();
        assertNotNull(filter);
        assertEquals("(price gt 5 and city eq 'Rotterdam')", filter.toString());
    }

    @Test
    public void testBuilderGroupingOr() {
        Filter filter = Filter.Builder.group(price).or(city).end().end();
        assertNotNull(filter);
        assertEquals("(price gt 5 or city eq 'Rotterdam')", filter.toString());
    }

    @Test
    public void testBuilderLogicalOrGrouping() {
        Filter filter = Filter.Builder.filterBy(price).and(city).orGroup(color)
            .and(amount).end().end();
        assertNotNull(filter);
        assertEquals("price gt 5 and city eq 'Rotterdam' or (color ne 'Green' and amount lt 100)",
                     filter.toString());
    }

    @Test
    public void testBuilderLogicalAndGrouping() {
        Filter filter = Filter.Builder.filterBy(price).and(city).andGroup(color)
            .and(amount).end().end();
        assertNotNull(filter);
        assertEquals("price gt 5 and city eq 'Rotterdam' and (color ne 'Green' and amount lt 100)",
                     filter.toString());
    }
}
