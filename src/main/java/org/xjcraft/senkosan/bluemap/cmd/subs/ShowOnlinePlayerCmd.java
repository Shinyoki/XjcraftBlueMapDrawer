package org.xjcraft.senkosan.bluemap.cmd.subs;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;
import org.xjcraft.senkosan.bluemap.cmd.ICmd;
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

/**
 * 显示或隐藏 自己在线时的玩家图标
 * /xjb show
 *
 * @author senko
 * @date 2023/2/4 14:41
 */
public class ShowOnlinePlayerCmd extends ICmd {

    private final DefaultBlueMapManager blueMapManager;

    public ShowOnlinePlayerCmd() {
        super("show", "显示自己在线时的玩家图标");
        blueMapManager = XJCraftBlueMapContext.getBlueMapManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            XJCraftBaseHomeBlueMapDrawer.submit(() -> {

                // 显示自己在线时的玩家图标
                YamlConfiguration cfg = new YamlConfiguration();
                File yml = new File(XJCraftBaseHomeBlueMapDrawer.getInstance().getDataFolder(), MapConstants.ONLINE_PLAYER_CONFIG_FILE_NAME);
                try {
                    cfg.load(yml);
                } catch (IOException | InvalidConfigurationException e) {
                    Log.d("加载" + MapConstants.ONLINE_PLAYER_CONFIG_FILE_NAME + "配置文件失败！");
                    throw new XBMPluginException("加载" + MapConstants.ONLINE_PLAYER_CONFIG_FILE_NAME + "配置文件失败！", e);
                }
                String playerUUID = MojangUtil.getPlayerUUID(player.getName());

                Log.info("玩家: " + player.getName() + " ，UUID: " + playerUUID + "开启了在线图标显示。");
                Location location = player.getLocation();
                XJCraftBaseHomeBlueMapDrawer.addOnlinePlayerCache(player.getName(), new PlayerInfo(
                        player.getName(),
                        playerUUID,
                        blueMapManager.getDimensionByWorldName(location.getWorld().getName()),
                        location.getX(),
                        location.getY(),
                        location.getZ()
                ));

                try {
                    // 存储玩家
                    List<String> list = cfg.getStringList(MapConstants.ONLINE_PLAYER_CONFIG_KEY);
                    if (!list.contains(player.getName())) {
                        list.add(player.getName());
                    }
                    cfg.set(MapConstants.ONLINE_PLAYER_CONFIG_KEY, list);
                    // 存储到配置文件
                    cfg.save(yml);
                } catch (IOException e) {
                    Log.d("保存" + MapConstants.ONLINE_PLAYER_CONFIG_FILE_NAME + "配置文件失败！");
                    throw new XBMPluginException("保存" + MapConstants.ONLINE_PLAYER_CONFIG_FILE_NAME + "配置文件失败！", e);
                }

            });


            if (!blueMapManager.isEnableOnlinePlayerRender()) {
                sender.sendMessage("目前卫星地图上不显示在线玩家图标，您需要等到管理员开启后才能显示！");
            } else {
                sender.sendMessage("等会卫星地图上应该会动态显示您的位置了！间隔可能有点长，请耐心等待！");
            }
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        } else {
            sender.sendMessage("只有玩家才能执行该命令！");
        }
        return true;
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
