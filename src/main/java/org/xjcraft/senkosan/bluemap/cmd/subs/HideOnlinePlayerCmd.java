package org.xjcraft.senkosan.bluemap.cmd.subs;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;
import org.xjcraft.senkosan.bluemap.cmd.ICmd;
import org.xjcraft.senkosan.bluemap.exception.XBMPluginException;
import org.xjcraft.senkosan.bluemap.utils.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 隐藏自己在线时的玩家图标
 * /xjb hide
 *
 * @author senko
 * @date 2023/2/4 14:44
 */
public class HideOnlinePlayerCmd extends ICmd {
    public HideOnlinePlayerCmd() {
        super("hide", "隐藏自己在线时的玩家图标");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            XJCraftBaseHomeBlueMapDrawer.getScheduledExecutorService().submit(() -> {
                File yml = new File(XJCraftBaseHomeBlueMapDrawer.getInstance().getDataFolder(), "online-player.yml");
                // 删除
                YamlConfiguration cfg = new YamlConfiguration();
                try {
                    cfg.load(yml);
                    cfg.set(player.getName(), null);
                    cfg.save(yml);
                } catch (IOException | InvalidConfigurationException e) {
                    Log.d("操作online-player.yml配置文件时遭遇IO异常!");
                    throw new XBMPluginException("读取online-player.yml配置文件时遭遇IO异常!",e);
                }
            });
            XJCraftBaseHomeBlueMapDrawer.removeOnlinePlayerCache(player.getName());

            sender.sendMessage("玩家:" + player.getName() + "的在线图标已隐藏");
            player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
            return true;
        }
        sender.sendMessage("只有玩家才能执行此命令！");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    protected void editHelpMessage() {
        this.appendNewLine("/xjb hide", "隐藏自己在线时的玩家图标");
    }
}
