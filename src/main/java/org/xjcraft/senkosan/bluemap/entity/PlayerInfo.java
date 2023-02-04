package org.xjcraft.senkosan.bluemap.entity;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;


/**
 * 玩家信息
 *
 * @author senko
 * @date 2023/2/4 12:54
 */
public class PlayerInfo {

    @SerializedName("name")
    private String playerName;
    @SerializedName("id")
    private String playerUUID;
    private Location location;
    private Integer word = 1;           // 0: 主世界，1: nether，2: end

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getWord() {
        return word;
    }

    public void setWord(Integer word) {
        this.word = word;
    }

    public PlayerInfo(String playerName, String playerUUID) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
    }

    public PlayerInfo(String playerName, String playerUUID, Location location) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.location = location;
    }

    public PlayerInfo(String playerName, String playerUUID, Location location, Integer word) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.location = location;
        this.word = word;
    }

}
