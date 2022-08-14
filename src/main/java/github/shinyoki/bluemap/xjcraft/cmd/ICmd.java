package github.shinyoki.bluemap.xjcraft.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jim.bukkit.audit.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * 子指令
 *
 * @author senko
 * @date 2022/8/14 9:33
 */
public abstract class ICmd {

    private String cmdName;
    private String cmdDesc = "";
    private String cmdPermission = "xjcraft.bluemap";

    private StringBuilder cmdUsage;

    public abstract boolean onCommand(CommandSender sender, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, String[] args, List<String> completions);

    protected abstract void editHelpMessage();


    public String getCmdName() {
        return cmdName;
    }

    public String getCmdDesc() {
        return cmdDesc;
    }

    public String getCmdPermission() {
        return cmdPermission;
    }

    public ICmd(String cmdName, String cmdPermission) {
        this.cmdName = cmdName;
        this.cmdPermission = cmdPermission;
        appendTitle();
    }

    public ICmd(String cmdName) {
        this.cmdName = cmdName;
        this.cmdPermission = this.cmdPermission.concat(".default");
        appendTitle();
    }

    public ICmd(String cmdName, String cmdDesc, String cmdPermission) {
        this.cmdName = cmdName;
        this.cmdDesc = cmdDesc;
        this.cmdPermission = this.cmdPermission.concat(".").concat(cmdName);
        appendTitle();
    }

    protected void resetHelpMessage() {
        cmdUsage = new StringBuilder();
    }

    private void appendTitle() {
        resetHelpMessage();
        cmdUsage.append("\n===============").append(ChatColor.GOLD).append(this.cmdName).append(ChatColor.WHITE).append("===============");
        this.editHelpMessage();
    }

    protected void appendNewLine(String cmdAndParams, String cmdDesc) {
        cmdUsage.append("\n").append(ChatColor.GOLD).append(cmdAndParams).append(ChatColor.WHITE).append(" - ").append(cmdDesc);
    }

    public String getCmdUsage() {
        return cmdUsage.toString();
    }

    public boolean hasPermission(CommandSender sender) {
        if (Objects.isNull(sender)) {
            return false;
        }
        return sender.hasPermission(this.getCmdPermission());
    }

}
