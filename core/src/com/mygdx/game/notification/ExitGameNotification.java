package com.mygdx.game.notification;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.Arrays;

public class ExitGameNotification extends Notification {
    public ExitGameNotification(Runnable exitCallback, Skin skin) {
        super("Do you want to exit the game?",
                Arrays.asList(new String[] {"Exit", "Cancel"}),
                Arrays.asList(new Runnable[]{exitCallback, null}),
                skin);
    }
}
