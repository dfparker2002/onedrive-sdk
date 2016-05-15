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
package io.yucca.microsoft.onedrive.resources;

/**
 * AsyncOperationStatus
 *
 * @author yucca.io
 */
public class AsyncOperationStatus {

    private String operation;

    private String percentageComplete;

    private OperationStatus status;

    private String statusDescription;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPercentageComplete() {
        return percentageComplete;
    }

    public void setPercentageComplete(String percentageComplete) {
        this.percentageComplete = percentageComplete;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }

    public boolean isDone() {
        return "completed".equals(this.status);
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    @Override
    public String toString() {
        return "Operation: " + this.operation + ", percentageComplete = "
               + this.percentageComplete + ", status: " + this.status.getName()
               + ", description: " + this.statusDescription;
    }
}
