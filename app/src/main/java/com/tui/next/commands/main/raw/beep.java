package com.tui.next.commands.main.raw;

import android.media.AudioManager;
import android.media.ToneGenerator;

import com.tui.next.R;
import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;
import com.tui.next.commands.main.MainPack;

public class beep implements CommandAbstraction {

    @Override
    public String exec(ExecutePack pack) throws Exception {
        try {
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);
        } catch (Exception e) {
            return e.toString();
        }

        return null;
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
    public int helpRes() {
        return R.string.help_beep;
    }

    @Override
    public String onArgNotFound(ExecutePack pack, int index) {
        return null;
    }

    @Override
    public String onNotArgEnough(ExecutePack pack, int nArgs) {
        return ((MainPack) pack).context.getString(helpRes());
    }
}
