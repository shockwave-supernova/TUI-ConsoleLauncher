package com.tui.next.commands.main.raw;

import android.content.Intent;
import android.net.Uri;

import com.tui.next.R;
import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;
import com.tui.next.commands.main.MainPack;

public class donate implements CommandAbstraction {

    private final String DONATE_URL = "https://www.paypal.me/fandreuzzi";

    @Override
    public String exec(ExecutePack pack) {
        MainPack info = (MainPack) pack;
        Intent donateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(DONATE_URL));
        info.context.startActivity(donateIntent);
        return info.res.getString(R.string.output_rate);
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public int helpRes() {
        return R.string.help_donate;
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
