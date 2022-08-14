package github.shinyoki.bluemap.xjcraft.marker.strategy;

import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.marker.Marker;
import de.bluecolored.bluemap.api.marker.MarkerSet;
import github.shinyoki.bluemap.xjcraft.enums.MarkerType;
import org.bukkit.Location;

/**
 * 创建Marker的策略
 *
 * @author senko
 * @date 2022/8/14 8:29
 */
public interface ICreator {
    Marker createMarker(String markerId, String markerLabel, MarkerSet markerSet, BlueMapMap map, Location location, MarkerType markerType);
}
