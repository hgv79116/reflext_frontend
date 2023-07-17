package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.SurviScreen;

import java.awt.*;
import java.net.InetAddress;

public class ConnectingScreen extends SurviScreen {
    private String ipAddress;
    private int port;
    private final Label label;
    public ConnectingScreen(MyGdxGame game, String ipAddress, int port) {
        super(game);
        this.ipAddress = ipAddress;
        this.port = port;
        this.label = new Label("Connecting to " + ipAddress + ":" + port, game.getSkin());
        this.label.setPosition(Gdx.graphics.getWidth() / 2 - label.getWidth() / 2, Gdx.graphics.getHeight() / 2 -label.getHeight() / 2);
        this.stage.addActor(label);
    }
}
