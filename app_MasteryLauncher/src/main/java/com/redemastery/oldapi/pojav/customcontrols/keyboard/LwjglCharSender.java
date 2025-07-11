package com.redemastery.oldapi.pojav.customcontrols.keyboard;

import static org.lwjgl.glfw.CallbackBridge.sendKeyPress;

import org.lwjgl.glfw.CallbackBridge;

import com.redemastery.oldapi.pojav.LwjglGlfwKeycode;

/** Sends keys via the CallBackBridge */
public class LwjglCharSender implements CharacterSenderStrategy {
    @Override
    public void sendBackspace() {
        CallbackBridge.sendKeycode(LwjglGlfwKeycode.GLFW_KEY_BACKSPACE, '\u0008', 0, 0, true);
        CallbackBridge.sendKeycode(LwjglGlfwKeycode.GLFW_KEY_BACKSPACE, '\u0008', 0, 0, false);
    }

    @Override
    public void sendEnter() {
        sendKeyPress(LwjglGlfwKeycode.GLFW_KEY_ENTER);
    }

    @Override
    public void sendChar(char character) {
        CallbackBridge.sendChar(character, 0);
    }
}
