package org.xjcraft.senkosan.bluemap.cmd.subs;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.xjcraft.senkosan.bluemap.cmd.ICmd;
import org.xjcraft.senkosan.bluemap.manager.XJCraftBlueMapContext;
import org.xjcraft.senkosan.bluemap.marker.MarkerCreator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 切换Marker的渲染方式
 *
 * @author senko
 * @date 2022/8/14 10:16
 */
public class ChangeMarkerCmd extends ICmd {

    public ChangeMarkerCmd() {
        super("change", "切换Marker的渲染方式", "change");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (Objects.nonNull(args) && args.length > 0) {
            String markerCreatorName = args[0].toLowerCase();
            for (MarkerCreator creator : MarkerCreator.values()) {
                if (creator.getCreatorName().equalsIgnoreCase(markerCreatorName)) {
                    // 如果是切换到指定的渲染方式，则切换到指定的渲染方式
                    XJCraftBlueMapContext.changeMarkerCreator(creator);
                    sender.sendMessage("切换到指定的渲染方式：" + ChatColor.GREEN + creator.getCreatorName());
                    return true;
                }
            }
            // 如果没有找到指定的渲染方式，则提示错误信息
            String message = new StringBuilder("没有找到指定的渲染方式，请试着输入一个正确的渲染方式名称：" + ChatColor.GOLD)
                    .append(
                            Arrays.toString(Arrays.stream(MarkerCreator.values())
                                    .map(MarkerCreator::getCreatorName).toArray()).replace("[", "").replace("]", "")
                    )
                    .toString();
            sender.sendMessage(message);
            return true;
        }
        return false;
    }

    /**
     * /xjb change <markerCreatorName>
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args, List<String> completions) {

        if (Objects.nonNull(args) && args.length == 0) {
            // 还没有输入指令参数，则返回所有的渲染方式名称
            return Arrays.stream(MarkerCreator.values())
                    .map(MarkerCreator::getCreatorName)
                    .collect(Collectors.toList());
        }

        else if (Objects.nonNull(args) && args.length == 1) {
            String markerCreatorName = args[0].toLowerCase();
            return Arrays.stream(MarkerCreator.values())
                    .filter(creator -> creator.getCreatorName().startsWith(markerCreatorName))
                    .map(MarkerCreator::getCreatorName)
                    .collect(Collectors.toList());
        }
        return null;

    }

    @Override
    protected void editHelpMessage() {
        this.appendNewLine("/xjb change <markerCreatorName>", "切换Marker的渲染方式");
    }

}
