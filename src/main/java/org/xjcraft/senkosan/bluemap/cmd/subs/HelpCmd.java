package org.xjcraft.senkosan.bluemap.cmd.subs;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;
import org.xjcraft.senkosan.bluemap.cmd.ICmd;
import org.xjcraft.senkosan.bluemap.cmd.XBMCommandHandler;
import org.xjcraft.senkosan.bluemap.utils.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author senko
 * @date 2022/8/14 13:23
 */
public class HelpCmd extends ICmd {
    public HelpCmd() {
        super("help", "获取提示", "help");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {

        XBMCommandHandler commandHandler = XJCraftBaseHomeBlueMapDrawer.getCommandHandler();
        if (Objects.isNull(args) || args.length == 0) {
            // /xjb help
            sender.sendMessage(commandHandler.getHelp(sender));
            return true;
        } else if (args.length > 0) {
            // /xjb help <子指令>
            String cmdName = args[0].toLowerCase();
            ICmd cmd = commandHandler.getSubCommand(cmdName);
            if (Objects.isNull(cmd) || !cmd.hasPermission(sender)) {
                sender.sendMessage(ChatColor.RED + "没有找到指令" + cmdName);
                return true;
            }
            sender.sendMessage(cmd.getCmdUsage());
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args, List<String> completions) {
        if (Objects.isNull(args) || StringUtil.isBlank(args[0])) {
            // /xjb help
            return XJCraftBaseHomeBlueMapDrawer.getCommandHandler().getSubCommandsMap().entrySet().stream()
                    .filter(entry -> entry.getValue().hasPermission(sender))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } else if (Objects.nonNull(args) && args.length >= 1 && StringUtil.isNotBlank(args[0])) {
            // /xjb help <子指令>
            return XJCraftBaseHomeBlueMapDrawer.getCommandHandler().getSubCommandsMap().entrySet().stream()
                    .filter(entry -> {
                        String cmdName = entry.getKey().toLowerCase();
                        return cmdName.startsWith(args[0].toLowerCase()) && entry.getValue().hasPermission(sender);
                    })
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    protected void editHelpMessage() {
        this.appendNewLine("/xjb help <子指令>", "获取指令的使用方法");
    }
}
