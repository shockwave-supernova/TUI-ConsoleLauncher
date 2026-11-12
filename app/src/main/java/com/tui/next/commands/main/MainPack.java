package com.tui.next.commands.main;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import java.io.File;
import java.lang.reflect.Method;

import com.tui.next.commands.CommandGroup;
import com.tui.next.commands.CommandsPreferences;
import com.tui.next.commands.ExecutePack;
import com.tui.next.managers.AliasManager;
import com.tui.next.managers.AppsManager;
import com.tui.next.managers.ContactManager;
import com.tui.next.managers.RssManager;
import com.tui.next.managers.TerminalManager;
import com.tui.next.managers.flashlight.TorchManager;
import com.tui.next.managers.music.MusicManager2;
import com.tui.next.managers.xml.XMLPrefsManager;
import com.tui.next.managers.xml.options.Behavior;
import com.tui.next.tuils.interfaces.Redirectator;
import com.tui.next.tuils.libsuperuser.ShellHolder;
import okhttp3.OkHttpClient;

public class MainPack extends ExecutePack {

    public File currentDirectory;

    public Resources res;

    public WifiManager wifi;

    public Method setMobileDataEnabledMethod;
    public ConnectivityManager connectivityMgr;
    public Object connectMgr;

    public ContactManager contacts;

    public MusicManager2 player;

    public AliasManager aliasManager;
    public AppsManager appsManager;

    public CommandsPreferences cmdPrefs;

    public String lastCommand;

    public Redirectator redirectator;

    public ShellHolder shellHolder;

    public RssManager rssManager;

    public OkHttpClient client;

    public int commandColor = TerminalManager.NO_COLOR;

    public MainPack(Context context, CommandGroup commandGroup, AliasManager alMgr, AppsManager appmgr, MusicManager2 p,
                    ContactManager c, Redirectator redirectator, RssManager rssManager, OkHttpClient client) {
        super(commandGroup);

        this.currentDirectory = XMLPrefsManager.get(File.class, Behavior.home_path);

        this.rssManager = rssManager;

        this.client = client;

        this.res = context.getResources();

        this.context = context;

        this.aliasManager = alMgr;
        this.appsManager = appmgr;

        this.cmdPrefs = new CommandsPreferences();

        this.player = p;
        this.contacts = c;

        this.redirectator = redirectator;
    }

    public void dispose() {
        TorchManager mgr = TorchManager.getInstance();
        if(mgr.isOn()) mgr.turnOff();
    }

    public void destroy() {
        if(player != null) player.destroy();
        appsManager.onDestroy();
        if(rssManager != null) rssManager.dispose();
        contacts.destroy(context);
    }

    @Override
    public void clear() {
        super.clear();

        commandColor = TerminalManager.NO_COLOR;
    }
}
