package org.xjcraft.senkosan.bluemap.manager;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.marker.Marker;
import de.bluecolored.bluemap.api.marker.MarkerSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jim.bukkit.audit.apply.ApplyHelper;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;
import org.xjcraft.senkosan.bluemap.enums.MarkerType;
import org.xjcraft.senkosan.bluemap.marker.MarkerBuilder;
import org.xjcraft.senkosan.bluemap.marker.MarkerCreator;
import org.xjcraft.senkosan.bluemap.utils.Log;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author senko
 * @date 2022/8/13 16:43
 */
public class DefaultBlueMapManager extends ConfigurableBlueMapManager {

    private MarkerCreator markerCreator = MarkerCreator.HTML;

    public DefaultBlueMapManager() {
        super(XJCraftBaseHomeBlueMapDrawer.getInstance().getConfig());
    }

    public DefaultBlueMapManager(MarkerCreator creator) {
        super(XJCraftBaseHomeBlueMapDrawer.getInstance().getConfig());
        this.markerCreator = creator;
    }

    public DefaultBlueMapManager(ApplyHelper applyHelper, BlueMapAPI api, FileConfiguration config) {
        super(applyHelper, api, config);
    }

    @Override
    protected Marker createMarker(String playerName, Location location, MarkerType markerType, MarkerSet markerSet) {
//        return HTMLMarkerFactory.builder()
//                .setPlayerName(playerName)
//                .appendIcon(markerType)
//                .build()
//                .toMarker(markerSet, getMap(), location);
        return MarkerBuilder.builder()
                .playerName(playerName)
                .markerSet(markerSet)
                .map(getMap())
                .location(location)
                .markerType(markerType)
                .build(markerCreator);
    }

    public void renderAll() {

        try {
            Arrays.stream(MarkerType.values())
                    .forEach(markerType -> {
                        getMarkerAPI().removeMarkerSet(markerType.getMarkerSetId());
                    });
            getMarkerAPI().save();
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                renderMarker(offlinePlayer.getName());
            }
            getMarkerAPI().save();
        } catch (IOException e) {
            Log.warning("清理Markers失败");
            throw new RuntimeException(e);
        }

    }

    public void changeMarkerCreator(MarkerCreator creator) {
        this.markerCreator = creator;
    }

}
