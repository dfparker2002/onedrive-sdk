package io.yucca.microsoft.onedrive.filter;
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

/**
 * Operators used for logical filter expression
 *
 * @author yucca.io
 */
public enum LogicalOperator {

    AND("and"), OR("or");

    private String operator;

    private LogicalOperator(String operator) {
        this.operator = operator;
    }

    /**
     * @return Returns the operator.
     */
    public String getOperator() {
        return operator;
    }
}
