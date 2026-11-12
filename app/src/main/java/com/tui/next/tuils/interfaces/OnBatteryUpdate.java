package com.tui.next.tuils.interfaces;

public interface OnBatteryUpdate {

    void update(float percentage);
    void onCharging();
    void onNotCharging();
}
