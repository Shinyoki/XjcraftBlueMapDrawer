package org.xjcraft.senkosan.bluemap.marker;


import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.markers.HtmlMarker;
import de.bluecolored.bluemap.api.markers.Marker;
import org.xjcraft.senkosan.bluemap.utils.BlueMapUtil;

/**
 * 在线玩家的Marker创建者
 *
 * @author senko
 * @date 2023/2/4 11:36
 */
public class OnlinePlayerMarkerBuilder {

    public static Marker createMarker(String playerName, String uuid, Vector3d location) {
        return new HtmlMarker(playerName, location, BlueMapUtil.getOnlinePlayerIconHtml(playerName, uuid));
    }

}
