package com.tui.next.commands.main.raw;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.tui.next.R;
import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;
import com.tui.next.commands.main.MainPack;
import com.tui.next.managers.AppsManager;

public class gemini implements CommandAbstraction {

    private static final String[] GEMINI_PACKAGES = {
            "com.google.android.apps.bard",
            "com.google.android.googlequicksearchbox"
    };

    @Override
    public String exec(ExecutePack pack) throws Exception {
        MainPack info = (MainPack) pack;
        PackageManager pm = info.context.getPackageManager();

        Intent launchIntent = pm.getLaunchIntentForPackage("com.google.android.apps.bard");
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            info.context.startActivity(launchIntent);
            return null;
        }

        if (info.appsManager != null) {
            AppsManager.LaunchInfo appInfo = info.appsManager.findLaunchInfoWithLabel("gemini", AppsManager.SHOWN_APPS);
            if (appInfo == null) {
                appInfo = info.appsManager.findLaunchInfoWithLabel("gemini", AppsManager.HIDDEN_APPS);
            }
            if (appInfo != null) {
                Intent appIntent = info.appsManager.getIntent(appInfo);
                if (appIntent != null) {
                    appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    info.context.startActivity(appIntent);
                    return null;
                }
            }
        }

        try {
            Intent assistIntent = new Intent(Intent.ACTION_VOICE_COMMAND);
            assistIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (assistIntent.resolveActivity(pm) != null) {
                info.context.startActivity(assistIntent);
                return null;
            }
        } catch (Exception ignored) {
        }

        try {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gemini.google.com"));
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            info.context.startActivity(webIntent);
            return null;
        } catch (Exception e) {
            return info.res.getString(R.string.output_appnotfound);
        }
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 6;
    }

    @Override
    public int helpRes() {
        return R.string.help_gemini;
    }

    @Override
    public String onArgNotFound(ExecutePack pack, int index) {
        return null;
    }

    @Override
    public String onNotArgEnough(ExecutePack pack, int nArgs) {
        return null;
    }
}
