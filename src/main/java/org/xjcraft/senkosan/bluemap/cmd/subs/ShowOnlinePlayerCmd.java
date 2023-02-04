package org.xjcraft.senkosan.bluemap.cmd.subs;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.HtmlMarker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.xjcraft.senkosan.bluemap.cmd.ICmd;
import org.xjcraft.senkosan.bluemap.constants.MapConstants;
import org.xjcraft.senkosan.bluemap.manager.DefaultBlueMapManager;
import org.xjcraft.senkosan.bluemap.manager.XJCraftBlueMapContext;
import org.xjcraft.senkosan.bluemap.marker.OnlinePlayerMarkerBuilder;
import org.xjcraft.senkosan.bluemap.utils.BlueMapUtil;

import java.util.Collection;
import java.util.List;

/**
 * 显示或隐藏 自己在线时的玩家图标
 * /xjb show
 *
 * @author senko
 * @date 2023/2/4 14:41
 */
public class ShowOnlinePlayerCmd extends ICmd {
    public ShowOnlinePlayerCmd() {
        super("show", "显示自己在线时的玩家图标");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            DefaultBlueMapManager blueMapManager = XJCraftBlueMapContext.getBlueMapManager();
            BlueMapMap mainLandMap = blueMapManager.getMainLandMap();
            MarkerSet markerSet = mainLandMap.getMarkerSets().get(MapConstants.MAIN_LAND_ONLINE_PLAYER_MARKER_SET_ID);
            if (markerSet == null) {
                markerSet = OnlinePlayerMarkerBuilder.buildMarkerSet();
                mainLandMap.getMarkerSets().put(MapConstants.MAIN_LAND_ONLINE_PLAYER_MARKER_SET_ID,
                        markerSet);
            }
            Vector3d vector3d = new Vector3d(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
            HtmlMarker htmlMarker = new HtmlMarker(player.getName(), vector3d, BlueMapUtil.getOnlinePlayerIconHtml(player.getName()));
            markerSet.getMarkers().put(player.getName(), htmlMarker);

            // TODO 显示自己在线时的玩家图标
            sender.sendMessage("现在卫星地图上应该会动态显示你的位置了！");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            return true;
        } else {
            sender.sendMessage("只有玩家才能执行此命令！");
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    protected void editHelpMessage() {
        this.appendNewLine("/xjb show", "显示自己在线时的玩家图标");
    }
}
