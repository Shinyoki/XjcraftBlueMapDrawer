package org.xjcraft.senkosan.bluemap.cmd.subs;

import org.bukkit.command.CommandSender;
import org.xjcraft.senkosan.bluemap.cmd.ICmd;
import org.xjcraft.senkosan.bluemap.enums.MarkerType;
import org.xjcraft.senkosan.bluemap.manager.DefaultBlueMapManager;
import org.xjcraft.senkosan.bluemap.manager.XJCraftBlueMapContext;

import java.util.List;

/**
 * 删除标记集合
 * /xjb remove
 *
 * @author senko
 * @date 2022/8/15 22:06
 */
public class RemoveMarkerSetCmd extends ICmd {

    public RemoveMarkerSetCmd() {
        super("remove", "删除标记集", "remove");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {

        DefaultBlueMapManager blueMapManager = XJCraftBlueMapContext.getBlueMapManager();

        // /xjb remove
        blueMapManager.resetHomeBaseMarkerSet(MarkerType.HOME);
        blueMapManager.resetHomeBaseMarkerSet(MarkerType.BASE);
        sender.sendMessage("删除标记集需要花费一定时间后才能生效，如果迟迟没有生效，请尝试使用/xjb reload命令后再执行");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    protected void editHelpMessage() {
        this.appendNewLine("/xjb remove", "删除所有标记");
    }

}
