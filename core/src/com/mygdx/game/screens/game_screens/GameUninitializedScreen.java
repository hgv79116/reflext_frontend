package com.mygdx.game.screens.game_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.screens.GameScreen;

public class GameUninitializedScreen extends GameScreen {
    private final String MESSAGE = "Waiting for a new game";
    private Label label;
    public GameUninitializedScreen(MyGdxGame game) {
        super(game);
        final float screenWidth = Gdx.graphics.getWidth();
        final float screenHeight = Gdx.graphics.getHeight();
        this.label = new Label(MESSAGE, game.getSkin());

        label.setPosition(screenWidth / 2 - label.getWidth() / 2, screenHeight / 2 - label.getHeight() / 2);
        stage.addActor(label);
    }
}
