package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.SurviScreen;
import com.mygdx.game.notification.ExitGameNotification;

public class GameScreen extends SurviScreen {
    protected final TextButton exitButton;
    public GameScreen(final MyGdxGame game) {
        super(game);

        final float screenWidth = Gdx.graphics.getWidth();
        final float screenHeight = Gdx.graphics.getHeight();

        this.exitButton = new TextButton("Exit", game.getSkin());


        exitButton.setPosition(screenWidth - exitButton.getWidth() - 10, screenHeight - exitButton.getHeight() - 10);
        exitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                putNotification(new ExitGameNotification(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    GameScreen.this.game.exitGame();
                                } catch (Exception e) {
                                    System.out.println(e);
                                }
                            }
                        },
                        game.getSkin()
                ));
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        stage.addActor(exitButton);
    }
}
