package org.xjcraft.senkosan.bluemap.cmd.subs;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.HtmlMarker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import org.bukkit.Bukkit;
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
import org.xjcraft.senkosan.bluemap.marker.OnlinePlayerMarkerBuilder;
import org.xjcraft.senkosan.bluemap.utils.BlueMapUtil;
import org.xjcraft.senkosan.bluemap.utils.Log;
import org.xjcraft.senkosan.bluemap.utils.MojangUtil;

import java.io.File;
import java.io.IOException;
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


            XJCraftBaseHomeBlueMapDrawer.submit(() -> {
                // 显示自己在线时的玩家图标
                YamlConfiguration cfg = new YamlConfiguration();
                File yml = new File(XJCraftBaseHomeBlueMapDrawer.getInstance().getDataFolder(), "online-player.yml");
                try {
                    cfg.load(yml);
                } catch (IOException | InvalidConfigurationException e) {
                    Log.d("加载online-player.yml配置文件失败！");
                    throw new XBMPluginException("加载online-player.yml配置文件失败！", e);
                }
                String playerUUID = MojangUtil.getPlayerUUID(player.getName());

                Log.info("玩家: " + player.getName() + " ，UUID: " + playerUUID + "的在线图标已显示。");
                Location location = player.getLocation();
                XJCraftBaseHomeBlueMapDrawer.addOnlinePlayerCache(player.getName(), new PlayerInfo(
                        player.getName(),
                        playerUUID,
                        XJCraftBlueMapContext.getBlueMapManager().getDimensionByWorldName(location.getWorld().getName()),
                        location.getX(),
                        location.getY(),
                        location.getZ()
                ));
                cfg.set(player.getName(), playerUUID);
                try {
                    // 存储到配置文件
                    cfg.save(yml);
                } catch (IOException e) {
                    Log.d("保存online-player.yml配置文件失败！");
                    throw new XBMPluginException("保存online-player.yml配置文件失败！", e);
                }

            });


            sender.sendMessage("等会卫星地图上应该会动态显示你的位置了！间隔可能有点长，请耐心等待！");
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
