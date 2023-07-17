package com.mygdx.game.notification;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.Arrays;

public class AuthenticatingNotification extends Notification {
    public AuthenticatingNotification(Skin skin) {
        super("Authenticating",
                Arrays.asList(new String[]{}),
                Arrays.asList(new Runnable[]{}),
                skin
        );
    }
}
