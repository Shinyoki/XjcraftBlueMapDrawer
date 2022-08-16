package org.xjcraft.senkosan.bluemap;

import de.bluecolored.bluemap.api.BlueMapAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.xjcraft.senkosan.bluemap.cmd.XBMCommandHandler;
import org.xjcraft.senkosan.bluemap.exception.XBMPluginException;
import org.xjcraft.senkosan.bluemap.listener.AuditStatusChangeListener;
import org.xjcraft.senkosan.bluemap.manager.XJCraftBlueMapContext;
import org.xjcraft.senkosan.bluemap.utils.Log;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class XJCraftBaseHomeBlueMapDrawer extends JavaPlugin {

    private static XJCraftBaseHomeBlueMapDrawer instance;   // 插件
    private static XBMCommandHandler commandHandler;        // 指令处理器
    private static ExecutorService executorService;         // 线程池

    public static XJCraftBaseHomeBlueMapDrawer getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {

        // 初始化线程池
        initThreadPool();
        // 加载配置
        this.saveDefaultConfig();
        this.reloadConfig();
        Log.setIsDebugMode(getConfig().getBoolean("senko.debug-mode"));

    }

    @Override
    public void onDisable() {

        if (Objects.nonNull(executorService)) {
            Log.info("关闭线程池中...");
            executorService.shutdown();

            if (executorService.isShutdown()) {
                Log.info("线程池已关闭！");

            } else {
                Log.info("线程池未能关闭！");
                executorService.shutdownNow();

            }
        }

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
                // 重载配置文件
                this.saveDefaultConfig();
                this.reloadConfig();

                // 判断插件是否已经加载好
                checkInit();

                Log.info("绘制Markers中...");

                // 删除以前的MarkerSet
                XJCraftBlueMapContext.getBlueMapManager()
                        .removeAllMarkerSet(null);

                // 渲染所有玩家的Markers
                XJCraftBlueMapContext.getBlueMapManager()
                        .renderAll(null);

                // 注册指令执行器
                Optional.ofNullable(getCommand("xjb"))
                        .ifPresent(command -> {
                            commandHandler = new XBMCommandHandler();
                            command.setExecutor(commandHandler);
                        });

                // 添加监听器
                Bukkit.getPluginManager().registerEvents(new AuditStatusChangeListener(), this);
                Log.info("初始化完成");
            });
        } catch (Exception ignore) {
        }

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

    private static synchronized ExecutorService initThreadPool() {

        if (Objects.isNull(executorService)) {
            // 初始化线程池
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
                Thread thread = new Thread(r);
                thread.setName("XJCraftBaseHomeBlueMapDrawer-Thread");
                return thread;
            });
        }
        return executorService;

    }

    public static synchronized ExecutorService getExecutorService() {
        return executorService == null ? initThreadPool() : executorService;
    }
}
