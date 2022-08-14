package github.shinyoki.bluemap.xjcraft.cmd.subs;

import github.shinyoki.bluemap.xjcraft.cmd.ICmd;
import github.shinyoki.bluemap.xjcraft.manager.XJCraftBlueMapContext;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args, List<String> completions) {
        return null;
    }

    @Override
    protected void editHelpMessage() {
        this.appendNewLine("/xjb reload", "重新读取配置文件");
    }

}
