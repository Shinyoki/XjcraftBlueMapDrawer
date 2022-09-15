package org.xjcraft.senkosan.bluemap.cmd.subs;

import org.bukkit.command.CommandSender;
import org.xjcraft.senkosan.bluemap.cmd.ICmd;
import org.xjcraft.senkosan.bluemap.manager.XJCraftBlueMapContext;

import java.util.List;

/**
 * 重新读取配置文件
 *
 * @author senko
 * @date 2022/8/14 9:38
 */
public class ReloadConfigCmd extends ICmd {

    public ReloadConfigCmd() {
        super("reload", "重新读取配置文件", "reload");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        XJCraftBlueMapContext.reloadConfig();
        sender.sendMessage("成功重新读取配置文件！如需立即生效，请使用/xjb render指令！");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    protected void editHelpMessage() {
        this.appendNewLine("/xjb reload", "重新读取配置文件");
    }

}
