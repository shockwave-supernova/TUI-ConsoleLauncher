package com.tui.next.commands.main.raw;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import com.tui.next.R;
import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;
import com.tui.next.commands.main.MainPack;

public class wifi implements CommandAbstraction {

    @Override
    public String exec(ExecutePack pack) {
        MainPack info = (MainPack) pack;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                panelIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                info.context.startActivity(panelIntent);
            } catch (Exception e) {
                Intent wifiSettings = new Intent(Settings.ACTION_WIFI_SETTINGS);
                wifiSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                info.context.startActivity(wifiSettings);
            }
            return info.res.getString(R.string.output_wifi);
        }

        if (info.wifi == null)
            info.wifi = (WifiManager) info.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean active = !info.wifi.isWifiEnabled();
        info.wifi.setWifiEnabled(active);
        return info.res.getString(R.string.output_wifi) + " " + Boolean.toString(active);
    }

    @Override
    public int helpRes() {
        return R.string.help_wifi;
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
    public String onNotArgEnough(ExecutePack info, int nArgs) {
        return null;
    }

    @Override
    public String onArgNotFound(ExecutePack info, int index) {
        return null;
    }

}
