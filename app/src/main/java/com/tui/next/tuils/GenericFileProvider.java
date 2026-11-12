package com.tui.next.tuils;

import androidx.core.content.FileProvider;

import com.tui.next.BuildConfig;

public class GenericFileProvider extends FileProvider {
    public static final String PROVIDER_NAME = BuildConfig.APPLICATION_ID + ".FILE_PROVIDER";
}
