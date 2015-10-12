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

/**
 * FilterCriteria represent a single filter expression
 * 
 * @author yucca.io
 */
public class FilterCriteria implements Criteria {

    private final String field;

    private final FilterOperator operator;

    private final String value;

    /**
     * Constructor
     * 
     * @param field String field name used in expression
     * @param operator FilterOperator used operator in expression
     * @param value String value in expression, textual values must be quoted
     *            like 'London'
     */
    FilterCriteria(String field, FilterOperator operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String toString() {
        return field + " " + operator.getOperator() + " " + value + "";
    }

    /**
     * NOTEQUAL
     * 
     * @param field String field name used in expression
     * @param value String value in expression, textual values must be quoted
     *            like 'London'
     * @return FilterCriteria
     */
    public static FilterCriteria NOTEQUAL(String field, String value) {
        return new FilterCriteria(field, FilterOperator.NOTEQUAL, value);
    }

    /**
     * EQUAL
     * 
     * @param field String field name used in expression
     * @param value String value in expression, textual values must be quoted
     *            like 'London'
     * @return FilterCriteria
     */
    public static FilterCriteria EQUAL(String field, String value) {
        return new FilterCriteria(field, FilterOperator.EQUAL, value);
    }

    /**
     * GREATERTHAN
     * 
     * @param field String field name used in expression
     * @param value String value in expression, textual values must be quoted
     *            like 'London'
     * @return FilterCriteria
     */
    public static FilterCriteria GREATERTHAN(String field, String value) {
        return new FilterCriteria(field, FilterOperator.GREATERTHAN, value);
    }

    /**
     * GREATEROREQUAL
     * 
     * @param field String field name used in expression
     * @param value String value in expression, textual values must be quoted
     *            like 'London'
     * @return FilterCriteria
     */
    public static FilterCriteria GREATEROREQUAL(String field, String value) {
        return new FilterCriteria(field, FilterOperator.GREATEROREQUAL, value);
    }

    /**
     * LESSTHAN
     * 
     * @param field String field name used in expression
     * @param value String value in expression, textual values must be quoted
     *            like 'London'
     * @return FilterCriteria
     */
    public static FilterCriteria LESSTHAN(String field, String value) {
        return new FilterCriteria(field, FilterOperator.LESSTHAN, value);
    }

    /**
     * LESSOREQUAL
     * 
     * @param field String field name used in expression
     * @param value String value in expression, textual values must be quoted
     *            like 'London'
     * @return FilterCriteria
     */
    public static FilterCriteria LESSOREQUAL(String field, String value) {
        return new FilterCriteria(field, FilterOperator.LESSOREQUAL, value);
    }
}
