package org.xjcraft.senkosan.bluemap.manager;

import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import org.bukkit.Location;
import org.xjcraft.senkosan.bluemap.enums.MarkerType;
import org.xjcraft.senkosan.bluemap.marker.HomeBaseMarkerBuilder;
import org.xjcraft.senkosan.bluemap.marker.MarkerCreator;

/**
 * @author senko
 * @date 2022/8/13 16:43
 */
public class DefaultBlueMapManager extends ConfigurableBlueMapManager {

    private MarkerCreator markerCreator = MarkerCreator.POI;


    @Override
    protected void createHomeBaseMarker(String playerName, Location location, MarkerType markerType, MarkerSet markerSet) {

        switch (markerType) {
            // 只处理HOME & BASE
            case BASE:
            case HOME:
                HomeBaseMarkerBuilder.builder()
                        .playerName(playerName)
                        .markerSet(markerSet)
                        .map(getMainLandMap())
                        .location(location)
                        .markerType(markerType)
                        .build(markerCreator);
                break;
            case ONLINE_PLAYER:
                break;
        };

    }

    @Override
    public MarkerSet getOnlinePlayerMarkerSet() {
        MarkerSet markerSet = MarkerSet.builder()
                .defaultHidden(false)
                .label("在线玩家")
                .toggleable(true)
                .build();
//        for (BlueMapMap map : getMapApi().getMaps()) {
//            map.getMarkerSets().put(markerSet.getId(), markerSet);
//        }
        // TODO 给主世界、地狱、末地添加在线玩家的MarkerSet
        return null;
    }

    public void changeMarkerCreator(MarkerCreator creator) {
        this.markerCreator = creator;
    }

    public void removeAll() {
        getBaseMarkerSet()
                .getMarkers().clear();
    }

}
