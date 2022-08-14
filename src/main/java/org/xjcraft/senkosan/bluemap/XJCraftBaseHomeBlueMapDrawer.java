package org.xjcraft.senkosan.bluemap;

import de.bluecolored.bluemap.api.BlueMapAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.xjcraft.senkosan.bluemap.cmd.XBMCommandHandler;
import org.xjcraft.senkosan.bluemap.exception.XBMPluginException;
import org.xjcraft.senkosan.bluemap.manager.XJCraftBlueMapContext;
import org.xjcraft.senkosan.bluemap.utils.Log;

import java.util.Optional;

public final class XJCraftBaseHomeBlueMapDrawer extends JavaPlugin {

    private static XJCraftBaseHomeBlueMapDrawer instance;   // 插件
    private static XBMCommandHandler commandHandler;        // 指令处理器

    public static XJCraftBaseHomeBlueMapDrawer getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;
        Log.info("开始初始化，请等待完成后再进行操作...");

        // BlueMap加载好后再初始化
        BlueMapAPI.onEnable(api -> this.init());

    }

    /**
     * 重新加载配置
     * 绘制Marker
     * 添加指令执行器
     */
    private void init() {

        try {
            // 需回到插件主线程
            Bukkit.getScheduler().runTask(this, () -> {
                this.saveDefaultConfig();
                this.reloadConfig();
                checkInit();
                Log.info("绘制Markers中...");
                XJCraftBlueMapContext.getBlueMapManager().renderAll();
                Optional.ofNullable(getCommand("xjb"))
                        .ifPresent(command -> {
                            commandHandler = new XBMCommandHandler();
                            command.setExecutor(commandHandler);
                        });
                Log.info("初始化完成");
            });
        } catch (Exception ignore) {}

    }

    private void checkInit() {

        PluginManager pluginManager = Bukkit.getPluginManager();
        Optional.ofNullable(pluginManager.getPlugin("BlueMap"))
                .orElseThrow(() -> {
                    Log.error("未能正确获取BlueMap插件，请检查是否正确加载了BlueMap插件");
                    return new XBMPluginException("未能正确获取BlueMap插件，请检查是否正确加载了BlueMap插件");
                });
        Optional.ofNullable(pluginManager.getPlugin("XJCraftAudit"))
                .orElseThrow(() -> {
                    Log.error("未能正确获取XJCraftAudit插件，请检查是否正确加载了XJCraftAudit插件");
                    return new XBMPluginException("未能正确获取XJCraftAudit插件，请检查是否正确加载了XJCraftAudit插件");
                });

    }

    public static XBMCommandHandler getCommandHandler() {
        return commandHandler;
    }
}
