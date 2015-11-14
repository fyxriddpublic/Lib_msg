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
    private boolean prefixAuto, suffixAuto;

    //缓存

    //称号类型 称号信息
    private HashMap<String, LevelInfo> levelInfos = new HashMap<>();

    //玩家名 称号类型 称号列表
    private HashMap<String, HashMap<String, LinkedHashSet<StringWrapper>>> levels = new HashMap<>();

    //开启自动选择的玩家名列表(修正优化)
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
        //检测(玩家没有显示称号,自动选择一个)
        if (prefixAuto && isNull(MsgApi.getPrefix(name))) {
            HashMap<String, LinkedHashSet<StringWrapper>> types = levels.get(name);
            if (types != null) {
                for (Map.Entry<String, LinkedHashSet<StringWrapper>> entry:types.entrySet()) {
                    if (isPrefix(entry.getKey()) && !entry.getValue().isEmpty()) {
                        setLevel(name, entry.getValue().iterator().next().toString(), true, true);
                        break;
                    }
                }
            }
        }
        if (suffixAuto && isNull(MsgApi.getSuffix(name))) {
            HashMap<String, LinkedHashSet<StringWrapper>> types = levels.get(name);
            if (types != null) {
                for (Map.Entry<String, LinkedHashSet<StringWrapper>> entry:types.entrySet()) {
                    if (!isPrefix(entry.getKey()) && !entry.getValue().isEmpty()) {
                        setLevel(name, entry.getValue().iterator().next().toString(), false, true);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(priority= EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent e) {
        enableAutos.remove(e.getPlayer().getName());
    }

    public Collection<LevelInfo> getLevelInfos() {
        return levelInfos.values();
    }

    public LinkedHashSet<StringWrapper> getLevels(String name, String type) {
        HashMap<String, LinkedHashSet<StringWrapper>> types = levels.get(name);
        if (types != null) {
            LinkedHashSet<StringWrapper> list = types.get(type);
            if (list != null) return list;
        }
        return new LinkedHashSet<>();
    }

    public String getTypeName(String type) {
        LevelInfo levelInfo = levelInfos.get(type);
        if (levelInfo != null) return levelInfo.getName();
        return "";
    }

    public boolean isPrefix(String type) {
        LevelInfo levelInfo = levelInfos.get(type);
        return levelInfo == null || levelInfo.isPrefix();
    }

    /**
     * @see com.fyxridd.lib.msg.api.MsgApi#registerLevel(String, String, boolean)
     */
    public void registerLevel(String type, String name, boolean prefix) {
        levelInfos.put(type, new LevelInfo(type, name, prefix));
    }

    /**
     * @see com.fyxridd.lib.msg.api.MsgApi#hasLevel(String, String, String)
     */
    public boolean hasLevel(String name, String type, String level) {
        HashMap<String, LinkedHashSet<StringWrapper>> types = levels.get(name);
        if (types != null) {
            LinkedHashSet<StringWrapper> list = types.get(type);
            if (list != null) return list.contains(new StringWrapper(level));
        }
        return false;
    }

    /**
     * @see com.fyxridd.lib.msg.api.MsgApi#addLevel(String, String, String)
     */
    public void addLevel(String name, String type, String level) {
        //初始化
        HashMap<String, LinkedHashSet<StringWrapper>> types = levels.get(name);
        if (types == null) {
            types = new HashMap<>();
            levels.put(name, types);
        }
        LinkedHashSet<StringWrapper> list = types.get(type);
        if (list == null) {
            list = new LinkedHashSet<>();
            types.put(type, list);
        }
        //已经拥有称号
        StringWrapper wrapper = new StringWrapper(level);
        if (list.contains(wrapper)) return;
        //添加
        list.add(wrapper);
        //检测先前显示的
        boolean isPrefix = isPrefix(type);
        if (isPrefix) {
            if (isNull(MsgApi.getPrefix(name)) && level.equals(CoreApi.getInfo(name, PRE_SHOW_PREFIX))) {
                setLevel(name, level, true, true);
                return;
            }
        }else {
            if (isNull(MsgApi.getSuffix(name)) && level.equals(CoreApi.getInfo(name, PRE_SHOW_SUFFIX))) {
                setLevel(name, level, false, true);
                return;
            }
        }
        //再检测自动选择显示
        if (enableAutos.contains(name)) {
            if (isPrefix) {
                if (prefixAuto && isNull(MsgApi.getPrefix(name))) {
                    setLevel(name, level, true, true);
                }
            }else {
                if (suffixAuto && isNull(MsgApi.getSuffix(name))) {
                    setLevel(name, level, false, true);
                }
            }
        }
    }

    /**
     * @see com.fyxridd.lib.msg.api.MsgApi#removeLevel(String, String, String)
     */
    public void removeLevel(String name, String type, String level) {
        HashMap<String, LinkedHashSet<StringWrapper>> types = levels.get(name);
        if (types != null) {
            LinkedHashSet<StringWrapper> list = types.get(type);
            StringWrapper wrapper = new StringWrapper(level);
            if (list != null && list.contains(wrapper)) {
                //删除
                list.remove(wrapper);
                if (list.isEmpty()) {
                    types.remove(type);
                    if (types.isEmpty()) {
                        levels.remove(name);
                    }
                }

                //检测当前显示此前后缀,删除
                boolean isPrefix = isPrefix(type);
                if (isPrefix) {
                    if (level.equals(MsgApi.getPrefix(name))) setLevel(name, null, true, false);
                }else {
                    if (level.equals(MsgApi.getSuffix(name))) setLevel(name, null, false, false);
                }
            }
        }
    }

    /**
     * @see com.fyxridd.lib.msg.api.MsgApi#clearLevels(String)
     */
    public void clearLevels(String name) {
        HashMap<String, LinkedHashSet<StringWrapper>> types = levels.get(name);
        if (types != null) {
            Set<String> clone = new HashSet<>();
            for (String type:types.keySet()) clone.add(type);
            for (String type:clone) clearLevels(name, type);
        }
    }

    /**
     * @see com.fyxridd.lib.msg.api.MsgApi#clearLevels(String, String)
     */
    public void clearLevels(String name, String type) {
        HashMap<String, LinkedHashSet<StringWrapper>> types = levels.get(name);
        if (types != null) {
            LinkedHashSet<StringWrapper> list = types.get(type);
            if (list != null) {
                LinkedHashSet<StringWrapper> copy = (LinkedHashSet<StringWrapper>) list.clone();
                for (StringWrapper level:copy) removeLevel(name, type, level.toString());
            }
        }
    }

    /**
     * @param changePre 是否改变先前显示的
     */
    public void setLevel(String name, String level, boolean prefix, boolean changePre) {
        if (prefix) {
            MsgMain.setPrefix(name, level);
            if (changePre) CoreApi.setInfo(name, PRE_SHOW_PREFIX, level);
        }else {
            MsgMain.setSuffix(name, level);
            if (changePre) CoreApi.setInfo(name, PRE_SHOW_SUFFIX, level);
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
