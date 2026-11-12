package com.tui.next.managers.flashlight;

public interface OutputDeviceListener extends DeviceListener {
    void onStatusChanged(boolean status);
}
