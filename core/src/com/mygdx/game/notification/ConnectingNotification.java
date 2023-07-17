package com.mygdx.game.notification;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;
import java.util.Arrays;

public class ConnectingNotification extends Notification {
    public ConnectingNotification(String address, int port, Skin skin) {
        super("Connecting to " + address + port,
                Arrays.asList(new String[]{"Cancel"}),
                Arrays.asList(new Runnable[]{null}),
                skin
        );
    }
}
