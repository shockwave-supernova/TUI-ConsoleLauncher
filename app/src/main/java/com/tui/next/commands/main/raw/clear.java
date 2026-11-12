package com.tui.next.commands.main.raw;

import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.tui.next.R;
import com.tui.next.UIManager;
import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;

public class clear implements CommandAbstraction {

    @Override
    public String exec(ExecutePack info) throws Exception {
        LocalBroadcastManager.getInstance(info.context.getApplicationContext()).sendBroadcast(new Intent(UIManager.ACTION_CLEAR));
        return null;
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public int helpRes() {
        return R.string.help_clear;
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
