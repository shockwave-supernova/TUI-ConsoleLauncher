package com.tui.next.tuils.interfaces;

import com.tui.next.commands.main.specific.RedirectCommand;

public interface Redirectator {

    void prepareRedirection(RedirectCommand cmd);
    void cleanup();
}
