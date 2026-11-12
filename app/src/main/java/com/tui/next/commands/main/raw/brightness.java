package com.tui.next.commands.main.raw;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import com.tui.next.R;
import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;

import static android.provider.Settings.System.SCREEN_BRIGHTNESS;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;

public class brightness implements CommandAbstraction {
    @Override
    public String exec(final ExecutePack pack) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(pack.context)) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(android.net.Uri.parse("package:" + pack.context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                pack.context.startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                pack.context.startActivity(intent);
            }
            return pack.context.getString(R.string.output_waitingpermission);
        }

        final int brightness = pack.getInt();

        ((Activity) pack.context).runOnUiThread(() -> {
            int b = brightness;

            if(b < 0) b = 0;
            else if(b > 100) b = 100;

            b = b * 255 / 100;

            int autobrightnessState;
            try {
                autobrightnessState = Settings.System.getInt(pack.context.getContentResolver(), SCREEN_BRIGHTNESS_MODE);
            } catch (Exception e) {
                autobrightnessState = SCREEN_BRIGHTNESS_MODE_MANUAL;
            }

            if(autobrightnessState == SCREEN_BRIGHTNESS_MODE_AUTOMATIC) Settings.System.putInt(pack.context.getContentResolver(), SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_MANUAL);

            ContentResolver cResolver = pack.context.getApplicationContext().getContentResolver();
            Settings.System.putInt(cResolver, SCREEN_BRIGHTNESS, b);

            refreshBrightness(((Activity) pack.context).getWindow(), b);

        });

        return null;
    }

    private void refreshBrightness(Window window, float brightness) {
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness < 0) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = brightness;
        }
        window.setAttributes(lp);
    }

    @Override
    public int[] argType() {
        return new int[] {CommandAbstraction.INT};
    }

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public int helpRes() {
        return R.string.help_brightness;
    }

    @Override
    public String onArgNotFound(ExecutePack pack, int indexNotFound) {
        return pack.context.getString(R.string.invalid_integer);
    }

    @Override
    public String onNotArgEnough(ExecutePack pack, int nArgs) {
        return pack.context.getString(helpRes());
    }
}
