package org.xjcraft.senkosan.bluemap.cmd.subs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.xjcraft.senkosan.bluemap.XJCraftBaseHomeBlueMapDrawer;
import org.xjcraft.senkosan.bluemap.cmd.ICmd;
import org.xjcraft.senkosan.bluemap.manager.DefaultBlueMapManager;
import org.xjcraft.senkosan.bluemap.manager.XJCraftBlueMapContext;
import org.xjcraft.senkosan.bluemap.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author senko
 * @date 2022/8/14 9:52
 */
public class RenderCmd extends ICmd {

    public RenderCmd() {
        super("render", "渲染BlueMap上玩家基地与家的图标", "render");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {

        try {

            DefaultBlueMapManager manager = XJCraftBlueMapContext.getBlueMapManager();
            if (null == args || args.length < 1) {
                // /xjb render
                manager.renderAll(sender);
                return true;
            } else if (args.length == 1) {
                // 只有一个参数，渲染相应的玩家基地与家的图标
                // /xjb render <player>
                String playerName = args[0].toLowerCase();
                String finalPlayerName = playerName;
                Optional<? extends Player> first = Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.getName().equalsIgnoreCase(finalPlayerName))
                        .findFirst();
                if (first.isPresent()) {
                    playerName = first.get().getName();
                }
                String finalPlayerName1 = playerName;
                sender.sendMessage("正在渲染玩家 " + finalPlayerName1 + " 的基地与家的图标...");
                XJCraftBaseHomeBlueMapDrawer.getExecutorService()
                        .submit(() -> {
                            manager.renderMarker(args[0]);
                            manager.save();
                            sender.sendMessage("玩家" + ChatColor.GOLD + finalPlayerName1 + ChatColor.WHITE + "基地与家的图标渲染完成!");
                        });
                return true;
            }

        } catch (Exception e) {
            Log.error("渲染BlueMap上玩家基地与家的图标失败，错误信息：".concat(e.getMessage()));
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (null == args || args.length < 1) {
            return null;
        }
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            // 只有一个参数，渲染相应的玩家基地与家的图标
            list.add("玩家名");
        }
        return list;
    }

    @Override
    protected void editHelpMessage() {
        this.appendNewLine("/xjb render", "渲染所有玩家基地与家的图标");
        this.appendNewLine("/xjb render <player>", "渲染指定玩家基地与家的图标");
    }

}
