package org.xjcraft.senkosan.bluemap.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;

/**
 * 玩家退出监听器
 *
 * @author senko
 * @date 2023/2/4 22:24
 */
public class PlayerQuitListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        XJCraftBaseHomeBlueMapDrawer.removeOnlinePlayerCache(event.getPlayer().getName());
    }

}
