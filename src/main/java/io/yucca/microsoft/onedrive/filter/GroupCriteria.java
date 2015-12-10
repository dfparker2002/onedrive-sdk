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

import io.yucca.microsoft.onedrive.filter.Filter.Builder;

/**
 * GroupCriteria, represent a grouping filter expression
 *
 * @author yucca.io
 */
public class GroupCriteria implements Criteria {

    public final Criteria criteria;

    /**
     * Constructor
     * 
     * @param criteria Criteria grouped criteria
     */
    GroupCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public String toString() {
        return "(" + criteria.toString() + ")";
    }

    /**
     * GroupBuilder
     *
     * @author yucca.io
     */
    public static class GroupBuilder {

        protected Criteria criteria;

        private GroupBuilder(Criteria criteria) {
            this.criteria = criteria;
        }

        /**
         * Get the builder for creating a grouped criteria expression like:
         * (price ne '5' and city eq 'Rotterdam')
         * 
         * @param criteria Criteria initial criteria in the group
         * @return GroupBuilder
         */
        static GroupBuilder group(Criteria criteria) {
            return new GroupBuilder(criteria);
        }

        /**
         * Add a criteria by a logical 'and'
         * 
         * @param andCriteria Criteria
         * @return Builder
         */
        public GroupBuilder and(Criteria andCriteria) {
            this.criteria = new LogicalCriteria(criteria, andCriteria,
                                                LogicalOperator.AND);
            return this;
        }

        /**
         * Add a criteria by a logical 'or'
         * 
         * @param orCriteria Criteria
         * @return Builder
         */
        public GroupBuilder or(Criteria orCriteria) {
            this.criteria = new LogicalCriteria(criteria, orCriteria,
                                                LogicalOperator.OR);
            return this;
        }

        /**
         * End the grouping expression
         * 
         * @return Builder
         */
        public Builder end() {
            return new Filter.Builder(new GroupCriteria(criteria));
        }

    }

    /**
     * LogicalGroupBuilder
     * 
     * @author yucca.io
     */
    public static class LogicalGroupBuilder extends GroupBuilder {

        private final Criteria logicalCriteria;

        private final LogicalOperator operator;

        private LogicalGroupBuilder(Criteria logicalCriteria,
                                    Criteria groupCriteria,
                                    LogicalOperator operator) {
            super(groupCriteria);
            this.logicalCriteria = logicalCriteria;
            this.operator = operator;
        }

        /**
         * Get the builder for creating a grouped criteria used in a logical
         * expression like: amount le '100' and (price ne '5' and city eq
         * 'Rotterdam')
         * 
         * @param criteria logicalCriteria left criteria in logical expression
         * @param criteria Criteria right grouped criteria in logical expression
         * @return GroupBuilder
         */
        static LogicalGroupBuilder group(Criteria logicalCriteria,
                                         Criteria criteria,
                                         LogicalOperator operator) {
            return new LogicalGroupBuilder(logicalCriteria, criteria, operator);
        }

        /**
         * End the grouping expression
         * 
         * @return Builder
         */
        public Builder end() {
            return new Filter.Builder(new LogicalCriteria(logicalCriteria,
                                                          new GroupCriteria(criteria),
                                                          operator));
        }
    }
}
