package com.fyxridd.lib.msg;

import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.msg.api.MsgApi;
import com.fyxridd.lib.msg.api.MsgPlugin;
import com.fyxridd.lib.msg.api.SideHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * 信息获取器
 * 此信息是静态的,玩家加入游戏时设置一次,在游戏中不会改变
 */
public class InfoHandler implements SideHandler, Listener {
    private static final String HANDLER_NAME = "info";

    public InfoHandler() {
        //注册获取器
        MsgApi.registerSideHandler(HANDLER_NAME, this);
        //注册事件
        Bukkit.getPluginManager().registerEvents(this, MsgPlugin.instance);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        MsgApi.updateSideShow(e.getPlayer(), HANDLER_NAME);
    }

    /**
     * data会转换颜色字符,可用变量:
     *   {name}: 玩家名
     *   {display}: 玩家显示名
     */
    @Override
    public String get(Player p, String data) {
        return CoreApi.convert(data)
                .replace("{name}", p.getName())
                .replace("{display}", p.getDisplayName());
    }
}
