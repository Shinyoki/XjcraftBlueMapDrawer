package org.xjcraft.senkosan.bluemap.manager;

import org.xjcraft.senkosan.bluemap.entity.PlayerInfo;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 需要渲染的在线玩家缓存，UUID为官网查询用户名得到的，而非直接从对象中获取
 *
 * @author senko
 * @date 2023/2/4 18:52
 */
public class OnlinePlayerCache {

    // playerName : UUID
    private ConcurrentHashMap<String, PlayerInfo> onlinePlayerCache = new ConcurrentHashMap<>();

    public void addPlayer(String playerName, PlayerInfo playerInfo) {
        onlinePlayerCache.put(playerName, playerInfo);
    }

    public void removePlayer(String playerName) {
        onlinePlayerCache.remove(playerName);
    }

    public ConcurrentHashMap<String, PlayerInfo> getOnlinePlayerCache() {
        return onlinePlayerCache;
    }

    public void setOnlinePlayerCache(ConcurrentHashMap<String, PlayerInfo> onlinePlayerCache) {
        this.onlinePlayerCache = onlinePlayerCache;
    }

    public String getUuid(String playerName) {
        PlayerInfo playerInfo = onlinePlayerCache.get(playerName);
        if (Objects.nonNull(playerInfo)) {
            return playerInfo.getPlayerUUID();
        }
        return null;
    }

    public boolean containsPlayer(String playerName) {
        return onlinePlayerCache.containsKey(playerName);
    }

    public boolean containsUuid(String uuid) {
        boolean res = false;
        for (PlayerInfo playerInfo : onlinePlayerCache.values()) {
            if (playerInfo.getPlayerUUID().equals(uuid)) {
                res = true;
                break;
            }
        }
        return res;
    }

    public void clear() {
        onlinePlayerCache.clear();
    }

}
