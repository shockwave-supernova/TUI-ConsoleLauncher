package com.tui.next.tuils.interfaces;

import com.tui.next.commands.main.specific.RedirectCommand;

public interface OnRedirectionListener {

    void onRedirectionRequest(RedirectCommand cmd);
    void onRedirectionEnd(RedirectCommand cmd);
}
