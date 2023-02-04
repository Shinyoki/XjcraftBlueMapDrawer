package org.xjcraft.senkosan.bluemap.cmd.subs;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.xjcraft.senkosan.bluemap.cmd.ICmd;

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

            // TODO 隐藏自己在线时的玩家图标
            sender.sendMessage("成功隐藏自己在线时的玩家图标！");
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
