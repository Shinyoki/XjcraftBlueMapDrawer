package github.shinyoki.bluemap.xjcraft.manager;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.marker.Marker;
import de.bluecolored.bluemap.api.marker.MarkerSet;
import github.shinyoki.bluemap.xjcraft.XJCraftBaseHomeBlueMapDrawer;
import github.shinyoki.bluemap.xjcraft.enums.MarkerType;
import github.shinyoki.bluemap.xjcraft.marker.MarkerBuilder;
import github.shinyoki.bluemap.xjcraft.marker.MarkerCreator;
import github.shinyoki.bluemap.xjcraft.utils.Log;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jim.bukkit.audit.apply.ApplyHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

    public void renderAll() {

        AtomicLong allBegin = new AtomicLong(System.currentTimeMillis());
        Log.info("需删除的Marker数量: " + getMarkerCount());
        Log.info("开始渲染所有玩家的地图，当前时刻：" + System.currentTimeMillis());
        // 切异步：删除所有Marker
        Bukkit.getScheduler().runTaskAsynchronously(XJCraftBaseHomeBlueMapDrawer.getInstance(), () -> {

            AtomicLong beforeDeleteMillis = new AtomicLong(System.currentTimeMillis());
            try {
                // 删除原有的所有标记
                Arrays.stream(MarkerType.values())
                        .forEach(markerType -> {
                            getMarkerAPI().removeMarkerSet(markerType.getMarkerSetId());
                        });
                getMarkerAPI().save();
                Log.info("删除线程：" + Thread.currentThread().getName() + "，耗时：" + (System.currentTimeMillis() - beforeDeleteMillis.get()) + "ms");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 切Main线程：获取所有离线玩家的名称
            Bukkit.getScheduler().runTask(XJCraftBaseHomeBlueMapDrawer.getInstance(), () -> {

                List<String> playerNames = Arrays.stream(Bukkit.getOfflinePlayers())
                        .map(OfflinePlayer::getName).collect(Collectors.toList());

                Log.info("离线玩家数量*2：" + playerNames.size() * 2);

                // 切异步：渲染所有离线玩家的地图
                Bukkit.getScheduler().runTaskAsynchronously(XJCraftBaseHomeBlueMapDrawer.getInstance(), () -> {

                    AtomicLong beforeRender = new AtomicLong(System.currentTimeMillis());
                    // 渲染所有标记
                    playerNames.forEach(this::renderMarker);
                    Log.info("渲染线程：" + Thread.currentThread().getName() + "耗时: " + (System.currentTimeMillis() - beforeRender.get()) + "ms");
                    AtomicLong beforeSaveMillis = new AtomicLong(System.currentTimeMillis());
                    try {
                        getMarkerAPI().save();
                        Log.info("保存时的线程：" + Thread.currentThread().getName() + "耗时: " + (System.currentTimeMillis() - beforeSaveMillis.get()) + "ms");
                        Log.info("渲染所有玩家的地图完成，当前时刻：" + System.currentTimeMillis() + "，总耗时：" + (System.currentTimeMillis() - allBegin.get()) + "ms");
                    } catch (IOException e) {
                        Log.error("保存Marker失败");
                        throw new RuntimeException(e);
                    }

                });

            });

        });

    }

    public void changeMarkerCreator(MarkerCreator creator) {
        this.markerCreator = creator;
    }

}
