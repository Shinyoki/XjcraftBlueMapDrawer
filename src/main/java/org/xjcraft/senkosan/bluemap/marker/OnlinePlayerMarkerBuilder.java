package org.xjcraft.senkosan.bluemap.marker;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.core.BlueMap;
import de.bluecolored.bluemap.core.world.World;
import org.bukkit.Location;
import org.xjcraft.senkosan.bluemap.exception.XBMPluginException;

import java.util.Objects;
import java.util.Optional;

import static org.xjcraft.senkosan.bluemap.constants.MapConstants.*;

/**
 * 在线玩家的Marker创建者
 *
 * @author senko
 * @date 2023/2/4 11:36
 */
public class OnlinePlayerMarkerBuilder {

    private String playerName;          // Marker的id
    private MarkerSet markerSet;        // Marker的所在集合
    private BlueMapMap map;             // Marker所在的地图
    private Location location;          // Marker的位置

    public static OnlinePlayerMarkerBuilder builder() {
        return new OnlinePlayerMarkerBuilder();
    }

    public MarkerSet getMainLandMarkerSet(BlueMapAPI blueMapAPI) {
        return getMarkerSet(0, blueMapAPI);
    }

    /**
     * @param dimension 0:主世界、1:地狱、2:末地
     * @return 在线玩家的MarkerSet
     */
    public MarkerSet getMarkerSet(Integer dimension, BlueMapAPI api) {
        MarkerSet res = null;
        switch (dimension) {
            case 0:
//                Optional<BlueMapMap> opt = api.getMap();
//                if (opt.isPresent()) {
//                    res = opt.get().getMarkerSets().get(MAIN_LAND_ONLINE_PLAYER_MARKER_SET_ID);
//                    if (res == null) {
//                        res = opt.get().getMarkerSets().create(MAIN_LAND_ONLINE_PLAYER_MARKER_SET_ID);
//                    }
//                } else {
//                    throw new XBMPluginException("主世界地图不存在！请检查配置文件中的服务器地图名称是否正确！");
//                }
                break;
            case 1:
                Optional<BlueMapMap> opt2 = api.getMap(NETHER_MAP_ID);
                if (opt2.isPresent()) {
                    res = opt2.get().getMarkerSets().get(NETHER_ONLINE_PLAYER_MARKER_SET_ID);
                }
                break;
            case 2:
                Optional<BlueMapMap> map1 = api.getMap(THE_END_MAP_ID);
                if (map1.isPresent()) {
                    res = map1.get().getMarkerSets().get(THE_END_ONLINE_PLAYER_MARKER_SET_ID);
                }
                break;
        }
        if (Objects.isNull(res)) {
            throw new RuntimeException("在线玩家的MarkerSet不存在");
        }

        return null;
    }

    public static MarkerSet buildMarkerSet() {
        return MarkerSet.builder()
                .label("在线玩家")
                .toggleable(true)
                .defaultHidden(false)
                .build();
    }

}
