package com.tui.next.commands.main.raw;

import com.tui.next.R;
import com.tui.next.commands.CommandAbstraction;
import com.tui.next.commands.ExecutePack;
import com.tui.next.commands.main.MainPack;
import com.tui.next.commands.main.specific.ParamCommand;
import com.tui.next.managers.music.MusicManager2;
import com.tui.next.managers.music.Song;
import com.tui.next.tuils.Tuils;
import com.tui.next.tuils.libsuperuser.Shell;

public class music extends ParamCommand {

    private enum Param implements com.tui.next.commands.main.Param {
        next {

            @Override
            public String exec(ExecutePack pack) {
                if (checkAndRequestAudioPermission((MainPack) pack)) return null;
                if(((MainPack) pack).player == null) {
                    dispatchMediaKey(pack.context, android.view.KeyEvent.KEYCODE_MEDIA_NEXT);
                    return null;
                }

                String title = ((MainPack) pack).player.playNext();
                if(title != null) return pack.context.getString(R.string.output_playing) + Tuils.SPACE + title;
                dispatchMediaKey(pack.context, android.view.KeyEvent.KEYCODE_MEDIA_NEXT);
                return null;
            }
        },
        previous {

            @Override
            public String exec(ExecutePack pack) {
                if (checkAndRequestAudioPermission((MainPack) pack)) return null;
                if(((MainPack) pack).player == null) {
                    dispatchMediaKey(pack.context, android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                    return null;
                }

                String title = ((MainPack) pack).player.playPrev();
                if(title != null) return pack.context.getString(R.string.output_playing) + Tuils.SPACE + title;
                dispatchMediaKey(pack.context, android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                return null;
            }
        },
        ls {

            @Override
            public String exec(ExecutePack pack) {
                if (checkAndRequestAudioPermission((MainPack) pack)) return null;
                if(((MainPack) pack).player == null) return pack.context.getString(R.string.output_musicdisabled);

                return ((MainPack) pack).player.lsSongs();
            }
        },
        play {

            @Override
            public String exec(ExecutePack pack) {
                if (checkAndRequestAudioPermission((MainPack) pack)) return null;
                if(((MainPack) pack).player == null) {
                    dispatchMediaKey(pack.context, android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                    return null;
                }

                String title = ((MainPack) pack).player.play();
                if(title == null) {
                    if (((MainPack) pack).player.getSongs().isEmpty()) {
                        dispatchMediaKey(pack.context, android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                    }
                    return null;
                }
                return pack.context.getString(R.string.output_playing) + Tuils.SPACE + title;
            }
        },
        stop {

            @Override
            public String exec(ExecutePack pack) {
                if(((MainPack) pack).player == null) {
                    dispatchMediaKey(pack.context, android.view.KeyEvent.KEYCODE_MEDIA_STOP);
                    return null;
                }

                ((MainPack) pack).player.stop();
                dispatchMediaKey(pack.context, android.view.KeyEvent.KEYCODE_MEDIA_STOP);
                return null;
            }
        },
        select {
            @Override
            public int[] args() {
                return new int[] {CommandAbstraction.SONG};
            }

            @Override
            public String exec(ExecutePack pack) {
                if(((MainPack) pack).player == null) return pack.context.getString(R.string.output_musicdisabled);

                String s = pack.getString();
                ((MainPack) pack).player.select(s);
                return null;
            }

            @Override
            public String onArgNotFound(ExecutePack pack, int indexNotFound) {
                return pack.context.getString(R.string.output_songnotfound);
            }
        },
        info {

            @Override
            public String exec(ExecutePack pack) {
                if(((MainPack) pack).player == null) return pack.context.getString(R.string.output_musicdisabled);

                StringBuilder builder = new StringBuilder();

                MusicManager2 m = ((MainPack) pack).player;
                Song song = m.get(m.getSongIndex());
                if(song == null) return pack.context.getString(R.string.output_songnotfound);

                builder.append("Name: ").append(song.getTitle()).append(Tuils.NEWLINE);
                if(song.getID() == -1) builder.append("Path: ").append(song.getPath()).append(Tuils.NEWLINE);
                builder.append(Tuils.NEWLINE);

                int curS = m.getCurrentPosition() / 1000;
                int curMin = 0;
                if(curS >= 60) {
                    curMin = curS / 60;
                    curS = curS % 60;
                }

                int s = m.getDuration() / 1000;
                int min = 0;
                if(s >= 60) {
                    min = s / 60;
                    s = s % 60;
                }

                builder.append(curMin > 0 ? curMin + "." + curS : curS + "s").append(" of ").append(min > 0 ? min + "." + s : s + "s").append(" (").append(Tuils.percentage(m.getCurrentPosition(), m.getDuration())).append("%)");
                return builder.toString();
            }
        },
        seekto {
            @Override
            public int[] args() {
                return new int[] {CommandAbstraction.INT};
            }

            @Override
            public String exec(ExecutePack pack) {
                if(((MainPack) pack).player == null) return pack.context.getString(R.string.output_musicdisabled);

                ((MainPack) pack).player.seekTo(pack.getInt() * 1000);
                return null;
            }

            @Override
            public String onArgNotFound(ExecutePack pack, int indexNotFound) {
                return pack.context.getString(R.string.invalid_integer);
            }
        };

        static Param get(String p) {
            p = p.toLowerCase();
            Param[] ps = values();
            for (Param p1 : ps)
                if (p.endsWith(p1.label()))
                    return p1;
            return null;
        }

        static String[] labels() {
            Param[] ps = values();
            String[] ss = new String[ps.length];

            for (int count = 0; count < ps.length; count++) {
                ss[count] = ps[count].label();
            }

            return ss;
        }

        @Override
        public String label() {
            return Tuils.MINUS + name();
        }

        @Override
        public String onArgNotFound(ExecutePack pack, int indexNotFound) {
            return null;
        }

        @Override
        public String onNotArgEnough(ExecutePack pack, int n) {
            return pack.context.getString(R.string.help_music);
        }

        @Override
        public int[] args() {
            return new int[0];
        }
    }

    private static boolean checkAndRequestAudioPermission(MainPack info) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(info.context, android.Manifest.permission.READ_MEDIA_AUDIO) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions((android.app.Activity) info.context, new String[]{android.Manifest.permission.READ_MEDIA_AUDIO}, com.tui.next.LauncherActivity.COMMAND_REQUEST_PERMISSION);
                return true;
            }
        }
        return false;
    }

    private static void dispatchMediaKey(android.content.Context context, int keyCode) {
        try {
            android.media.AudioManager am = (android.media.AudioManager) context.getSystemService(android.content.Context.AUDIO_SERVICE);
            if (am != null) {
                long eventTime = android.os.SystemClock.uptimeMillis();
                am.dispatchMediaKeyEvent(new android.view.KeyEvent(eventTime, eventTime, android.view.KeyEvent.ACTION_DOWN, keyCode, 0));
                am.dispatchMediaKeyEvent(new android.view.KeyEvent(eventTime, eventTime, android.view.KeyEvent.ACTION_UP, keyCode, 0));
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    protected com.tui.next.commands.main.Param paramForString(MainPack pack, String param) {
        return Param.get(param);
    }

    @Override
    public int priority() {
        return 4;
    }

    @Override
    public int helpRes() {
        return R.string.help_music;
    }

    @Override
    public String[] params() {
        return Param.labels();
    }

    @Override
    protected String doThings(ExecutePack pack) {
        return null;
    }
}
