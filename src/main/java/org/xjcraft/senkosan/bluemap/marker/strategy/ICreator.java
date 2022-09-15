package org.xjcraft.senkosan.bluemap.marker.strategy;

import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.Marker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import org.bukkit.Location;
import org.xjcraft.senkosan.bluemap.enums.MarkerType;

/**
 * 创建Marker的策略
 *
 * @author senko
 * @date 2022/8/14 8:29
 */
public interface ICreator {
    Marker createMarker(String markerId, String markerLabel, MarkerSet markerSet, Location location, MarkerType markerType);
}
