package com.fyxridd.lib.msg;

import com.fyxridd.lib.core.api.ConfigApi;
import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.event.ReloadConfigEvent;
import com.fyxridd.lib.msg.api.MsgApi;
import com.fyxridd.lib.msg.api.MsgPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

/**
 * 前后缀的中间层
 */
public class MsgManager implements Listener {
    private static final String PRE_SHOW_PREFIX = "PreShowPrefix";
    private static final String PRE_SHOW_SUFFIX = "PreShowSuffix";

    //配置
    public static String prefixPer, suffixPer;
    public boolean prefixAuto, suffixAuto;

    //缓存

    //称号类型 称号信息
    private HashMap<String, LevelInfo> infos = new HashMap<>();

    //玩家名 称号类型 称号信息(内的level不为null)
    private HashMap<String, HashMap<String, LevelData>> prefixLevels = new HashMap<>(), suffixLevels = new HashMap<>();

    //玩家名 当前显示的称号类型,不为null
    private HashMap<String, String> nowPrefix = new HashMap<>(), nowSuffix = new HashMap<>();

    //允许自动选择的玩家列表
    private HashSet<String> enableAutos = new HashSet<>();

    public MsgManager() {
        loadConfig();
        Bukkit.getPluginManager().registerEvents(this, MsgPlugin.instance);
    }

    @EventHandler(priority= EventPriority.LOW)
    public void onReloadConfig(ReloadConfigEvent e) {
        if (e.getPlugin().equals(MsgPlugin.pn)) loadConfig();
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        String name = e.getPlayer().getName();
        enableAutos.add(name);
        //前缀为空
        if (isNull(MsgApi.getPrefix(name))) {
            //检测先前显示的
            HashMap<String, LevelData> datas = prefixLevels.get(name);
            if (datas != null) {
                LevelData levelData = datas.get(CoreApi.getInfo(name, PRE_SHOW_PREFIX));
                if (levelData != null && isPrefix(levelData.getType())) {
                    setLevel(name, levelData.getType(), levelData.getLevel(), true, true);
                }else {
                    //检测自动选择一个
                    checkAutoSel(name, true);
                }
            }
        }
        //后缀为空
        if (isNull(MsgApi.getSuffix(name))) {
            //检测先前显示的
            HashMap<String, LevelData> datas = suffixLevels.get(name);
            if (datas != null) {
                LevelData levelData = datas.get(CoreApi.getInfo(name, PRE_SHOW_SUFFIX));
                if (levelData != null && !isPrefix(levelData.getType())) {
                    setLevel(name, levelData.getType(), levelData.getLevel(), false, true);
                }else {
                    //检测自动选择一个
                    checkAutoSel(name, false);
                }
            }
        }
    }

    @EventHandler(priority= EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        enableAutos.remove(e.getPlayer().getName());
    }

    /**
     * @see com.fyxridd.lib.msg.api.MsgApi#registerLevel(String, boolean)
     */
    public void registerLevel(String type, boolean prefix) {
        infos.put(type, new LevelInfo(type, prefix));
    }

    /**
     * @see com.fyxridd.lib.msg.api.MsgApi#getLevel(String, String)
     */
    public String getLevel(String name, String type) {
        HashMap<String, LevelData> datas = isPrefix(type)?prefixLevels.get(name):suffixLevels.get(name);
        if (datas != null) {
            LevelData levelData = datas.get(type);
            if (levelData != null) return levelData.getLevel();
        }
        return null;
    }

    /**
     * @see com.fyxridd.lib.msg.api.MsgApi#setLevel(String, String, String)
     */
    public void setLevel(String name, String type, String level) {
        boolean isPrefix = isPrefix(type);
        //初始化
        HashMap<String, LevelData> datas = isPrefix?prefixLevels.get(name):suffixLevels.get(name);
        if (datas == null) {
            datas = new HashMap<>();
            if (isPrefix) prefixLevels.put(name, datas);
            else suffixLevels.put(name, datas);
        }
        //与旧的相同
        LevelData levelData = datas.get(type);
        if (levelData != null && levelData.getLevel().equals(level)) return;
        //处理
        boolean enableAuto = enableAutos.contains(name);
        String nowType = getNowType(name, isPrefix);
        if (level != null && !level.isEmpty()) {//设置
            datas.put(type, new LevelData(type, level));
            if (type.equals(nowType)) {
                //更新当前显示
                if (enableAuto) setLevel(name, nowType, level, isPrefix, true);
            }else {
                //检测自动选择新的
                if (enableAuto) checkAutoSel(name, isPrefix);
            }
        }else {//删除
            datas.remove(type);
            if (type.equals(nowType)) {
                //删除当前显示
                if (enableAuto) setLevel(name, null, null, isPrefix, true);
                //检测自动选择新的
                if (enableAuto) checkAutoSel(name, isPrefix);
            }
        }
    }

    /**
     * @see com.fyxridd.lib.msg.api.MsgApi#getNowType(String, boolean)
     */
    public String getNowType(String name, boolean prefix) {
        if (prefix) return nowPrefix.get(name);
        return nowSuffix.get(name);
    }

    public Collection<LevelInfo> getLevelInfos() {
        return infos.values();
    }

    public Collection<LevelData> getPrefixs(String name) {
        return prefixLevels.get(name).values();
    }

    public Collection<LevelData> getSuffixs(String name) {
        return suffixLevels.get(name).values();
    }

    /**
     * 检测指定类型是否显示为前缀
     */
    public boolean isPrefix(String type) {
        LevelInfo levelInfo = infos.get(type);
        return levelInfo == null || levelInfo.isPrefix();
    }

    /**
     * 检测自动选择
     * @param name 玩家名
     * @param prefix 是否前缀
     */
    public void checkAutoSel(String name, boolean prefix) {
        if ((prefix && prefixAuto && isNull(MsgApi.getPrefix(name))) || (!prefix && suffixAuto && isNull(MsgApi.getSuffix(name)))) {
            HashMap<String, LevelData> datas = prefix?prefixLevels.get(name):suffixLevels.get(name);
            if (datas != null) {
                for (LevelData levelData:datas.values()) {
                    if (!(prefix ^ isPrefix(levelData.getType()))) {
                        setLevel(name, levelData.getType(), levelData.getLevel(), prefix, true);
                        break;
                    }
                }
            }
        }
    }

    /**
     * @param type 可为null
     * @param level 可为null
     * @param changePre 是否改变先前显示的
     */
    public void setLevel(String name, String type, String level, boolean prefix, boolean changePre) {
        if (prefix) {
            nowPrefix.put(name, type);
            MsgMain.setPrefix(name, level);
            if (changePre) CoreApi.setInfo(name, PRE_SHOW_PREFIX, type);
        }else {
            nowSuffix.put(name, type);
            MsgMain.setSuffix(name, level);
            if (changePre) CoreApi.setInfo(name, PRE_SHOW_SUFFIX, type);
        }
    }

    private boolean isNull(String s) {
        return s == null || s.isEmpty();
    }

    private void loadConfig() {
        YamlConfiguration config = ConfigApi.getConfig(MsgPlugin.pn);

        prefixPer = config.getString("prefix.per");
        suffixPer = config.getString("suffix.per");
        prefixAuto = config.getBoolean("prefix.auto");
        suffixAuto = config.getBoolean("suffix.auto");
    }
}
