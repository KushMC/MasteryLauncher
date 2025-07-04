package com.redemastery.oldapi.pojav.customcontrols.mouse;

import android.os.Handler;

import org.lwjgl.glfw.CallbackBridge;

import com.redemastery.oldapi.pojav.LwjglGlfwKeycode;

public class RightClickGesture extends ValidatorGesture {
    private boolean mGestureEnabled = true;
    private boolean mGestureValid = true;
    private float mGestureStartX, mGestureStartY, mGestureEndX, mGestureEndY;
    public RightClickGesture(Handler mHandler) {
        super(mHandler);
    }

    public final void inputEvent() {
        if(!mGestureEnabled) return;
        if(submit()) {
            mGestureStartX = mGestureEndX = CallbackBridge.mouseX;
            mGestureStartY = mGestureEndY = CallbackBridge.mouseY;
            mGestureEnabled = false;
            mGestureValid = true;
        }
    }

    public void setMotion(float deltaX, float deltaY) {
        System.out.println("set motion called");
        mGestureEndX += deltaX;
        mGestureEndY += deltaY;
    }

    @Override
    protected int getGestureDelay() {
        return 150;
    }

    @Override
    public boolean checkAndTrigger() {
        // If the validate() method was called, it means that the user held on for too long. The cancellation should be ignored.
        mGestureValid = false;
        // Never call onGestureCancelled. This way we will be able to reserve that only for when
        // the gesture is stopped in the code (when the user lets go of the screen or the tap was
        // cancelled by turning on the grab)
        return true;
    }

    @Override
    public void onGestureCancelled(boolean isSwitching) {
        mGestureEnabled = true;
        if(!mGestureValid || isSwitching) return;
        boolean fingerStill = LeftClickGesture.isFingerStill(mGestureStartX, mGestureStartY, mGestureEndX, mGestureEndY, LeftClickGesture.FINGER_STILL_THRESHOLD);
        System.out.println("Right click: " + fingerStill);
        if(!fingerStill) return;
        CallbackBridge.sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_RIGHT, true);
        CallbackBridge.sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_RIGHT, false);
    }
}
