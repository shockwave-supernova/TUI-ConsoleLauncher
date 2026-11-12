package com.tui.next.commands.main.specific;

import java.util.ArrayList;
import java.util.List;

import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;

public abstract class RedirectCommand implements CommandAbstraction {

    public List<Object> beforeObjects = new ArrayList<>();
    public List<Object> afterObjects = new ArrayList<>();

    public abstract String onRedirect(ExecutePack pack);
    public abstract int getHint();
    public abstract boolean isWaitingPermission();

    public void cleanup() {
        beforeObjects.clear();
        afterObjects.clear();
    }
}
