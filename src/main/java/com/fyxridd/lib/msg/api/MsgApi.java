package com.fyxridd.lib.msg.api;

import com.fyxridd.lib.msg.MsgMain;
import org.bukkit.entity.Player;

public class MsgApi {
    /**
     * 开启/关闭玩家的侧边栏显示
     * @param p 玩家,可为null(null或不在线时无效果)
     * @param display 是否显示
     */
    public static void setDisplaySideBar(Player p, boolean display) {
        MsgMain.setDisplaySideBar(p, display);
    }

    /**
     * 检测玩家是否显示侧边栏
     * @param p 玩家,可为null(null时返回false)
     * @return 是否显示侧边栏
     */
    public static boolean isDisplaySideBar(Player p) {
        return MsgMain.isDisplaySideBar(p);
    }

    /**
     * 设置玩家的侧边栏显示标题
     * @param p 玩家,可为null(null或不在线时无效果)
     * @param title 显示标题,超过32字符会被截断,可为null
     */
    public static void setSideShowTitle(Player p, String title) {
        MsgMain.setSideShowTitle(p, title);
    }

    /**
     * 设置玩家的侧边栏显示行内容<br>
     * 注意:行内容不能一样,否则将只显示一个
     * @param p 玩家,可为null(null或不在线时无效果)
     * @param index 行位置,从下往上排,0-(sideSize-1)
     * @param show 行内容,超过14字符会被截断,可为null
     */
    public static void setSideShowItem(Player p, int index, String show) {
        MsgMain.setSideShowItem(p, index, show);
    }

    /**
     * 设置玩家名字前缀(头上的名字)
     * @param p 玩家,可为null(null或不在线时无效果)
     * @param prefix 前缀,最长16字符,超过会被截断,null或空表示取消前缀
     */
    public static void setPrefix(Player p, String prefix) {
        MsgMain.setPrefix(p, prefix);
    }

    /**
     * 设置玩家名字后缀(头上的名字)
     * @param p 玩家,可为null(null或不在线时无效果)
     * @param suffix 后缀,最长16字符,超过会被截断,null或空表示取消后缀
     */
    public static void setSuffix(Player p, String suffix) {
        MsgMain.setSuffix(p, suffix);
    }

    /**
     * 获取玩家的前缀
     * @param name 玩家名,可为null(null时返回null)
     * @return 前缀,不存在或异常返回null
     */
    public static String getPrefix(String name) {
        return MsgMain.getPrefix(name);
    }

    /**
     * 获取玩家的后缀
     * @param name 玩家名,可为null(null时返回null)
     * @return 后缀,不存在返回null
     */
    public static String getSuffix(String name) {
        return MsgMain.getSuffix(name);
    }
}
