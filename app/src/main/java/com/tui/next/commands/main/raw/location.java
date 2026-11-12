package com.tui.next.commands.main.raw;

import android.content.Context;
import android.os.Build;

import com.tui.next.BuildConfig;
import com.tui.next.R;
import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;
import com.tui.next.commands.main.MainPack;
import com.tui.next.commands.main.specific.APICommand;
import com.tui.next.managers.TuiLocationManager;

public class location implements APICommand, CommandAbstraction {

    public static String ACTION_LOCATION_CMD_GOT = BuildConfig.APPLICATION_ID + ".loc_cmd_location";

    @Override
    public String exec(final ExecutePack pack) throws Exception {
        final Context context = ((MainPack) pack).context;

        TuiLocationManager l = TuiLocationManager.instance(context);
        if(l.locationAvailable) return "Lat: " + l.latitude + "; Long: " + l.longitude;
        else {
            l.add(ACTION_LOCATION_CMD_GOT);
        }

        return null;
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
        return R.string.help_location;
    }

    @Override
    public String onArgNotFound(ExecutePack pack, int indexNotFound) {
        return null;
    }

    @Override
    public String onNotArgEnough(ExecutePack pack, int nArgs) {
        return null;
    }

    @Override
    public boolean willWorkOn(int api) {
        return api >= Build.VERSION_CODES.GINGERBREAD;
    }
}
