package org.xjcraft.senkosan.bluemap.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.xjcraft.senkosan.bluemap.cmd.subs.*;
import org.xjcraft.senkosan.bluemap.utils.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 插件的指令执行器
 *
 * @author senko
 * @date 2022/8/14 9:30
 */
public class XBMCommandHandler implements TabExecutor {

    /**
     * 子指令集合
     * key：全是小写
     */
    private Map<String, ICmd> subCommands = new HashMap<>() {{
        put("reload", new ReloadConfigCmd());
        put("render", new RenderCmd());
        put("remove", new RemoveMarkerSetCmd());
//        put("change", new ChangeMarkerCmd());  // TODO 重构
        put("help", new HelpCmd());
    }};

    public ICmd getSubCommand(String cmdName) {
        return subCommands.get(cmdName);
    }

    public ICmd registerSubCommand(String cmdName, ICmd cmd) {
        return subCommands.put(cmdName, cmd);
    }

    public ICmd unregisterSubCommand(String cmdName) {
        return subCommands.remove(cmdName);
    }

    public Map<String, ICmd> getSubCommandsMap() {
        return subCommands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0 || StringUtil.isBlank(args[0])) {
            sender.sendMessage(getHelp(sender));
            return true;
        } else if (args.length > 0) {

            // 子指令参数
            String[] subArgs = null;
            if (args.length >= 2) {
                // 去除args的第一个元素，将剩下的元素赋值给subArgs
                subArgs = new String[args.length - 1];
                System.arraycopy(args, 1, subArgs, 0, subArgs.length);
            }

            // 子指令名称
            String subCmdName = args[0];
            Optional<ICmd> cmdOpt = Optional.ofNullable(subCommands.get(subCmdName));
            if (!cmdOpt.isPresent()) {
                // 没有找到子指令
                sender.sendMessage("未知的子指令：" + subCmdName);
                sender.sendMessage(getHelp(sender));
                return true;
            }

            // 鉴权
            ICmd subC = cmdOpt.get();
            if (subC.hasPermission(sender)) {
                if (!subC.onCommand(sender, subArgs)) {
                    sender.sendMessage(getHelp(subCmdName));
                }
            } else {
                sender.sendMessage(ChatColor.RED + "你没有权限执行" + subC.getCmdName() + "指令！");
            }
            return true;
        }
        sender.sendMessage(getHelp(sender));
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args[0].isEmpty()) {
            return new ArrayList<>(this.subCommands.entrySet().stream()
                    .filter(entry -> {
                        String cmdName = entry.getKey();
                        return cmdName.startsWith(args[0].toLowerCase()) && entry.getValue().hasPermission(sender);
                    })
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList()));
        }

        List<String> res = new ArrayList<>();
        if (args.length >= 1) {

            // 子指令参数
            String[] subArgs = null;
            if (args.length >= 2) {
                // 去除args的第一个元素，将剩下的元素赋值给subArgs
                subArgs = new String[args.length - 1];
                System.arraycopy(args, 1, subArgs, 0, subArgs.length);
            }

            // 子指令名称
            String subC = args[0].toLowerCase();
            for (Map.Entry<String, ICmd> entry : this.subCommands.entrySet()) {
                if (entry.getValue().hasPermission(sender)) {
                    if (entry.getKey().equalsIgnoreCase(subC)) {
                        // 完全匹配
                        return entry.getValue().onTabComplete(sender, subArgs);
                    }
                    if (entry.getKey().toLowerCase().startsWith(subC)) {
                        // 匹配开头
                        res.add(entry.getKey());
                    }
                }
            }
        }
        return res;
    }

    public String getHelp(CommandSender sender) {
        StringBuilder sb = new StringBuilder(ChatColor.GRAY.toString()).append("\n=========").append(ChatColor.DARK_GREEN).append(" XJCraft-BlueMap-Adapter-Plugin").append(ChatColor.GRAY).append(" =========\n");
        for (Map.Entry<String, ICmd> entry : this.subCommands.entrySet()) {
            if (entry.getValue().hasPermission(sender)) {
                if (entry.getValue().getCmdName().equals("help")) {
                    sb.append(ChatColor.GOLD).append("/xjb ").append(entry.getKey()).append(" <子指令名>").append(ChatColor.WHITE).append(" -").append(entry.getValue().getCmdDesc()).append("\n");
                } else {
                    sb.append(ChatColor.GOLD).append("/xjb ").append(entry.getKey()).append(ChatColor.WHITE).append(" -").append(entry.getValue().getCmdDesc()).append("\n");
                }
            }
        }
        return sb.toString();
    }

    public String getHelp(String cmdName) {
        return getSubCommand(cmdName)
                .getCmdUsage();
    }

    public String getSubCmdsUsages() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append(ChatColor.GRAY.toString()).append("==============").append(ChatColor.GOLD).append(" XJCraft-BlueMap-Adapter-Plugin").append(ChatColor.GRAY).append(" ==============\n");
        getSubCommandsMap().forEach((key, value) -> {
            if (!key.equalsIgnoreCase("help")) {
                sb.append(ChatColor.GOLD).append("/xjb ").append(key).append(ChatColor.WHITE).append(" - ").append(value.getCmdDesc()).append("\n");
            } else {
                sb.append(ChatColor.GOLD).append("/xjb ").append(key).append(" <子指令> ").append(ChatColor.WHITE).append(" - ").append(value.getCmdDesc()).append("\n");
            }
        });
        return sb.toString();
    }

}
