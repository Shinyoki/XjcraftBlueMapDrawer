package org.xjcraft.senkosan.bluemap.manager;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.marker.HtmlMarker;
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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.xjcraft.senkosan.bluemap.constants.MapConstants.*;

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

        Marker marker = getMarkerById(markerSet, markerType.getMarkerId(playerName));
        if (Objects.nonNull(marker)) {
//            if (!(marker instanceof HtmlMarker)) {
//                // 不是同类，则只进行坐标修改
//            }
            // 存在，则只进行坐标修改
            generalEdit(marker, location, playerName, markerType);
            return marker;
        }

        // 不存在，则创建
        return MarkerBuilder.builder()
                .playerName(playerName)
                .markerSet(markerSet)
                .map(getMap())
                .location(location)
                .markerType(markerType)
                .build(markerCreator);

    }

    private Integer getMarkerCount() {
        AtomicInteger count = new AtomicInteger();
        for (MarkerType type : MarkerType.values()) {
            getMarkerAPI().getMarkerSet(type.getMarkerSetId())
                    .ifPresent(markerSet -> {
                        count.addAndGet(markerSet.getMarkers().size());
                    });
        }
        return count.get();
    }

    public void changeMarkerCreator(MarkerCreator creator) {
        this.markerCreator = creator;
    }

}
