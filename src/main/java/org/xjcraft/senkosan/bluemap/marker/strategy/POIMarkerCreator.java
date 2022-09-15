package org.xjcraft.senkosan.bluemap.marker.strategy;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.Marker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import org.bukkit.Location;
import org.xjcraft.senkosan.bluemap.enums.MarkerType;
import org.xjcraft.senkosan.bluemap.utils.BlueMapUtil;
import org.xjcraft.senkosan.bluemap.utils.Log;

import static org.xjcraft.senkosan.bluemap.utils.BlueMapUtil.BaseIcon.*;

/**
 * POI Marker
 *
 * @author senko
 * @date 2022/9/15 13:44
 */
public class POIMarkerCreator implements ICreator {

    @Override
    public Marker createMarker(String markerId, String markerLabel, MarkerSet markerSet, Location location, MarkerType markerType) {

        Vector3d vector3d = new Vector3d(location.getX() + .5D, location.getY() + .5D, location.getZ() + .5D);
        Vector2i offset = new Vector2i(0, 0);
        BlueMapUtil.BaseIcon icon = markerType.equals(MarkerType.BASE) ?  BASE: HOME;
        POIMarker poiMarker = new POIMarker(markerLabel, vector3d, BlueMapUtil.getBlueMapIcon(icon), offset);

        return markerSet.getMarkers()
                .put(markerId, poiMarker);
    }

}
