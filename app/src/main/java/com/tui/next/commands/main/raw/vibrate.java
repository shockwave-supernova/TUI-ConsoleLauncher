package com.tui.next.commands.main.raw;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

import com.tui.next.R;
import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;
import com.tui.next.commands.main.MainPack;
import com.tui.next.tuils.Tuils;

public class vibrate implements CommandAbstraction {

    public static void performVibration(Context context, long ms) {
        if (ms <= 0) return;
        Vibrator vibrator = getVibrator(context);
        if (vibrator == null || !vibrator.hasVibrator()) return;

        AudioAttributes attrs = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE), attrs);
        } else {
            vibrator.vibrate(ms, attrs);
        }
    }

    public static void performVibrationPattern(Context context, long[] pattern) {
        if (pattern == null || pattern.length == 0) return;
        Vibrator vibrator = getVibrator(context);
        if (vibrator == null || !vibrator.hasVibrator()) return;

        AudioAttributes attrs = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1), attrs);
        } else {
            vibrator.vibrate(pattern, -1, attrs);
        }
    }

    private static Vibrator getVibrator(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vm = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            return vm != null ? vm.getDefaultVibrator() : null;
        }
        return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public String exec(ExecutePack pack) throws Exception {
        String text = pack.getString();
        Context context = ((MainPack) pack).context;

        char separator = Tuils.firstNonDigit(text);

        if(separator == 0) {
            int ms;
            try {
                ms = Integer.parseInt(text);
                performVibration(context, ms);
                return null;
            } catch (NumberFormatException e) {
                return context.getString(R.string.invalid_integer);
            } catch (Exception e) {
                return e.toString();
            }
        } else {
            if(separator == ' ') {
                char s2 = Tuils.firstNonDigit(Tuils.removeSpaces(text));
                if(s2 != 0) {
                    text = Tuils.removeSpaces(text);
                    separator = s2;
                }
            }

            String[] split = text.split(separator + Tuils.EMPTYSTRING);
            long pattern[] = new long[split.length];

            for(int c = 0; c < split.length; c++) {
                try {
                    pattern[c] = Long.parseLong(split[c]);
                } catch (Exception e) {
                    pattern[c] = 0;
                }
            }

            performVibrationPattern(context, pattern);
        }

        return null;
    }

    @Override
    public int[] argType() {
        return new int[] {CommandAbstraction.PLAIN_TEXT};
    }

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public int helpRes() {
        return R.string.help_vibrate;
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
