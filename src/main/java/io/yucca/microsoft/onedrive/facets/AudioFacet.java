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
 * AudioFacet
 * 
 * @author yucca.io
 */
public class AudioFacet {

    private String album;
    private String albumArtist;
    private String artist;
    private String bitrate;
    private String composers;
    private String copyright;
    private Float disc;
    private Float discCount;
    private Float duration;
    private String genre;
    private Boolean hasDrm;
    private Boolean isVariableBitrate;
    private String title;
    private Float track;
    private Float trackCount;
    private Float year;

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getComposers() {
        return composers;
    }

    public void setComposers(String composers) {
        this.composers = composers;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Float getDisc() {
        return disc;
    }

    public void setDisc(Float disc) {
        this.disc = disc;
    }

    public Float getDiscCount() {
        return discCount;
    }

    public void setDiscCount(Float discCount) {
        this.discCount = discCount;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Boolean isHasDrm() {
        return hasDrm;
    }

    public void setHasDrm(Boolean hasDrm) {
        this.hasDrm = hasDrm;
    }

    public Boolean isVariableBitrate() {
        return isVariableBitrate;
    }

    public void setVariableBitrate(Boolean isVariableBitrate) {
        this.isVariableBitrate = isVariableBitrate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getTrack() {
        return track;
    }

    public void setTrack(Float track) {
        this.track = track;
    }

    public Float getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(Float trackCount) {
        this.trackCount = trackCount;
    }

    public Float getYear() {
        return year;
    }

    public void setYear(Float year) {
        this.year = year;
    }

}
