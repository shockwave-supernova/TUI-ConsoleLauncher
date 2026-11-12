package com.tui.next.tuils.libsuperuser;

import android.os.Handler;
import android.os.Looper;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.tui.next.tuils.Tuils;

import static com.tui.next.tuils.libsuperuser.StreamGobbler.*;

public class Shell {

    @Deprecated
    public static List<String> run(String shell, String[] commands, boolean wantSTDERR) {
        return run(shell, commands, null, wantSTDERR);
    }

    public static List<String> run(String shell, String[] commands, String[] environment,
                                   boolean wantSTDERR) {
        String shellUpper = shell.toUpperCase(Locale.ENGLISH);

        List<String> res = Collections.synchronizedList(new ArrayList<String>());

        try {

            if (environment != null) {
                Map<String, String> newEnvironment = new HashMap<>();
                newEnvironment.putAll(System.getenv());
                int split;
                for (String entry : environment) {
                    if ((split = entry.indexOf("=")) >= 0) {
                        newEnvironment.put(entry.substring(0, split), entry.substring(split + 1));
                    }
                }
                int i = 0;
                environment = new String[newEnvironment.size()];
                for (Map.Entry<String, String> entry : newEnvironment.entrySet()) {
                    environment[i] = entry.getKey() + "=" + entry.getValue();
                    i++;
                }
            }

            Process process = Runtime.getRuntime().exec(shell, environment);
            DataOutputStream STDIN = new DataOutputStream(process.getOutputStream());
            StreamGobbler STDOUT = new StreamGobbler(shellUpper + "-", process.getInputStream(),
                    res);
            StreamGobbler STDERR = new StreamGobbler(shellUpper + "*", process.getErrorStream(),
                    wantSTDERR ? res : null);

            STDOUT.start();
            STDERR.start();
            try {
                for (String write : commands) {
                    STDIN.write((write + "\n").getBytes("UTF-8"));
                    STDIN.flush();
                }
                STDIN.write("exit\n".getBytes("UTF-8"));
                STDIN.flush();
            } catch (IOException e) {
                if (e.getMessage().contains("EPIPE") || e.getMessage().contains("Stream closed")) {

                } else {

                    throw e;
                }
            }

            process.waitFor();

            try {
                STDIN.close();
            } catch (IOException e) {

            }
            STDOUT.join();
            STDERR.join();
            process.destroy();

            if (SU.isSU(shell) && (process.exitValue() == 255)) {
                res = null;
            }
        } catch (IOException e) {

            Tuils.log(e);
            res = null;
        } catch (InterruptedException e) {

            Tuils.log(e);
            res = null;
        }

        return res;
    }

    protected static String[] availableTestCommands = new String[]{
            "echo -BOC-",
            "id"
    };

    protected static boolean parseAvailableResult(List<String> ret, boolean checkForRoot) {
        if (ret == null)
            return false;

        boolean echo_seen = false;

        for (String line : ret) {
            if (line.contains("uid=")) {

                return !checkForRoot || line.contains("uid=0");
            } else if (line.contains("-BOC-")) {

                echo_seen = true;
            }
        }

        return echo_seen;
    }

    public static class SH {

        public static List<String> run(String command) {
            return Shell.run("sh", new String[]{
                    command
            }, null, false);
        }

        public static List<String> run(List<String> commands) {
            return Shell.run("sh", commands.toArray(new String[commands.size()]), null, false);
        }

        public static List<String> run(String[] commands) {
            return Shell.run("sh", commands, null, false);
        }
    }

    public static class SU {
        private static Boolean isSELinuxEnforcing = null;
        private static String[] suVersion = new String[]{
                null, null
        };

        public static List<String> run(String command) {
            return Shell.run("su", new String[]{
                    command
            }, null, false);
        }

        public static List<String> run(List<String> commands) {
            return Shell.run("su", commands.toArray(new String[commands.size()]), null, false);
        }

        public static List<String> run(String[] commands) {
            return Shell.run("su", commands, null, false);
        }

        public static boolean available() {

            List<String> ret = run(Shell.availableTestCommands);
            return Shell.parseAvailableResult(ret, true);
        }

        public static synchronized String version(boolean internal) {
            int idx = internal ? 0 : 1;
            if (suVersion[idx] == null) {
                String version = null;

                List<String> ret = Shell.run(
                        internal ? "su -V" : "su -v",
                        new String[] { "exit" },
                        null,
                        false
                );

                if (ret != null) {
                    for (String line : ret) {
                        if (!internal) {
                            if (!line.trim().equals("")) {
                                version = line;
                                break;
                            }
                        } else {
                            try {
                                if (Integer.parseInt(line) > 0) {
                                    version = line;
                                    break;
                                }
                            } catch (NumberFormatException e) {

                            }
                        }
                    }
                }

                suVersion[idx] = version;
            }
            return suVersion[idx];
        }

        public static boolean isSU(String shell) {

            int pos = shell.indexOf(' ');
            if (pos >= 0) {
                shell = shell.substring(0, pos);
            }

            pos = shell.lastIndexOf('/');
            if (pos >= 0) {
                shell = shell.substring(pos + 1);
            }

            return shell.equals("su");
        }

        public static String shell(int uid, String context) {

            String shell = "su";

            if ((context != null) && isSELinuxEnforcing()) {
                String display = version(false);
                String internal = version(true);

                if ((display != null) &&
                        (internal != null) &&
                        (display.endsWith("SUPERSU")) &&
                        (Integer.valueOf(internal) >= 190)) {
                    shell = String.format(Locale.ENGLISH, "%s --context %s", shell, context);
                }
            }

            if (uid > 0) {
                shell = String.format(Locale.ENGLISH, "%s %d", shell, uid);
            }

            return shell;
        }

        public static String shellMountMaster() {
            if (android.os.Build.VERSION.SDK_INT >= 17) {
                return "su --mount-master";
            }
            return "su";
        }

        public static synchronized boolean isSELinuxEnforcing() {
            if (isSELinuxEnforcing == null) {
                Boolean enforcing = null;

                if (android.os.Build.VERSION.SDK_INT >= 17) {

                    File f = new File("/sys/fs/selinux/enforce");
                    if (f.exists()) {
                        try {
                            InputStream is = new FileInputStream("/sys/fs/selinux/enforce");
                            try {
                                enforcing = (is.read() == '1');
                            } finally {
                                is.close();
                            }
                        } catch (Exception e) {

                        }
                    }

                    if (enforcing == null) {
                        try {
                            Class seLinux = Class.forName("android.os.SELinux");
                            Method isSELinuxEnforced = seLinux.getMethod("isSELinuxEnforced");
                            enforcing = (Boolean) isSELinuxEnforced.invoke(seLinux.newInstance());
                        } catch (Exception e) {

                            enforcing = (android.os.Build.VERSION.SDK_INT >= 19);
                        }
                    }
                }

                if (enforcing == null) {
                    enforcing = false;
                }

                isSELinuxEnforcing = enforcing;
            }
            return isSELinuxEnforcing;
        }

        public static synchronized void clearCachedResults() {
            isSELinuxEnforcing = null;
            suVersion[0] = null;
            suVersion[1] = null;
        }
    }

    private interface OnResult {

        int WATCHDOG_EXIT = -1;
        int SHELL_DIED = -2;

        int SHELL_EXEC_FAILED = -3;
        int SHELL_WRONG_UID = -4;
        int SHELL_RUNNING = 0;
    }

    public interface OnCommandResultListener extends OnResult {

        void onCommandResult(int commandCode, int exitCode, List<String> output);
    }

    public interface OnCommandLineListener extends OnResult, OnLineListener {

        void onCommandResult(int commandCode, int exitCode);
    }

    private static class Command {
        private static int commandCounter = 0;

        private final String[] commands;
        private final int code;
        private final OnCommandResultListener onCommandResultListener;
        private final OnCommandLineListener onCommandLineListener;
        private final String marker;

        public Command(String[] commands, int code,
                       OnCommandResultListener onCommandResultListener,
                       OnCommandLineListener onCommandLineListener) {
            this.commands = commands;
            this.code = code;
            this.onCommandResultListener = onCommandResultListener;
            this.onCommandLineListener = onCommandLineListener;
            this.marker = UUID.randomUUID().toString() + String.format("-%08x", ++commandCounter);
        }
    }

    public static class Builder {
        private Handler handler = null;
        private boolean autoHandler = true;
        private String shell = "sh";
        private boolean wantSTDERR = false;
        private List<Command> commands = new LinkedList<>();
        private Map<String, String> environment = new HashMap<>();
        private OnLineListener onSTDOUTLineListener = null;
        private OnLineListener onSTDERRLineListener = null;
        private int watchdogTimeout = 0;

        public Builder setHandler(Handler handler) {
            this.handler = handler;
            return this;
        }

        public Builder setAutoHandler(boolean autoHandler) {
            this.autoHandler = autoHandler;
            return this;
        }

        public Builder setShell(String shell) {
            this.shell = shell;
            return this;
        }

        public Builder useSH() {
            return setShell("sh");
        }

        public Builder useSU() {
            return setShell("su");
        }

        public Builder setWantSTDERR(boolean wantSTDERR) {
            this.wantSTDERR = wantSTDERR;
            return this;
        }

        public Builder addEnvironment(String key, String value) {
            environment.put(key, value);
            return this;
        }

        public Builder addEnvironment(Map<String, String> addEnvironment) {
            environment.putAll(addEnvironment);
            return this;
        }

        public Builder addCommand(String command) {
            return addCommand(command, 0, null);
        }

        public Builder addCommand(String command, int code,
                                  OnCommandResultListener onCommandResultListener) {
            return addCommand(new String[]{
                    command
            }, code, onCommandResultListener);
        }

        public Builder addCommand(List<String> commands) {
            return addCommand(commands, 0, null);
        }

        public Builder addCommand(List<String> commands, int code,
                                  OnCommandResultListener onCommandResultListener) {
            return addCommand(commands.toArray(new String[commands.size()]), code,
                    onCommandResultListener);
        }

        public Builder addCommand(String[] commands) {
            return addCommand(commands, 0, null);
        }

        public Builder addCommand(String[] commands, int code,
                                  OnCommandResultListener onCommandResultListener) {
            this.commands.add(new Command(commands, code, onCommandResultListener, null));
            return this;
        }

        public Builder setOnSTDOUTLineListener(OnLineListener onLineListener) {
            this.onSTDOUTLineListener = onLineListener;
            return this;
        }

        public Builder setOnSTDERRLineListener(OnLineListener onLineListener) {
            this.onSTDERRLineListener = onLineListener;
            return this;
        }

        public Builder setWatchdogTimeout(int watchdogTimeout) {
            this.watchdogTimeout = watchdogTimeout;
            return this;
        }

        public Interactive open() {
            return new Interactive(this, null);
        }

        public Interactive open(OnCommandResultListener onCommandResultListener) {
            return new Interactive(this, onCommandResultListener);
        }
    }

    public static class Interactive {
        private final Handler handler;
        private final boolean autoHandler;
        private final String shell;
        private final boolean wantSTDERR;
        private final List<Command> commands;
        private final Map<String, String> environment;
        private final OnLineListener onSTDOUTLineListener;
        private final OnLineListener onSTDERRLineListener;
        private int watchdogTimeout;

        private Process process = null;
        private DataOutputStream STDIN = null;
        private StreamGobbler STDOUT = null;
        private StreamGobbler STDERR = null;
        private ScheduledThreadPoolExecutor watchdog = null;

        private volatile boolean running = false;
        private volatile boolean idle = true;
        private volatile boolean closed = true;
        private volatile int callbacks = 0;
        private volatile int watchdogCount;

        private final Object idleSync = new Object();
        private final Object callbackSync = new Object();

        private volatile int lastExitCode = 0;
        private volatile String lastMarkerSTDOUT = null;
        private volatile String lastMarkerSTDERR = null;
        private volatile Command command = null;
        private volatile List<String> buffer = null;

        private Interactive(final Builder builder,
                            final OnCommandResultListener onCommandResultListener) {
            autoHandler = builder.autoHandler;
            shell = builder.shell;
            wantSTDERR = builder.wantSTDERR;
            commands = builder.commands;
            environment = builder.environment;
            onSTDOUTLineListener = builder.onSTDOUTLineListener;
            onSTDERRLineListener = builder.onSTDERRLineListener;
            watchdogTimeout = builder.watchdogTimeout;

            if ((Looper.myLooper() != null) && (builder.handler == null) && autoHandler) {
                handler = new Handler();
            } else {
                handler = builder.handler;
            }

            if (onCommandResultListener != null) {

                watchdogTimeout = 60;
                commands.add(0, new Command(Shell.availableTestCommands, 0, (commandCode, exitCode, output) -> {
                    if ((exitCode == OnCommandResultListener.SHELL_RUNNING) &&
                            !Shell.parseAvailableResult(output, SU.isSU(shell))) {

                        exitCode = OnCommandResultListener.SHELL_WRONG_UID;
                    }
                    watchdogTimeout = builder.watchdogTimeout;
                    onCommandResultListener.onCommandResult(0, exitCode, output);
                }, null));
            }

            if (!open() && (onCommandResultListener != null)) {
                onCommandResultListener.onCommandResult(0,
                        OnCommandResultListener.SHELL_EXEC_FAILED, null);
            }
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }

        public void addCommand(String command) {
            addCommand(command, 0, (OnCommandResultListener) null);
        }

        public void addCommand(String command, int code,
                               OnCommandResultListener onCommandResultListener) {
            addCommand(new String[]{
                    command
            }, code, onCommandResultListener);
        }

        public void addCommand(String command, int code, OnCommandLineListener onCommandLineListener) {
            addCommand(new String[]{
                    command
            }, code, onCommandLineListener);
        }

        public void addCommand(List<String> commands) {
            addCommand(commands, 0, (OnCommandResultListener) null);
        }

        public void addCommand(List<String> commands, int code,
                               OnCommandResultListener onCommandResultListener) {
            addCommand(commands.toArray(new String[commands.size()]), code, onCommandResultListener);
        }

        public void addCommand(List<String> commands, int code,
                               OnCommandLineListener onCommandLineListener) {
            addCommand(commands.toArray(new String[commands.size()]), code, onCommandLineListener);
        }

        public void addCommand(String[] commands) {
            addCommand(commands, 0, (OnCommandResultListener) null);
        }

        public synchronized void addCommand(String[] commands, int code,
                                            OnCommandResultListener onCommandResultListener) {
            this.commands.add(new Command(commands, code, onCommandResultListener, null));
            runNextCommand();
        }

        public synchronized void addCommand(String[] commands, int code,
                                            OnCommandLineListener onCommandLineListener) {
            this.commands.add(new Command(commands, code, null, onCommandLineListener));
            runNextCommand();
        }

        private void runNextCommand() {
            runNextCommand(true);
        }

        private synchronized void handleWatchdog() {
            final int exitCode;

            if (watchdog == null)
                return;
            if (watchdogTimeout == 0)
                return;

            if (!isRunning()) {
                exitCode = OnCommandResultListener.SHELL_DIED;
            } else if (watchdogCount++ < watchdogTimeout) {
                return;
            } else {
                exitCode = OnCommandResultListener.WATCHDOG_EXIT;
            }

            postCallback(command, exitCode, buffer);

            command = null;
            buffer = null;
            idle = true;

            watchdog.shutdown();
            watchdog = null;
            kill();
        }

        private void startWatchdog() {
            if (watchdogTimeout == 0) {
                return;
            }
            watchdogCount = 0;
            watchdog = new ScheduledThreadPoolExecutor(1);
            watchdog.scheduleAtFixedRate(this::handleWatchdog, 1, 1, TimeUnit.SECONDS);
        }

        private void stopWatchdog() {
            if (watchdog != null) {
                watchdog.shutdownNow();
                watchdog = null;
            }
        }

        private void runNextCommand(boolean notifyIdle) {

            boolean running = isRunning();
            if (!running)
                idle = true;

            if (running && idle && (commands.size() > 0)) {
                Command command = commands.get(0);
                commands.remove(0);

                buffer = null;
                lastExitCode = 0;
                lastMarkerSTDOUT = null;
                lastMarkerSTDERR = null;

                if (command.commands.length > 0) {
                    try {
                        if (command.onCommandResultListener != null) {

                            buffer = Collections.synchronizedList(new ArrayList<String>());
                        }

                        idle = false;
                        this.command = command;
                        startWatchdog();
                        for (String write : command.commands) {
                            STDIN.write((write + "\n").getBytes("UTF-8"));
                        }
                        STDIN.write(("echo " + command.marker + " $?\n").getBytes("UTF-8"));
                        STDIN.write(("echo " + command.marker + " >&2\n").getBytes("UTF-8"));
                        STDIN.flush();
                    } catch (IOException e) {

                    }
                } else {
                    runNextCommand(false);
                }
            } else if (!running) {

                while (commands.size() > 0) {
                    postCallback(commands.remove(0), OnCommandResultListener.SHELL_DIED, null);
                }
            }

            if (idle && notifyIdle) {
                synchronized (idleSync) {
                    idleSync.notifyAll();
                }
            }
        }

        private synchronized void processMarker() {
            if (command.marker.equals(lastMarkerSTDOUT)
                    && (command.marker.equals(lastMarkerSTDERR))) {
                postCallback(command, lastExitCode, buffer);
                stopWatchdog();
                command = null;
                buffer = null;
                idle = true;
                runNextCommand();
            }
        }

        private synchronized void processLine(String line, OnLineListener listener) {
            if (listener != null) {
                if (handler != null) {
                    final String fLine = line;
                    final OnLineListener fListener = listener;

                    startCallback();
                    handler.post(() -> {
                        try {
                            fListener.onLine(fLine);
                        } finally {
                            endCallback();
                        }
                    });
                } else {
                    listener.onLine(line);
                }
            }
        }

        private synchronized void addBuffer(String line) {
            if (buffer != null) {
                buffer.add(line);
            }
        }

        private void startCallback() {
            synchronized (callbackSync) {
                callbacks++;
            }
        }

        private void postCallback(final Command fCommand, final int fExitCode,
                                  final List<String> fOutput) {
            if (fCommand.onCommandResultListener == null && fCommand.onCommandLineListener == null) {
                return;
            }
            if (handler == null) {
                if (fCommand.onCommandResultListener != null)
                    fCommand.onCommandResultListener.onCommandResult(fCommand.code, fExitCode,
                            fOutput);
                if (fCommand.onCommandLineListener != null)
                    fCommand.onCommandLineListener.onCommandResult(fCommand.code, fExitCode);
                return;
            }
            startCallback();
            handler.post(() -> {
                try {
                    if (fCommand.onCommandResultListener != null)
                        fCommand.onCommandResultListener.onCommandResult(fCommand.code,
                                fExitCode, fOutput);
                    if (fCommand.onCommandLineListener != null)
                        fCommand.onCommandLineListener
                                .onCommandResult(fCommand.code, fExitCode);
                } finally {
                    endCallback();
                }
            });
        }

        private void endCallback() {
            synchronized (callbackSync) {
                callbacks--;
                if (callbacks == 0) {
                    callbackSync.notifyAll();
                }
            }
        }

        private synchronized boolean open() {
            try {

                if (environment.size() == 0) {
                    process = Runtime.getRuntime().exec(shell);
                } else {
                    Map<String, String> newEnvironment = new HashMap<>();
                    newEnvironment.putAll(System.getenv());
                    newEnvironment.putAll(environment);
                    int i = 0;
                    String[] env = new String[newEnvironment.size()];
                    for (Map.Entry<String, String> entry : newEnvironment.entrySet()) {
                        env[i] = entry.getKey() + "=" + entry.getValue();
                        i++;
                    }
                    process = Runtime.getRuntime().exec(shell, env);
                }

                STDIN = new DataOutputStream(process.getOutputStream());
                STDOUT = new StreamGobbler(shell.toUpperCase(Locale.ENGLISH) + "-",
                        process.getInputStream(), line -> {
                            synchronized (Interactive.this) {
                                if (command == null) {
                                    return;
                                }

                                String contentPart = line;
                                String markerPart = null;

                                int markerIndex = line.indexOf(command.marker);
                                if (markerIndex == 0) {
                                    contentPart = null;
                                    markerPart = line;
                                } else if (markerIndex > 0) {
                                    contentPart = line.substring(0, markerIndex);
                                    markerPart = line.substring(markerIndex);
                                }

                                if (contentPart != null) {
                                    addBuffer(contentPart);
                                    processLine(contentPart, onSTDOUTLineListener);
                                    processLine(contentPart, command.onCommandLineListener);
                                }

                                if (markerPart != null) {
                                    try {
                                        lastExitCode = Integer.valueOf(
                                                markerPart.substring(command.marker.length() + 1), 10);
                                    } catch (Exception e) {

                                        e.printStackTrace();
                                    }
                                    lastMarkerSTDOUT = command.marker;
                                    processMarker();
                                }
                            }
                        });
                STDERR = new StreamGobbler(shell.toUpperCase(Locale.ENGLISH) + "*",
                        process.getErrorStream(), line -> {
                            synchronized (Interactive.this) {
                                if (command == null) {
                                    return;
                                }

                                String contentPart = line;

                                int markerIndex = line.indexOf(command.marker);
                                if (markerIndex == 0) {
                                    contentPart = null;
                                } else if (markerIndex > 0) {
                                    contentPart = line.substring(0, markerIndex);
                                }

                                if (contentPart != null) {
                                    if (wantSTDERR)
                                        addBuffer(contentPart);
                                    processLine(contentPart, onSTDERRLineListener);
                                }

                                if (markerIndex >= 0) {
                                    lastMarkerSTDERR = command.marker;
                                    processMarker();
                                }
                            }
                        });

                STDOUT.start();
                STDERR.start();

                running = true;
                closed = false;

                runNextCommand();

                return true;
            } catch (IOException e) {

                return false;
            }
        }

        public void close() {
            boolean _idle = isIdle();

            synchronized (this) {
                if (!running)
                    return;
                running = false;
                closed = true;
            }

            if (!_idle)
                waitForIdle();

            try {
                try {
                    STDIN.write(("exit\n").getBytes("UTF-8"));
                    STDIN.flush();
                } catch (IOException e) {
                    if (e.getMessage().contains("EPIPE") || e.getMessage().contains("Stream closed")) {

                    } else {
                        throw e;
                    }
                }

                process.waitFor();

                try {
                    STDIN.close();
                } catch (IOException e) {

                }
                STDOUT.join();
                STDERR.join();
                stopWatchdog();
                process.destroy();
            } catch (IOException e) {

            } catch (InterruptedException e) {

            }
        }

        public synchronized void kill() {
            running = false;
            closed = true;

            try {
                STDIN.close();
            } catch (IOException e) {

            }
            try {
                process.destroy();
            } catch (Exception e) {

            }

            idle = true;
            synchronized (idleSync) {
                idleSync.notifyAll();
            }
        }

        public boolean isRunning() {
            if (process == null) {
                return false;
            }
            try {
                process.exitValue();
                return false;
            } catch (IllegalThreadStateException e) {

            }
            return true;
        }

        public synchronized boolean isIdle() {
            if (!isRunning()) {
                idle = true;
                synchronized (idleSync) {
                    idleSync.notifyAll();
                }
            }
            return idle;
        }

        public boolean waitForIdle() {
            if (isRunning()) {
                synchronized (idleSync) {
                    while (!idle) {
                        try {
                            idleSync.wait();
                        } catch (InterruptedException e) {
                            return false;
                        }
                    }
                }

                if ((handler != null) &&
                        (handler.getLooper() != null) &&
                        (handler.getLooper() != Looper.myLooper())) {

                    synchronized (callbackSync) {
                        while (callbacks > 0) {
                            try {
                                callbackSync.wait();
                            } catch (InterruptedException e) {
                                return false;
                            }
                        }
                    }
                }
            }

            return true;
        }

        public boolean hasHandler() {
            return (handler != null);
        }
    }
}
