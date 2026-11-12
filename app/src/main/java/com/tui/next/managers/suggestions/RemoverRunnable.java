package com.tui.next.managers.suggestions;

import android.widget.LinearLayout;

public class RemoverRunnable implements Runnable {

    public boolean stop = false, isGoingToRun = false;

    public LinearLayout suggestionsView;

    public RemoverRunnable(LinearLayout suggestionsView) {
        this.suggestionsView = suggestionsView;
    }

    @Override
    public void run() {
        if(stop) {
            stop = false;
        } else suggestionsView.removeAllViews();

        isGoingToRun = false;
    }
}
