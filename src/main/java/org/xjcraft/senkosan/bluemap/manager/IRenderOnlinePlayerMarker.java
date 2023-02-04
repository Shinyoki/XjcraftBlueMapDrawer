package org.xjcraft.senkosan.bluemap.manager;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.markers.MarkerSet;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 渲染在线玩家的Marker
 *
 * @author senko
 * @date 2023/2/4 12:00
 */
public interface IRenderOnlinePlayerMarker {

    /**
     * 获取 主世界在线玩家MarkerSet
     */
    MarkerSet getMainLandOnlinePlayerMarkerSet();

    /**
     * 获取 地狱在线玩家MarkerSet
     */
    MarkerSet getNetherOnlinePlayerMarkerSet();

    /**
     * 获取 末地在线玩家MarkerSet
     */
    MarkerSet getTheEndOnlinePlayerMarkerSet();

    /**
     * 渲染在线玩家
     */
    void renderOnlinePlayer(String playerName, String uuid, int dimension, double x, double y, double z);
    void renderOnlinePlayer(String playerName, String uuid, int dimension, Vector3d location);

    /**
     * 清空Marker
     */
    void clearMainLandOnlinePlayerMarker(String playerName);
    void clearNetherOnlinePlayerMarker(String playerName);
    void clearTheEndOnlinePlayerMarker(String playerName);

    default void clearAllOnlinePlayerMarker(String playerName) {
        clearMainLandOnlinePlayerMarker(playerName);
        clearNetherOnlinePlayerMarker(playerName);
        clearTheEndOnlinePlayerMarker(playerName);
    }

}
