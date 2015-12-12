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
 * LogicalCriteria, represents a logical (and/or) filter expression
 *
 * @author yucca.io
 */
public class LogicalCriteria implements Criteria {

    private final Criteria criteria;

    private final Criteria otherCriteria;

    private final LogicalOperator operator;

    /**
     * Constructor
     * 
     * @param criteria Criteria left criteria in logical expression
     * @param otherCriteria right criteria in logical expression
     * @param operator LogicalOperator used for logical expression
     */
    LogicalCriteria(Criteria criteria, Criteria otherCriteria,
                    LogicalOperator operator) {
        this.criteria = criteria;
        this.otherCriteria = otherCriteria;
        this.operator = operator;
    }

    /**
     * @return Criteria the criteria.
     */
    public Criteria getCriteria() {
        return criteria;
    }

    /**
     * @return Criteria the otherCriteria.
     */
    public Criteria getOtherCriteria() {
        return otherCriteria;
    }

    /**
     * @return Criteria the operator.
     */
    public LogicalOperator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return criteria.toString() + " " + operator.getOperator() + " "
               + otherCriteria.toString();
    }

}
