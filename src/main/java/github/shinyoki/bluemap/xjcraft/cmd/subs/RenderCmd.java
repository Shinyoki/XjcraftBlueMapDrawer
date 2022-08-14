package github.shinyoki.bluemap.xjcraft.cmd.subs;

import github.shinyoki.bluemap.xjcraft.cmd.ICmd;
import github.shinyoki.bluemap.xjcraft.manager.XJCraftBlueMapContext;
import github.shinyoki.bluemap.xjcraft.utils.Log;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

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

            if (null == args || args.length < 1) {
                // /xjb render
                XJCraftBlueMapContext.getBlueMapManager().renderAll();
                sender.sendMessage("BlueMap上玩家基地与家的图标渲染完成！");
                return true;
            }

            else if (args.length == 1) {
                // 只有一个参数，渲染相应的玩家基地与家的图标
                // /xjb render <player>
                XJCraftBlueMapContext.getBlueMapManager().renderMarker(args[0]);
                sender.sendMessage("玩家" + ChatColor.GOLD + args[0] + ChatColor.WHITE + "基地与家的图标渲染完成!");
                return true;
            }

        } catch (Exception e) {
            Log.error("渲染BlueMap上玩家基地与家的图标失败，错误信息：".concat(e.getMessage()));
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args, List<String> completions) {
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
