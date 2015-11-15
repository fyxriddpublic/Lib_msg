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
    private static final String PAGE_MAIN = "Main";
    private static final String PAGE_PREFIX = "Prefix";
    private static final String PAGE_SUFFIX = "Suffix";
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
     * 'a' 查看主界面
     * 'b' 查看前缀列表
     * 'c' 查看后缀列表
     * 'd 类型' 选择显示称号
     * 'e' 取消显示前缀
     * 'f' 取消显示后缀
     */
    @Override
    public void onOperate(Player p, String... args) {
        try {
            switch (args.length) {
                case 1:
                    if (args[0].equalsIgnoreCase("a")) {//查看主界面
                        showMain(p, null);
                        return;
                    }else if (args[0].equalsIgnoreCase("b")) {//查看前缀列表
                        //权限检测
                        if (!checkPer(p, true)) return;
                        //显示
                        showPrefix(p, null);
                        return;
                    }else if (args[0].equalsIgnoreCase("c")) {//查看后缀列表
                        //权限检测
                        if (!checkPer(p, false)) return;
                        //显示
                        showSuffix(p, null);
                        return;
                    }else if (args[0].equalsIgnoreCase("e")) {//取消显示前缀
                        cancel(p, true);
                        return;
                    }else if (args[0].equalsIgnoreCase("f")) {//取消显示后缀
                        cancel(p, false);
                        return;
                    }
                    break;
                case 2:
                    if (args[0].equalsIgnoreCase("d")) {//选择显示称号
                        selShow(p, args[1]);
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
                    if (args[0].equalsIgnoreCase("a")) showMain(pc.p, pc);
                    else if (args[0].equalsIgnoreCase("b")) showPrefix(pc.p, pc);
                    else if (args[0].equalsIgnoreCase("c")) showSuffix(pc.p, pc);
                    break;
            }
        }
    }

    private void showMain(Player p, PlayerContext pc) {
        //map
        HashMap<String, Object> map = new HashMap<>();
        String prefix = MsgApi.getPrefix(p.getName());
        String suffix = MsgApi.getSuffix(p.getName());
        map.put("prefix", prefix == null?"":prefix);
        map.put("suffix", suffix == null?"":suffix);
        //显示
        if (pc != null) ShowManager.show(this, "a", p, MsgPlugin.pn, PAGE_MAIN, null, map, pc.pageNow, pc.listNow, null, null);
        else ShowManager.show(this, "a", p, MsgPlugin.pn, PAGE_MAIN, null, map, 1, 1, null, null);
    }

    private void showPrefix(Player p, PlayerContext pc) {
        //list
        ShowList showList = ShowApi.getShowList(2, MsgMain.msgManager.getPrefixs(p.getName()), LevelData.class);
        //map
        HashMap<String, Object> map = new HashMap<>();
        String type = MsgMain.msgManager.getNowType(p.getName(), true);
        map.put("type", type != null?type:"");
        //显示
        if (pc != null) ShowManager.show(this, "b", p, MsgPlugin.pn, PAGE_PREFIX, showList, map, pc.pageNow, pc.listNow, null, null);
        else ShowManager.show(this, "b", p, MsgPlugin.pn, PAGE_PREFIX, showList, map, 1, 1, null, null);
    }

    private void showSuffix(Player p, PlayerContext pc) {
        //list
        ShowList showList = ShowApi.getShowList(2, MsgMain.msgManager.getSuffixs(p.getName()), LevelData.class);
        //map
        HashMap<String, Object> map = new HashMap<>();
        String type = MsgMain.msgManager.getNowType(p.getName(), false);
        map.put("type", type != null?type:"");
        //显示
        if (pc != null) ShowManager.show(this, "c", p, MsgPlugin.pn, PAGE_SUFFIX, showList, map, pc.pageNow, pc.listNow, null, null);
        else ShowManager.show(this, "c", p, MsgPlugin.pn, PAGE_SUFFIX, showList, map, 1, 1, null, null);
    }

    private void selShow(Player p, String type) {
        //检测权限
        if (!checkPer(p, type)) return;
        //速度检测
        if (!SpeedApi.checkShort(p, MsgPlugin.pn, SHORT_SEL, 3)) return;
        //与当前显示相同
        boolean isPrefix = MsgMain.msgManager.isPrefix(type);
        if (type.equals(MsgApi.getNowType(p.getName(), isPrefix))) {
            ShowApi.tip(p, get(70), true);
            return;
        }
        //无此称号
        String level = MsgApi.getLevel(p.getName(), type);
        if (level == null) {
            if (isPrefix) ShowApi.tip(p, get(40), true);
            else ShowApi.tip(p, get(45), true);
            return;
        }
        //选择显示成功
        MsgMain.msgManager.setLevel(p.getName(), type, level, isPrefix, true);
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
        MsgMain.msgManager.setLevel(p.getName(), null, null, prefix, true);
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
