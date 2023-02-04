package org.xjcraft.senkosan.bluemap.manager;

import de.bluecolored.bluemap.api.markers.MarkerSet;

/**
 * 渲染在线玩家的Marker
 *
 * @author senko
 * @date 2023/2/4 12:00
 */
public interface IRenderOnlinePlayerMarker {

    /**
     * 获取在线玩家MarkerSet
     */
    MarkerSet getOnlinePlayerMarkerSet();
}
