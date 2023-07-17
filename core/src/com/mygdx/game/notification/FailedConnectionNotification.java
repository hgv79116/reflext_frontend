package com.mygdx.game.notification;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;
import java.util.Arrays;

public class FailedConnectionNotification extends Notification{
    public FailedConnectionNotification(String errorMessage, Skin skin) {
        super("Connection failed. Error: " + errorMessage,
                Arrays.asList(new String[] {"Okay"}),
                Arrays.asList(new Runnable[] {null}),
                skin);
    }
}
