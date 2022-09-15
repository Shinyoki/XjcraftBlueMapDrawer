package org.xjcraft.senkosan.bluemap.manager;

import de.bluecolored.bluemap.api.markers.MarkerSet;
import org.bukkit.Location;
import org.xjcraft.senkosan.bluemap.enums.MarkerType;
import org.xjcraft.senkosan.bluemap.marker.MarkerBuilder;
import org.xjcraft.senkosan.bluemap.marker.MarkerCreator;

/**
 * @author senko
 * @date 2022/8/13 16:43
 */
public class DefaultBlueMapManager extends ConfigurableBlueMapManager {

    private MarkerCreator markerCreator = MarkerCreator.POI;


    @Override
    protected void createMarker(String playerName, Location location, MarkerType markerType, MarkerSet markerSet) {

        // 不存在，则创建
        MarkerBuilder.builder()
                .playerName(playerName)
                .markerSet(markerSet)
                .map(getMapToRender())
                .location(location)
                .markerType(markerType)
                .build(markerCreator);

    }

    public void changeMarkerCreator(MarkerCreator creator) {
        this.markerCreator = creator;
    }

    public void removeAll() {
        getBaseMarkerSet()
                .getMarkers().clear();
    }

}
