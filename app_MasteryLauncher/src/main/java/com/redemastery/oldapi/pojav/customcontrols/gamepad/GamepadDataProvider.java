package com.redemastery.oldapi.pojav.customcontrols.gamepad;

import com.redemastery.oldapi.pojav.GrabListener;

public interface GamepadDataProvider {
    GamepadMap getMenuMap();
    GamepadMap getGameMap();
    boolean isGrabbing();
    void attachGrabListener(GrabListener grabListener);
    void detachGrabListener(GrabListener grabListener);
}
