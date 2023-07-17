package com.mygdx.game.screens.game_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.SurviScreen;
import com.mygdx.game.screens.GameScreen;


public class InQueueScreen extends GameScreen {
    private Label label;
    public InQueueScreen(MyGdxGame game) {
        super(game);
        this.label = new Label("Waiting for the current game to end", game.getSkin());
        label.setPosition(Gdx.graphics.getWidth() / 2 - label.getWidth() / 2, Gdx.graphics.getHeight() / 2 - label.getHeight() / 2);
        stage.addActor(label);
    }
}
