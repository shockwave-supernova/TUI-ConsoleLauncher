package com.tui.next.commands.main.raw;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tui.next.LauncherActivity;
import com.tui.next.R;
import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;
import com.tui.next.commands.main.MainPack;

public class bluetooth implements CommandAbstraction {

    @Override
    public String exec(ExecutePack pack) {
        MainPack info = (MainPack) pack;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(info.context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(info.context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        (Activity) info.context,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                        LauncherActivity.COMMAND_REQUEST_PERMISSION
                );
                return info.context.getString(R.string.output_waitingpermission);
            }
        }

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter == null) return info.context.getString(R.string.output_bluetooth_unavailable);

        try {
            if (adapter.isEnabled()) {
                boolean disabled = adapter.disable();
                if (!disabled) {
                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    info.context.startActivity(intent);
                }
                return info.context.getString(R.string.output_bluetooth) + " false";
            } else {
                boolean enabled = adapter.enable();
                if (!enabled) {
                    try {
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        enableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        info.context.startActivity(enableIntent);
                    } catch (Exception e) {
                        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        info.context.startActivity(intent);
                    }
                }
                return info.context.getString(R.string.output_bluetooth) + " true";
            }
        } catch (SecurityException e) {
            return info.context.getString(R.string.output_nopermissions);
        }
    }

    @Override
    public int helpRes() {
        return R.string.help_bluetooth;
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
