package org.xjcraft.senkosan.bluemap.enums;

import org.xjcraft.senkosan.bluemap.constants.MapConstants;

/**
 * Marker的类型 [基地、小镇]
 *
 * @author senko
 * @date 2022/8/13 19:17
 */
public enum MarkerType {

    /**
     * 基地
     */
    BASE(MapConstants.BASE_MARKER_SET_ID, MapConstants.BASE_MARKER_ID_SUFFIX, MapConstants.BASE_MARKER_SET_LABEL),

    /**
     * 小镇
     */
    HOME(MapConstants.HOME_MARKER_SET_ID, MapConstants.HOME_MARKER_ID_SUFFIX, MapConstants.HOME_MARKER_SET_LABEL);

    private final String markerSetId;     // markerSet的id
    private final String markerSuffix;    // marker的后缀：如 '的基地'
    private final String markerSetLabel;  // markerSet的标签

    MarkerType(String markerSetId, String markerSuffix, String markerSetLabel) {
        this.markerSetId = markerSetId;
        this.markerSuffix = markerSuffix;
        this.markerSetLabel = markerSetLabel;
    }

    public String getMarkerSetId() {
        return markerSetId;
    }

    public String getMarkerSuffix() {
        return markerSuffix;
    }

    public String getMarkerSetLabel() {
        return markerSetLabel;
    }

    public String getMarkerId(String playerName) {
        return playerName.toLowerCase().concat(markerSuffix);
    }

}
