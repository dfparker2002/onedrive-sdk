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
package io.yucca.microsoft.onedrive.facets;

/**
 * PhotoFacet
 * 
 * @author yucca.io
 */
public class PhotoFacet {

    private String takenDateTime;
    private String cameraMake;
    private String cameraModel;
    private Float fNumber;
    private Float exposureDenominator;
    private Float exposureNumerator;
    private Float focalLength;
    private Float iso;

    public String getTakenDateTime() {
        return takenDateTime;
    }

    public void setTakenDateTime(String takenDateTime) {
        this.takenDateTime = takenDateTime;
    }

    public String getCameraMake() {
        return cameraMake;
    }

    public void setCameraMake(String cameraMake) {
        this.cameraMake = cameraMake;
    }

    public String getCameraModel() {
        return cameraModel;
    }

    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    public Float getfNumber() {
        return fNumber;
    }

    public void setfNumber(Float fNumber) {
        this.fNumber = fNumber;
    }

    public Float getExposureDenominator() {
        return exposureDenominator;
    }

    public void setExposureDenominator(Float exposureDenominator) {
        this.exposureDenominator = exposureDenominator;
    }

    public Float getExposureNumerator() {
        return exposureNumerator;
    }

    public void setExposureNumerator(Float exposureNumerator) {
        this.exposureNumerator = exposureNumerator;
    }

    public Float getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(Float focalLength) {
        this.focalLength = focalLength;
    }

    public Float getIso() {
        return iso;
    }

    public void setIso(Float iso) {
        this.iso = iso;
    }

}
