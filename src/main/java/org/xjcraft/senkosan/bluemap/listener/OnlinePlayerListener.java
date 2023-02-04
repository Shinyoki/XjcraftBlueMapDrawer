package org.xjcraft.senkosan.bluemap.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;
import org.xjcraft.senkosan.bluemap.entity.PlayerInfo;
import org.xjcraft.senkosan.bluemap.manager.XJCraftBlueMapContext;
import org.xjcraft.senkosan.bluemap.utils.MojangUtil;

/**
 * 玩家登录监听器
 *
 * @author senko
 * @date 2023/2/4 18:56
 */
public class OnlinePlayerListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 登录后更新在线玩家缓存
        XJCraftBaseHomeBlueMapDrawer.submit(() -> {
            // 异步查询玩家信息
            Player player = event.getPlayer();
            String playerName = player.getName();
            String playerUUID = MojangUtil.getPlayerUUID(playerName);
            Location location = player.getLocation();
            // 在玩家登录前，清空所有该玩家的Marker
            XJCraftBlueMapContext.getBlueMapManager().clearAllOnlinePlayerMarker(playerName);
            XJCraftBaseHomeBlueMapDrawer.addOnlinePlayerCache(playerName, new PlayerInfo(
                    playerName,
                    playerUUID,
                    XJCraftBlueMapContext.getBlueMapManager().getDimensionByWorldName(location.getWorld().getName()),
                    location.getX(),
                    location.getY(),
                    location.getZ()
            ));
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        XJCraftBaseHomeBlueMapDrawer.removeOnlinePlayerCache(event.getPlayer().getName());
        XJCraftBlueMapContext.getBlueMapManager().clearAllOnlinePlayerMarker(event.getPlayer().getName());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerSwitchWorld(PlayerChangedWorldEvent event) {
        if (XJCraftBaseHomeBlueMapDrawer.containsPlayer(event.getPlayer().getName())) {
            // 切换世界后更新在线玩家缓存
            XJCraftBaseHomeBlueMapDrawer.submit(() -> {
                // 异步查询玩家信息
                Player player = event.getPlayer();
                PlayerInfo info = XJCraftBaseHomeBlueMapDrawer.getOnlinePlayerCache(player.getName());
                Location location = player.getLocation();
                XJCraftBlueMapContext.getBlueMapManager().clearAllOnlinePlayerMarker(player.getName());
                XJCraftBaseHomeBlueMapDrawer.addOnlinePlayerCache(player.getName(), new PlayerInfo(
                        player.getName(),
                        info == null ? MojangUtil.getPlayerUUID(player.getName()) : info.getPlayerUUID(),
                        XJCraftBlueMapContext.getBlueMapManager().getDimensionByWorldName(location.getWorld().getName()),
                        location.getX(),
                        location.getY(),
                        location.getZ()
                ));
            });
        }
    }

}
