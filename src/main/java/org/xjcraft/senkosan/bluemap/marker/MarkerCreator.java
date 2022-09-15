package org.xjcraft.senkosan.bluemap.marker;

import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.Marker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import org.bukkit.Location;
import org.xjcraft.senkosan.bluemap.enums.MarkerType;
import org.xjcraft.senkosan.bluemap.marker.strategy.ICreator;
import org.xjcraft.senkosan.bluemap.marker.strategy.POIMarkerCreator;

/**
 * Marker的创建者
 *
 * @author senko
 * @date 2022/8/14 8:17
 */
public enum MarkerCreator {

    POI("POI", new POIMarkerCreator());

    MarkerCreator(String creatorName, ICreator creator) {
        this.creatorName = creatorName;
        this.creator = creator;
    }

    public ICreator getCreator() {
        return creator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    private String creatorName;
    private ICreator creator;

    public Marker createMarker(String markerId, String markerLabel, MarkerSet markerSet, Location location, MarkerType markerType) {
        return creator.createMarker(markerId, markerLabel, markerSet, location, markerType);
    }

}


