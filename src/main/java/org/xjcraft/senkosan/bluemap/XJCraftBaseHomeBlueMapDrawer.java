package org.xjcraft.senkosan.bluemap;

import de.bluecolored.bluemap.api.BlueMapAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.xjcraft.senkosan.bluemap.cmd.XBMCommandHandler;
import org.xjcraft.senkosan.bluemap.entity.PlayerInfo;
import org.xjcraft.senkosan.bluemap.exception.XBMPluginException;
import org.xjcraft.senkosan.bluemap.listener.AuditStatusChangeListener;
import org.xjcraft.senkosan.bluemap.listener.OnlinePlayerListener;
import org.xjcraft.senkosan.bluemap.manager.DefaultBlueMapManager;
import org.xjcraft.senkosan.bluemap.manager.OnlinePlayerCache;
import org.xjcraft.senkosan.bluemap.manager.XJCraftBlueMapContext;
import org.xjcraft.senkosan.bluemap.utils.BlueMapUtil;
import org.xjcraft.senkosan.bluemap.utils.Log;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class XJCraftBaseHomeBlueMapDrawer extends JavaPlugin {

    private static XJCraftBaseHomeBlueMapDrawer instance;   // 插件
    private static XBMCommandHandler commandHandler;        // 指令处理器
    private static ExecutorService executorService;         // 线程池
    private static ScheduledExecutorService scheduledExecutorService; // 定时任务线程池
    private static final OnlinePlayerCache onlinePlayerCache = new OnlinePlayerCache(); // 在线玩家缓存

    public static void addOnlinePlayerCache(String playerName, PlayerInfo playerInfo) {
        onlinePlayerCache.addPlayer(playerName, playerInfo);
    }

    public static void removeOnlinePlayerCache(String playerName) {
        onlinePlayerCache.removePlayer(playerName);
    }

    public static PlayerInfo getOnlinePlayerCache(String playerName) {
        return onlinePlayerCache.getOnlinePlayerCache().get(playerName);
    }

    public static boolean containsPlayer(String playerName) {
        return onlinePlayerCache.containsPlayer(playerName);
    }

    public static XJCraftBaseHomeBlueMapDrawer getInstance() {
        return instance;
    }

    public static ClassLoader getSpigotClassLoader() {
        return getInstance().getClassLoader();
    }

    @Override
    public void onLoad() {

        // 初始化线程池
        initThreadPool();
        initSchedulerThreadPool();
        // 加载配置
        this.saveDefaultConfig();
        this.reloadConfig();
        Log.setIsDebugMode(getConfig().getBoolean("senko.debug-mode"));

    }

    @Override
    public void onDisable() {

        // 关闭线程池
        closeExecutors(executorService, scheduledExecutorService);
        // 清除缓存
        onlinePlayerCache.clear();

    }

    private void closeExecutors(ExecutorService... executorService) {
        for (ExecutorService service : executorService) {
            if (Objects.nonNull(service) && !service.isShutdown()) {
                service.shutdown();

                if (!service.isShutdown()) {
                    Log.info("线程池未能关闭！");
                    service.shutdownNow();

                }
            }
        }
    }

    @Override
    public void onEnable() {

        instance = this;
        Log.info("开始初始化，请等待完成后再进行操作...");

        // BlueMap加载好后再初始化
        BlueMapAPI.onEnable(api -> {
            this.init();
            this.scheduleOnlinePlayerRender();
            Log.info("初始化完成");
        });
        BlueMapAPI.onDisable(api -> this.onDisable());

    }

    /**
     * 定时更新在线玩家的位置
     */
    private void scheduleOnlinePlayerRender() {
        // 2s 间隔
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // 找在线玩家
            DefaultBlueMapManager blueMapManager = XJCraftBlueMapContext.getBlueMapManager();
            onlinePlayerCache.getOnlinePlayerCache().forEach((playerName, playerInfo) -> {
                System.out.println("当前玩家：" + playerName + "所在区域: " + playerInfo.getDimension());
                // TODO 不执行下面的代码
                blueMapManager.renderOnlinePlayer(
                        playerName,
                        playerInfo.getPlayerUUID(),
                        playerInfo.getDimension(),
                        playerInfo.getLocation()
                );
            });
        }, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * 重新加载配置
     * 绘制Marker
     * 添加指令执行器
     */
    private void init() {

        for (BlueMapUtil.BaseIcon value : BlueMapUtil.BaseIcon.values()) {
            BlueMapUtil.checkMapIcon(value);
        }
        try {
            // 需回到插件主线程
            Bukkit.getScheduler().runTask(this, () -> {
                // 重载配置文件
                this.saveDefaultConfig();
                this.reloadConfig();

                // 判断插件是否已经加载好
                checkInit();

                // 初始化需要渲染的玩家
                initOnlinePlayerCache();

                Log.info("绘制Markers中...");

                // 渲染所有玩家的 家&基地Markers
                XJCraftBlueMapContext.resetManager();
                XJCraftBlueMapContext.getBlueMapManager()
                        .renderAllHomeBaseMarker(null);

                // 注册指令执行器
                Optional.ofNullable(getCommand("xjb"))
                        .ifPresent(command -> {
                            commandHandler = new XBMCommandHandler();
                            command.setExecutor(commandHandler);
                        });

                // 添加监听器
                PluginManager pluginManager = Bukkit.getPluginManager();
                pluginManager.registerEvents(new AuditStatusChangeListener(), this);        // 认证状态变更监听器
                pluginManager.registerEvents(new OnlinePlayerListener(), this);               // 玩家加入监听器
                Log.info("初始化完成");
            });
        } catch (Exception ignore) {
        }

    }

    /**
     * 从配置文件中读取需要被渲染的玩家和UUID
     */
    private void initOnlinePlayerCache() {

        File onlinePlayerYml = new File(getDataFolder(), "online-player.yml");
        if (!onlinePlayerYml.exists()) {
            try {
                onlinePlayerYml.createNewFile();
                Log.d("当前没有在线玩家缓存文件，已创建。");
                return;
            } catch (IOException e) {
                Log.error("创建在线玩家缓存文件失败 :/");
                e.printStackTrace();
            }
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(onlinePlayerYml);
        Collection<? extends Player> serverOnlinePlayers = Bukkit.getOnlinePlayers();
        DefaultBlueMapManager blueMapManager = XJCraftBlueMapContext.getBlueMapManager();

        for (Player serverOnlinePlayer : serverOnlinePlayers) {
            // 根据在线玩家寻找是否也存在于配置文件中
            String uuid = cfg.getString(serverOnlinePlayer.getName());
            if (Objects.isNull(uuid) || uuid.trim().equals("")) {
                // 不存在则该玩家不需要被渲染
                continue;
            }
            Location location = serverOnlinePlayer.getLocation();
            // 添加至缓存
            onlinePlayerCache.addPlayer(serverOnlinePlayer.getName(),
                    new PlayerInfo(
                            serverOnlinePlayer.getName(),
                            uuid,
                            blueMapManager.getDimensionByWorldName(serverOnlinePlayer.getWorld().getName()),
                            location.getX(),
                            location.getY(),
                            location.getZ()
                    ));
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
                thread.setDaemon(true);
                thread.setName("XJCraftBaseHomeBlueMapDrawer-Thread");
                return thread;
            });
        }
        return executorService;

    }

    private static synchronized ScheduledExecutorService initSchedulerThreadPool() {

        if (Objects.isNull(scheduledExecutorService)) {
            // 初始化线程池
            scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("XJCraftBaseHomeBlueMapDrawer-ScheduledThread");
                return thread;
            });
        }
        return scheduledExecutorService;

    }

    public static synchronized ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService == null ? initSchedulerThreadPool() : scheduledExecutorService;
    }

    public static synchronized ExecutorService getExecutorService() {
        return executorService == null ? initThreadPool() : executorService;
    }

    public static void submit(Runnable runnable) {
        getExecutorService()
                .submit(runnable);
    }

}
