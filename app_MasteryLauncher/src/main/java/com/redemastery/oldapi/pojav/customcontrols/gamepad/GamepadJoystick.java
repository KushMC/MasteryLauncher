package com.redemastery.oldapi.pojav.customcontrols.gamepad;

import android.view.InputDevice;
import android.view.MotionEvent;

import com.redemastery.oldapi.pojav.utils.MathUtils;

public class GamepadJoystick {

    //Directions
    public static final int DIRECTION_NONE = -1; //GamepadJoystick at the center

    public static final int DIRECTION_EAST = 0;
    public static final int DIRECTION_NORTH_EAST = 1;
    public static final int DIRECTION_NORTH = 2;
    public static final int DIRECTION_NORTH_WEST = 3;
    public static final int DIRECTION_WEST = 4;
    public static final int DIRECTION_SOUTH_WEST = 5;
    public static final int DIRECTION_SOUTH = 6;
    public static final int DIRECTION_SOUTH_EAST = 7;

    private final InputDevice mInputDevice;

    private final int mHorizontalAxis;
    private final int mVerticalAxis;
    private float mVerticalAxisValue = 0;
    private float mHorizontalAxisValue = 0;

    public GamepadJoystick(int horizontalAxis, int verticalAxis, InputDevice device){
        mHorizontalAxis = horizontalAxis;
        mVerticalAxis = verticalAxis;
        this.mInputDevice = device;
    }

    public double getAngleRadian(){
        //From -PI to PI
        // TODO misuse of the deadzone here !
        return -Math.atan2(getVerticalAxis(), getHorizontalAxis());
    }


    public double getAngleDegree(){
        //From 0 to 360 degrees
        double result = Math.toDegrees(getAngleRadian());
        if(result < 0) result += 360;

        return result;
    }

    public double getMagnitude(){
        float x = Math.abs(mHorizontalAxisValue);
        float y = Math.abs(mVerticalAxisValue);

        return MathUtils.dist(0,0, x, y);
    }

    public float getVerticalAxis(){
        return mVerticalAxisValue;
    }

    public float getHorizontalAxis(){
        return mHorizontalAxisValue;
    }

    public static boolean isJoystickEvent(MotionEvent event){
        return (event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK
                && event.getAction() == MotionEvent.ACTION_MOVE;
    }


    public int getHeightDirection(){
        if(getMagnitude() == 0) return DIRECTION_NONE;
        return ((int) ((getAngleDegree()+22.5)/45)) % 8;
    }


    /* Setters */
    public void setXAxisValue(float value){
        this.mHorizontalAxisValue = value;
    }

    public void setYAxisValue(float value){
        this.mVerticalAxisValue = value;
    }
}
