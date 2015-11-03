package com.fyxridd.lib.msg.api;

import com.fyxridd.lib.msg.MsgMain;
import org.bukkit.entity.Player;

public class MsgApi {
    /**
     * 注册称号类型
     * @param type 称号类型
     * @param name 称号名称(显示用)
     * @param prefix true表示显示在前缀;false表示显示在后缀
     */
    public static void registerLevel(String type, String name, boolean prefix) {
        MsgMain.msgManager.registerLevel(type, name, prefix);
    }

    /**
     * 给玩家添加称号
     * @param name 玩家名
     * @param type 称号类型
     * @param level 称号,最长16字符,超过会被截断
     */
    public static void addLevel(String name, String type, String level) {
        MsgMain.msgManager.addLevel(name, type, level);
    }

    /**
     * 检测玩家是否拥有称号
     * @param name 玩家名
     * @param type 称号类型
     * @param level 称号
     * @return 是否拥有称号
     */
    public static boolean hasLevel(String name, String type, String level) {
        return MsgMain.msgManager.hasLevel(name, type, level);
    }

    /**
     * 给玩家删除称号
     * @param name 玩家名
     * @param type 称号类型
     * @param level 称号,最长16字符,超过会被截断
     */
    public static void removeLevel(String name, String type, String level) {
        MsgMain.msgManager.removeLevel(name, type, level);
    }

    /**
     * 清空玩家的所有称号
     * @param name 玩家
     */
    public static void clearLevels(String name) {
        MsgMain.msgManager.clearLevels(name);
    }

    /**
     * 清空玩家指定类型的所有称号
     * @param name 玩家
     * @param type 称号类型
     */
    public static void clearLevels(String name, String type) {
        MsgMain.msgManager.clearLevels(name, type);
    }

    /**
     * 获取玩家当前显示的前缀
     * @param name 玩家名,可为null(null时返回null)
     * @return 前缀,不存在或异常返回null或""
     */
    public static String getPrefix(String name) {
        return MsgMain.getPrefix(name);
    }

    /**
     * 获取玩家当前显示的后缀
     * @param name 玩家名,可为null(null时返回null)
     * @return 后缀,不存在或异常返回null或""
     */
    public static String getSuffix(String name) {
        return MsgMain.getSuffix(name);
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
}
