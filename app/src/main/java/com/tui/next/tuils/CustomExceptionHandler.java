package com.tui.next.tuils;

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler _defaultEH;

    public CustomExceptionHandler(){
        _defaultEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        Tuils.toFile(ex);
        _defaultEH.uncaughtException(thread, ex);
    }

}
