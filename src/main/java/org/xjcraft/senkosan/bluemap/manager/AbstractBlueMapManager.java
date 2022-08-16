package org.xjcraft.senkosan.bluemap.manager;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.marker.Marker;
import de.bluecolored.bluemap.api.marker.MarkerAPI;
import de.bluecolored.bluemap.api.marker.MarkerSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.apply.ApplyHelper;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;
import org.xjcraft.senkosan.bluemap.enums.MarkerType;
import org.xjcraft.senkosan.bluemap.exception.XBMPluginException;
import org.xjcraft.senkosan.bluemap.utils.Log;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author senko
 * @date 2022/8/13 14:38
 */
public abstract class AbstractBlueMapManager {

    private MarkerAPI markerAPI;

    private BlueMapMap map;

    /**
     * TODO BlueMapAPI在2.0版本里存在很多破坏性的更新，需要适配
     */
    private Integer blueMapAPIVersion = 1;

    public Integer getBlueMapAPIVersion() {
        return blueMapAPIVersion;
    }

    public AbstractBlueMapManager() {
        this(AuditPlugin.getPlugin().getHelper(), BlueMapAPI.getInstance().get());
    }

    /**
     * @param applyHelper 小鸡服认证插件的ApplyHelper，用于获取玩家的meta信息
     * @param api         BlueMapAPI，用于渲染Marker
     */
    public AbstractBlueMapManager(ApplyHelper applyHelper, BlueMapAPI api) {

        try {
            this.markerAPI = Optional.ofNullable(api).orElseThrow(() -> new XBMPluginException("未能正确获取BlueMap相关API，请检查是否正确加载了BlueMap插件")).getMarkerAPI();
            this.map = api.getMap("world").get();
            this.blueMapAPIVersion = Integer.parseInt(api.getBlueMapVersion().substring(0, api.getBlueMapVersion().indexOf(".")));
        } catch (IOException e) {
            Log.error("加载BlueMapAPI时发生错误!");
            e.printStackTrace();
        }

    }

    public MarkerAPI getMarkerAPI() {
        return markerAPI;
    }

    public BlueMapMap getMap() {
        return map;
    }

    public void renderMarker(Player player) {
        this.renderMarker(player.getName());
    }

    public MarkerSet getMarkerSetById(String markerSetId) {
        return getMarkerAPI().getMarkerSet(markerSetId)
                .orElse(null);
    }

    /**
     * 获取地图上和我们要存储的MarkerSet
     */
    public List<MarkerSet> getMapMarkerSets() {
        return getMarkerAPI().getMarkerSets()
                .stream()
                .filter(this::isMyMarkerSet)
                .collect(Collectors.toList());
    }

    private boolean isMyMarkerSet(MarkerSet markerSet) {
        AtomicBoolean flag = new AtomicBoolean(false);
        Arrays.stream(MarkerType.values())
                .filter(markerType -> markerType.getMarkerSetId().equals(markerSet.getId()))
                .findFirst()
                .ifPresent((markerType) -> {
                    flag.set(true);
                });
        return flag.get();
    }

    public Marker getMarkerById(String markerSetId, String markerId) {
        return getMarkerById(getMarkerSetById(markerSetId), markerId);
    }

    /**
     * 获取Marker
     * 如果markerSet为空，则也返回空
     */
    public Marker getMarkerById(MarkerSet markerSet, String markerId) {
        if (Objects.nonNull(markerSet)) {
            return markerSet.getMarker(markerId).orElse(null);
        }
        return null;
    }

    protected void generalEdit(Marker marker, Location newLocation, String playerName, MarkerType newType) {
        Optional.ofNullable(marker)
                .ifPresent(oldMarker -> {
                    // location
                    oldMarker.setPosition(new Vector3d(newLocation.getX() + .5D, newLocation.getY() + .5D, newLocation.getZ() + .5D));
                });
    }

    protected String getLabel(String playerName, MarkerType markerType) {
        switch (markerType) {
            case BASE:
                return playerName + "的基地";
            case HOME:
                return playerName + "的家";
        }
        return "";
    }

    public void saveAsynchronously() {

        XJCraftBaseHomeBlueMapDrawer.getExecutorService()
                .submit(this::save);

    }

    public void save() {

        try {

            getMarkerAPI().save();

        } catch (IOException e) {
            Log.error("保存修改失败！");
            e.printStackTrace();
        }

    }

    /**
     * 删除MarkerSet
     * （需手动save()保存修改）
     */
    public void removeMarkerSet(String markerSetId) {
        getMarkerAPI().removeMarkerSet(markerSetId);
    }

    /**
     * 删除Marker，
     * 在遇到不同类型（如以前是HTML Marker， 现在要变成POI Marker）
     * 时要用到。
     * （需手动save()保存修改）
     */
    protected void removeMarker(String playerName, MarkerSet markerSet, MarkerType markerType) {

        if (Objects.nonNull(markerSet)) {
            markerSet.removeMarker(markerType.getMarkerId(playerName));
        }

    }

    public void removeMarkers(String playerName) {

        for (MarkerType type : MarkerType.values()) {
            Optional.ofNullable(getMarkerSetById(type.getMarkerSetId()))
                    .ifPresent(markerSet -> {
                        markerSet.removeMarker(type.getMarkerId(playerName));
                    });
        }

    }

    /**
     * 渲染全部
     *
     * @param sender: 发送回调消息，代码触发时传入null即可
     * @return false：当前已有进程正在渲染，则会
     */
    public void renderAll(final CommandSender sender) {

        long beforeIterateOffline = System.currentTimeMillis();
        List<String> playerNames = getXJAuditPlayerNameRecords();
        Log.info("获取需被渲染玩家数量耗时：" + (System.currentTimeMillis() - beforeIterateOffline) + "ms");
        Log.d("将被渲染玩家数量:" + playerNames.size());

        XJCraftBaseHomeBlueMapDrawer.getExecutorService().submit(() -> {
            try {

                long beforeRender = System.currentTimeMillis();
                // 在开始渲染钱
                playerNames.forEach(this::renderMarker);
                Log.d("渲染所有标点花费的时间：" + (System.currentTimeMillis() - beforeRender) + "ms");

                long beforeSave = System.currentTimeMillis();
                save();
                Log.d("保存所有标点花费的时间：" + (System.currentTimeMillis() - beforeSave) + "ms");
            } catch (Exception e) {
                Log.error("渲染所有人的Markers失败！" + e.getMessage());
                e.printStackTrace();
            }
            Log.info("渲染Markers总耗时：" + (System.currentTimeMillis() - beforeIterateOffline) + "ms");

            if (Objects.nonNull(sender)) {
                sender.sendMessage("渲染Markers完成！总共耗时：" + (System.currentTimeMillis() - beforeIterateOffline) + "ms");
            }

        });

    }

    protected List<String> getOfflinePlayerNames() {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .map(OfflinePlayer::getName).collect(Collectors.toList());
    }

    protected List<String> getXJAuditPlayerNameRecords() {

        // 获取AuditPlugin配置文件夹 status
        String statusDirPath = AuditPlugin.getPlugin().getDataFolder().getAbsolutePath()
                .concat(File.separator).concat("status");
        File statusDir = new File(statusDirPath);
        if (!statusDir.exists()) {
            statusDir.mkdirs();
            return null;
        }

        // 遍历所有内部的文件名
        return Arrays.stream(statusDir.listFiles())
                // 是yml文件
                .filter(file -> file.isFile() && file.getName().endsWith(".yml"))
                // 获取玩家名（文件名）
                .map(file -> file.getName().split("\\.")[0])
                .collect(Collectors.toList());

    }

    /**
     * 清理以前的MarkerSet
     *
     * @param sender 发送回调消息
     */
    public Future<?> removeAllMarkerSet(final CommandSender sender) {

        return XJCraftBaseHomeBlueMapDrawer.getExecutorService().submit(() -> {

            // 删除
            Arrays.stream(MarkerType.values())
                    .forEach(markerType -> {
                        getMarkerAPI().getMarkerSet(markerType.getMarkerSetId())
                                .ifPresent(markerSetId -> {
                                    getMarkerAPI().removeMarkerSet(markerSetId);
                                });
                    });

            // 保存修改
            save();

            Optional.ofNullable(sender)
                    .ifPresent(s -> {
                        s.sendMessage("删除所有MarkerSet成功！");
                        Log.info(sender.getName() + "删除了所有的MarkerSet！");
                    });
        });

    }

    /**
     * 给指定玩家渲染Marker
     */
    public abstract void renderMarker(String playerName);

}
