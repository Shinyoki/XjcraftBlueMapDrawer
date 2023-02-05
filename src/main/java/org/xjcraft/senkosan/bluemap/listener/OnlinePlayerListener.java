package org.xjcraft.senkosan.bluemap.listener;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;
import org.xjcraft.senkosan.bluemap.constants.MapConstants;
import org.xjcraft.senkosan.bluemap.entity.PlayerInfo;
import org.xjcraft.senkosan.bluemap.exception.XBMPluginException;
import org.xjcraft.senkosan.bluemap.manager.DefaultBlueMapManager;
import org.xjcraft.senkosan.bluemap.manager.XJCraftBlueMapContext;
import org.xjcraft.senkosan.bluemap.utils.Log;
import org.xjcraft.senkosan.bluemap.utils.MojangUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 玩家登录监听器
 *
 * @author senko
 * @date 2023/2/4 18:56
 */
public class OnlinePlayerListener implements Listener {

    private final DefaultBlueMapManager blueMapManager = XJCraftBlueMapContext.getBlueMapManager();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        String playerName = player.getName();

        Log.d("玩家登录事件触发！登录玩家：" + playerName);

        // 登录后更新在线玩家缓存
        XJCraftBaseHomeBlueMapDrawer.submit(() -> {
            // 检查是否为需要渲染的玩家
            File ymlFile = new File(XJCraftBaseHomeBlueMapDrawer.getInstance().getDataFolder(), MapConstants.ONLINE_PLAYER_CONFIG_FILE_NAME);
            if (!ymlFile.exists()) {
                return;
            }
            YamlConfiguration cfg = new YamlConfiguration();
            try {
                cfg.load(ymlFile);
            } catch (IOException | InvalidConfigurationException e) {
                throw new XBMPluginException("读取" + MapConstants.ONLINE_PLAYER_CONFIG_FILE_NAME + "配置文件失败！", e);
            }

            // 配置文件中玩家不存在，则跳过
            List<String> list = cfg.getStringList(MapConstants.ONLINE_PLAYER_CONFIG_KEY);
            if (
                    list.isEmpty() ||
                            !list.contains(playerName)
            ) {
                Log.d("玩家" + playerName + "不在渲染列表中，跳过！");
                return;
            }

            Log.d("开始查询UUID");
            // 异步查询玩家信息
            String playerUUID = MojangUtil.getPlayerUUID(playerName);
            // 在玩家登录前，清空所有该玩家的Marker
            blueMapManager.clearAllOnlinePlayerMarker(playerName);
            Log.d("添加玩家缓存");
            XJCraftBaseHomeBlueMapDrawer.addOnlinePlayerCache(playerName, new PlayerInfo(
                    playerName,
                    playerUUID,
                    blueMapManager.getDimensionByWorldName(player.getWorld().getName()
                    ))
            );
        });

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        XJCraftBaseHomeBlueMapDrawer.removeOnlinePlayerCache(event.getPlayer().getName());
        blueMapManager.clearAllOnlinePlayerMarker(event.getPlayer().getName());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerSwitchWorld(PlayerChangedWorldEvent event) {
        if (XJCraftBaseHomeBlueMapDrawer.containsPlayer(event.getPlayer().getName())) {
            // 切换世界后更新在线玩家缓存
            // 异步查询玩家信息
            Player player = event.getPlayer();
            blueMapManager.clearAllOnlinePlayerMarker(player.getName());
            PlayerInfo playerInfo = XJCraftBaseHomeBlueMapDrawer.getOnlinePlayerCache(player.getName()).clone();
            if (Objects.nonNull(playerInfo)) {
                playerInfo.setDimension(blueMapManager.getDimensionByWorldName(player.getWorld().getName()));
                XJCraftBaseHomeBlueMapDrawer.addOnlinePlayerCache(player.getName(), playerInfo);
            }
        }
    }

}
