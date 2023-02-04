package org.xjcraft.senkosan.bluemap.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.xjcraft.senkosan.bluemap.exception.XBMPluginException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 获取有关Mojang的信息
 *
 * @author senko
 * @date 2023/2/4 12:05
 */
public class MojangUtil {

    // 玩家名查询玩家信息api
    public static final String MOJANG_API = "https://api.mojang.com/users/profiles/minecraft/";
    // uuid获取头像URL api
    public static final String PLAYER_HEAD_API = "https://crafatar.com/avatars/";

    /**
     * 根据玩家名获取玩家UUID信息
     * @param playerName    玩家名
     * @return              玩家信息
     */
    public static String getPlayerUUID(String playerName) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(MOJANG_API + playerName);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.setUseCaches(false);
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String result = StreamUtil.read(connection.getInputStream());
                result = new Gson().fromJson(result, JsonObject.class).get("id").getAsString();
                Log.d("得到玩家" + playerName + "的uuid：" + result);
                return result;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new XBMPluginException("获取玩家皮肤信息时出错，请检查网络连接是否正常", e);
        }
    }

    /**
     * 获取玩家头像URL
     * @param playerName    玩家名
     * @return              头像URL
     */
    public static String getPlayerHeadUrl(String playerName) {
        String uuid = getPlayerUUID(playerName);
        if (uuid == null || uuid.trim().equals("")) {
            // BlueMap默认的头像，文件位于 服务端/bluemap/web/ 文件夹
            return "assets/steve.png";
        }
        return PLAYER_HEAD_API + uuid;
    }


}
