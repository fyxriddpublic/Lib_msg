package com.fyxridd.lib.msg;

import com.fyxridd.lib.core.api.*;
import com.fyxridd.lib.core.api.inter.*;
import com.fyxridd.lib.core.show.ShowManager;
import com.fyxridd.lib.msg.api.MsgApi;
import com.fyxridd.lib.msg.api.MsgPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MsgFunc implements FunctionInterface, ShowInterface {
    private static final String FUNC_NAME = "Msg";
    private static final String PAGE_TYPES = "Types";
    private static final String PAGE_LEVELS = "Levels";
    private static final String SHORT_SEL = "st_sel";

    public MsgFunc() {
        FuncApi.register(this);
    }

    @Override
    public String getName() {
        return FUNC_NAME;
    }

    @Override
    public boolean isOn(String name, String data) {
        return true;
    }

    /**
     * 'a' 查看前后缀类型列表
     * 'b 类型' 查看指定前后缀类型的称号列表
     * 'c 类型 称号' 选择显示称号(称号可包含空格)
     * 'd' 取消显示前缀
     * 'e' 取消显示后缀
     */
    @Override
    public void onOperate(Player p, String... args) {
        try {
            if (args.length >= 3) {
                if (args[0].equalsIgnoreCase("c")) {//选择显示称号(称号可包含空格)
                    String level = CoreApi.combine(args, " ", 2, args.length).replace("\u00A6", "\u00A7");
                    selShow(p, args[1], level);
                    return;
                }
            }
            switch (args.length) {
                case 1:
                    if (args[0].equalsIgnoreCase("a")) {//查看前后缀类型列表
                        showTypes(p, null);
                        return;
                    }else if (args[0].equalsIgnoreCase("d")) {//取消显示前缀
                        cancel(p, true);
                        return;
                    }else if (args[0].equalsIgnoreCase("e")) {//取消显示后缀
                        cancel(p, false);
                        return;
                    }
                    break;
                case 2:
                    if (args[0].equalsIgnoreCase("b")) {//查看指定前后缀类型的称号列表
                        //检测权限
                        if (!checkPer(p, args[1])) return;
                        //显示
                        showLevels(p, args[1], null);
                        return;
                    }
                    break;
            }
        } catch (Exception e) {
            //操作异常
            ShowApi.tip(p, get(10), true);
            return;
        }
        //输入格式错误
        ShowApi.tip(p, get(15), true);
    }

    @Override
    public void show(PlayerContext pc) {
        if (pc.obj != null) {
            String[] args = pc.obj.toString().split(" ");
            switch (args.length) {
                case 1:
                    if (args[0].equalsIgnoreCase("a")) showTypes(pc.p, pc);
                    break;
                case 2:
                    if (args[0].equalsIgnoreCase("b")) showLevels(pc.p, args[1], pc);
                    break;
            }
        }
    }

    private void showTypes(Player p, PlayerContext pc) {
        //list
        ShowList showList = ShowApi.getShowList(2, MsgMain.msgManager.getLevelInfos(), LevelInfo.class);
        //map
        HashMap<String, Object> map = new HashMap<>();
        String prefix = MsgApi.getPrefix(p.getName());
        String suffix = MsgApi.getSuffix(p.getName());
        map.put("prefix", prefix == null?"":prefix);
        map.put("suffix", suffix == null?"":suffix);
        //显示
        if (pc != null) ShowManager.show(this, "a", p, MsgPlugin.pn, PAGE_TYPES, showList, map, pc.pageNow, pc.listNow, null, null);
        else ShowManager.show(this, "a", p, MsgPlugin.pn, PAGE_TYPES, showList, map, 1, 1, null, null);
    }

    private void showLevels(Player p, String type, PlayerContext pc) {
        //list
        ShowList showList = ShowApi.getShowList(2, MsgMain.msgManager.getLevels(p.getName(), type), StringWrapper.class);
        //map
        HashMap<String, Object> map = new HashMap<>();
        map.put("prefix", MsgMain.msgManager.isPrefix(type));
        map.put("type", type);
        map.put("typeName", MsgMain.msgManager.getTypeName(type));
        //显示
        if (pc != null) ShowManager.show(this, "b "+type, p, MsgPlugin.pn, PAGE_LEVELS, showList, map, pc.pageNow, pc.listNow, null, null);
        else ShowManager.show(this, "b "+type, p, MsgPlugin.pn, PAGE_LEVELS, showList, map, 1, 1, null, null);
    }

    private void selShow(Player p, String type, String level) {
        //检测权限
        if (!checkPer(p, type)) return;
        //速度检测
        if (!SpeedApi.checkShort(p, MsgPlugin.pn, SHORT_SEL, 3)) return;
        //无此称号
        boolean isPrefix = MsgMain.msgManager.isPrefix(type);
        if (!MsgMain.msgManager.hasLevel(p.getName(), type, level)) {
            if (isPrefix) ShowApi.tip(p, get(40), true);
            else ShowApi.tip(p, get(45), true);
            return;
        }
        //与当前显示的称号相同
        if ((isPrefix && level.equals(MsgApi.getPrefix(p.getName()))) || (!isPrefix && level.equals(MsgApi.getSuffix(p.getName())))) {
            ShowApi.tip(p, get(70), true);
            return;
        }
        //选择显示成功
        if (isPrefix) MsgMain.setPrefix(p.getName(), level);
        else MsgMain.setSuffix(p.getName(), level);
        //提示
        if (isPrefix) ShowApi.tip(p, get(50, level), true);
        else ShowApi.tip(p, get(55, level), true);
    }

    private void cancel(Player p, boolean prefix) {
        //检测权限
        if (!checkPer(p, prefix)) return;
        //速度检测
        if (!SpeedApi.checkShort(p, MsgPlugin.pn, SHORT_SEL, 3)) return;
        //取消显示成功
        if (prefix) MsgMain.setPrefix(p.getName(), null);
        else MsgMain.setSuffix(p.getName(), null);
        //提示
        if (prefix) ShowApi.tip(p, get(60), true);
        else ShowApi.tip(p, get(65), true);
    }

    /**
     * 检测权限
     */
    private boolean checkPer(Player p, String type) {
        return checkPer(p, MsgMain.msgManager.isPrefix(type));
    }

    /**
     * 检测权限
     */
    private boolean checkPer(Player p, boolean prefix) {
        if (prefix) {
            if (!PerApi.checkPer(p, MsgManager.prefixPer)) return false;
        }else {
            if (!PerApi.checkPer(p, MsgManager.suffixPer)) return false;
        }
        return true;
    }

    private static FancyMessage get(int id, Object... args) {
        return FormatApi.get(MsgPlugin.pn, id, args);
    }
}
