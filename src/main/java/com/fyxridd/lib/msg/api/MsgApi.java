package com.fyxridd.lib.msg.api;

import com.fyxridd.lib.msg.MsgMain;
import org.bukkit.entity.Player;

public class MsgApi {
    /**
     * 检测玩家是否显示侧边栏
     * @param p 玩家,可为null(null时返回false)
     * @return 是否显示侧边栏
     */
    public static boolean isDisplaySideBar(Player p) {
        return MsgMain.isDisplaySideBar(p);
    }

    /**
     * 开启/关闭玩家的侧边栏显示
     * @param p 玩家,可为null(null或不在线时无效果)
     * @param display 是否显示
     */
    public static void setDisplaySideBar(Player p, boolean display) {
        MsgMain.setDisplaySideBar(p, display);
    }

    /**
     * 注册侧边栏值获取器
     * @param name 获取器名,唯一
     * @param sideHandler 值获取器
     */
    public static void registerSideHandler(String name, SideHandler sideHandler) {
        MsgMain.registerSideHandler(name, sideHandler);
    }

    /**
     * 更新玩家的侧边栏显示
     * @param name 获取器名,唯一
     */
    public static void updateSideShow(Player p, String name) {
        MsgMain.updateSideShow(p, name);
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
