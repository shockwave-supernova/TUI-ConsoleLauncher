package com.tui.next.commands.main;

import com.tui.next.commands.ExecutePack;

public interface Param {

    int[] args();
    String exec(ExecutePack pack);
    String label();

    String onNotArgEnough(ExecutePack pack, int n);
    String onArgNotFound(ExecutePack pack, int index);
}
