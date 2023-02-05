package org.xjcraft.senkosan.bluemap.entity;

import com.flowpowered.math.vector.Vector3d;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;


/**
 * 玩家信息
 *
 * @author senko
 * @date 2023/2/4 12:54
 */
public class PlayerInfo implements Cloneable{

    @SerializedName("name")
    private String playerName;
    @SerializedName("id")
    private String playerUUID;
    private double x;
    private double y;
    private double z;
    private int dimension = 0;           // 0: 主世界，1: nether，2: end

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

    public Vector3d getLocation() {
        return new Vector3d(x, y, z);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public PlayerInfo(String playerName, String playerUUID) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
    }

    public PlayerInfo(String playerName, String playerUUID, int dimension) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.dimension = dimension;
    }

    public PlayerInfo(String playerName, String playerUUID, int dimension, double x, double y, double z) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public PlayerInfo clone() {
        try {
            return (PlayerInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
