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

import io.yucca.microsoft.onedrive.filter.GroupCriteria.GroupBuilder;

/**
 * Filter representation to filter a collection of {@link Items}
 * 
 * @author yucca.io
 */
public class Filter {

    private final Criteria criteria;

    private Filter(Criteria criteria) {
        this.criteria = criteria;
    }

    /**
     * @return Returnst the filter criteria as a String expression
     */
    @Override
    public String toString() {
        return criteria.toString();
    }

    /**
     * Builder for a filter query parameter
     *
     * @author yucca.io
     */
    public static class Builder {

        private final Criteria criteria;

        Builder(Criteria criteria) {
            this.criteria = criteria;
        }

        /**
         * Filter by criteria
         * 
         * @param criteria Criteria
         * @return Builder
         */
        public static Builder filterBy(Criteria criteria) {
            return new Builder(criteria);
        }

        /**
         * Filter by criteria
         * 
         * @param groupCriteria Criteria
         * @return Builder
         */
        public static GroupBuilder group(Criteria groupCriteria) {
            return GroupCriteria.GroupBuilder.group(groupCriteria);
        }

        /**
         * Add a criteria by a logical 'and'
         * 
         * @param andCriteria Criteria
         * @return Builder
         */
        public Builder and(Criteria andCriteria) {
            return new Builder(new LogicalCriteria(criteria, andCriteria,
                                                   LogicalOperator.AND));
        }

        /**
         * Add a criteria by a logical 'or'
         * 
         * @param orCriteria Criteria
         * @return Builder
         */
        public Builder or(Criteria orCriteria) {
            return new Builder(new LogicalCriteria(criteria, orCriteria,
                                                   LogicalOperator.OR));
        }

        /**
         * Add a grouping criteria expression by a logical 'and'
         * 
         * @param andCriteria Criteria
         * @return Builder
         */
        public GroupBuilder andGroup(Criteria groupCriteria) {
            return GroupCriteria.LogicalGroupBuilder
                .group(criteria, groupCriteria, LogicalOperator.AND);
        }

        /**
         * Add a grouping criteria expression by a logical 'or'
         * 
         * @param andCriteria Criteria
         * @return Builder
         */
        public GroupBuilder orGroup(Criteria groupCriteria) {
            return GroupCriteria.LogicalGroupBuilder
                .group(criteria, groupCriteria, LogicalOperator.OR);
        }

        /**
         * Get the complete filter expression
         * 
         * @return Filter
         */
        public Filter end() {
            return new Filter(criteria);
        }
    }

}
