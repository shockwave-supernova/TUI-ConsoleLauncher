package com.tui.next.tuils.libsuperuser;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import com.tui.next.managers.TerminalManager;
import com.tui.next.managers.xml.XMLPrefsManager;
import com.tui.next.managers.xml.options.Behavior;
import com.tui.next.tuils.Tuils;

public class ShellHolder {

    private Context context;

    public ShellHolder(Context context) {
        this.context = context;
    }

    Pattern p = Pattern.compile("^\\n");

    public Shell.Interactive build() {
        Shell.Interactive interactive = new Shell.Builder()
                .setOnSTDOUTLineListener(line -> {
                    line = p.matcher(line).replaceAll(Tuils.EMPTYSTRING);
                    Tuils.sendOutput(context, line, TerminalManager.CATEGORY_OUTPUT);
                })
                .setOnSTDERRLineListener(line -> {
                    line = p.matcher(line).replaceAll(Tuils.EMPTYSTRING);
                    Tuils.sendOutput(context, line, TerminalManager.CATEGORY_OUTPUT);
                })
                .open();

        interactive.addCommand("cd " + XMLPrefsManager.get(File.class, Behavior.home_path));
        return interactive;
    }
}
