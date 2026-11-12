package com.tui.next.commands.tuixt.raw;

import android.app.Activity;

import com.tui.next.R;
import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;
import com.tui.next.commands.tuixt.TuixtPack;

public class exit implements CommandAbstraction {

    @Override
    public String exec(ExecutePack info) throws Exception {
        TuixtPack pack = (TuixtPack) info;

        ((Activity) pack.context).finish();
        return null;
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public int helpRes() {
        return R.string.help_tuixt_exit;
    }

    @Override
    public String onArgNotFound(ExecutePack info, int index) {
        return null;
    }

    @Override
    public String onNotArgEnough(ExecutePack info, int nArgs) {
        return null;
    }
}
