package org.xjcraft.senkosan.bluemap.marker;


import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.Marker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import org.bukkit.Location;
import org.jim.bukkit.audit.util.Assert;
import org.xjcraft.senkosan.bluemap.constants.MapConstants;
import org.xjcraft.senkosan.bluemap.enums.MarkerType;

/**
 *
 * @author senko
 * @date 2022/8/14 8:02
 */
public class MarkerBuilder {

    private String playerName;            // Marker的id
    private MarkerSet markerSet;        // Marker的所在集合
    private BlueMapMap map;             // Marker所在的地图
    private Location location;          // Marker的位置
    private MarkerType markerType;      // Marker的类型

    public static MarkerBuilder builder() {
        return new MarkerBuilder();
    }

    public MarkerBuilder playerName(String playerName) {
        Assert.notNull(playerName, "markerId");
        this.playerName = playerName;
        return this;
    }


    public MarkerBuilder markerSet(MarkerSet markerSet) {
        Assert.notNull(markerSet, "markerSet");
        this.markerSet = markerSet;
        return this;
    }

    public MarkerBuilder map(BlueMapMap map) {
        Assert.notNull(map, "map");
        this.map = map;
        return this;
    }

    public MarkerBuilder location(Location location) {
        Assert.notNull(location, "location");
        this.location = location;
        return this;
    }

    public MarkerBuilder markerType(MarkerType markerType) {
        Assert.notNull(markerType, "markerType");
        this.markerType = markerType;
        return this;
    }

    public Marker build(MarkerCreator creator) {
        Assert.notNull(creator, "MarkerCreator不能为空");
        Assert.notNull(playerName, "playerName不能为空");
        Assert.notNull(markerSet, "MarkerSet不能为空");
        Assert.notNull(map, "Map不能为空");
        Assert.notNull(location, "Location不能为空");
        return creator.createMarker(getMarkerId(playerName), getLabel(playerName), markerSet, location, markerType);
    }

    private String getLabel(String playerName) {
        switch (markerType) {
            case BASE:
                return playerName + "的基地";
            case HOME:
                return playerName + "的家";
        }
        return "";
    }

    private String getMarkerId(String playerName) {
        switch (markerType) {
            case BASE:
                return playerName.toLowerCase() + MapConstants.BASE_MARKER_ID_SUFFIX;
            case HOME:
                return playerName.toLowerCase() + MapConstants.HOME_MARKER_ID_SUFFIX;
        }
        return "";
    }

}
