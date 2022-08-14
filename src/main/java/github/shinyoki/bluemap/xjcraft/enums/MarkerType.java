package github.shinyoki.bluemap.xjcraft.enums;

import github.shinyoki.bluemap.xjcraft.constants.MapConstants;

/**
 * @author senko
 * @date 2022/8/13 19:17
 */
public enum MarkerType {

    /**
     * 基地
     */
    BASE(MapConstants.BASE_MARKER_SET_ID, MapConstants.BASE_MARKER_ID_SUFFIX),

    /**
     * 小镇
     */
    HOME(MapConstants.HOME_MARKER_SET_ID, MapConstants.HOME_MARKER_ID_SUFFIX),

    /**
     * 单独显示
     */
    DEFAULT(MapConstants.ALL_IN_ONE_MARKER_SET_ID, MapConstants.ALL_IN_ONE_MARKER_ID_SUFFIX);

    private String markerSetId;     // markerSet的id
    private String markerSuffix;    // marker的后缀：如 '的基地'

    MarkerType(String markerSetId, String markerSuffix) {
        this.markerSetId = markerSetId;
        this.markerSuffix = markerSuffix;
    }

    public String getMarkerSetId() {
        return markerSetId;
    }

    public String getMarkerSuffix() {
        return markerSuffix;
    }

}
