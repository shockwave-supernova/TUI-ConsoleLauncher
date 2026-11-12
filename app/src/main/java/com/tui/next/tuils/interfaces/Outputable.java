package com.tui.next.tuils.interfaces;

public interface Outputable {
    void onOutput(CharSequence output, int category);
    void onOutput(int color, CharSequence output);
    void onOutput(CharSequence output);
    void dispose();
}
