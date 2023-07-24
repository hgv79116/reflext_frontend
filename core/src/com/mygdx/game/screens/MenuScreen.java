package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea; // somehow it cannot import this automatically?
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.SurviScreen;

import java.awt.*;

public class MenuScreen extends SurviScreen {
    private int width;
    private int height;
    private TextArea usernameInput;
    private TextArea gameIpInput;
    private TextButton joinGameButton;
    private TextButton createGameButton;
    public MenuScreen(MyGdxGame game) {
        super(game);
        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();
        this.usernameInput = new TextArea("John Doe x", game.getSkin());
        this.gameIpInput = new TextArea("127.0.0.1:8080", game.getSkin());
        this.joinGameButton = new TextButton("Join", game.getSkin());
        this.createGameButton = new TextButton("Create a new game", game.getSkin());

        final float formWidth = usernameInput.getWidth();
        final float formX = width / 2 - formWidth / 2;

        usernameInput.setX(formX);
        usernameInput.setY(height / 2);

        final float joinGameY = height / 2 - joinGameButton.getHeight();

        gameIpInput.setPosition(formX,joinGameY);
        gameIpInput.setWidth(gameIpInput.getWidth() * 2 / 3);


        joinGameButton.setPosition(formX + gameIpInput.getWidth(), joinGameY);
        joinGameButton.setWidth(usernameInput.getWidth() - gameIpInput.getWidth());

        final float createGameY = joinGameY  - joinGameButton.getHeight();

        createGameButton.setPosition(formX, createGameY);

        stage.addActor(usernameInput);
        stage.addActor(gameIpInput);
        stage.addActor(joinGameButton);
        stage.addActor(createGameButton);

        joinGameButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String username = usernameInput.getText();
                String[] ipAddress = gameIpInput.getText().split(":");

                MenuScreen.this.game.setUsername(username);
                MenuScreen.this.game.connect(ipAddress[0], Integer.valueOf(ipAddress[1]));
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }
}
