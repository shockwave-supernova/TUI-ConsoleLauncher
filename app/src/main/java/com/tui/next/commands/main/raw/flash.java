package com.tui.next.commands.main.raw;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tui.next.LauncherActivity;
import com.tui.next.R;
import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;
import com.tui.next.managers.flashlight.TorchManager;

public class flash implements CommandAbstraction {

    @Override
    public String exec(ExecutePack pack) {
        if (ContextCompat.checkSelfPermission(pack.context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) pack.context, new String[]{Manifest.permission.CAMERA}, LauncherActivity.COMMAND_REQUEST_PERMISSION);
            return pack.context.getString(R.string.output_waitingpermission);
        }

        TorchManager.getInstance().toggle(pack.context);
        return pack.context.getString(TorchManager.getInstance().isOn() ? R.string.output_flashon : R.string.output_flashoff);
    }

    @Override
    public int helpRes() {
        return R.string.help_flash;
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 4;
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
