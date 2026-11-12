package com.tui.next.commands.main.raw;

import com.tui.next.R;
import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;
import com.tui.next.tuils.Tuils;

public class exit implements CommandAbstraction {
    @Override
    public String exec(ExecutePack pack) throws Exception {
        Tuils.resetPreferredLauncherAndOpenChooser(pack.context);
        return null;
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 4;
    }

    @Override
    public int helpRes() {
        return R.string.help_exit;
    }

    @Override
    public String onArgNotFound(ExecutePack pack, int indexNotFound) {
        return null;
    }

    @Override
    public String onNotArgEnough(ExecutePack pack, int nArgs) {
        return null;
    }
}
