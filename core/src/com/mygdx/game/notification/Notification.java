package com.mygdx.game.notification;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.*;

public class Notification {
    private final Skin skin;
    private final String message;
    private final Group group = new Group();
    private final Label label;
    private final Background background;

    public class Background extends Actor {
        ShapeRenderer sr;
        public Background() {
            sr = new ShapeRenderer();
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);

            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();

            batch.end();
//            Gdx.gl.glEnable(GL30.GL_BLEND);
//            Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
//            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(Color.GRAY);
            sr.rect(0, 0, screenWidth, screenHeight);
            sr.end();
//            sr.begin(ShapeRenderer.ShapeType.Filled);
//            sr.setColor(Color.BLACK);
//            sr.rect(0.25f * screenWidth, 0.25f * screenHeight, 0.5f * screenWidth, 0.5f * screenHeight);
//            sr.end();
            batch.begin();
        }
    }
    public Notification(String message, List<String> options, List<Runnable> callbacks, Skin skin) {
        this.skin = skin;
        this.message = message;

        final float screenWidth = Gdx.graphics.getWidth();
        final float screenHeight = Gdx.graphics.getHeight();

        this.background = new Background();
        group.addActor(background);

        this.label = new Label(message, skin);
        this.label.setPosition(screenWidth / 2 - label.getWidth() / 2,
                screenHeight / 2 - label.getHeight() / 2);

        group.addActor(label);

        final float BUTTON_WIDTH = 40;
        final float BUTTON_HEIGHT = 20;
        final float BUTTON_GAP = 10;
        float totalButtonWidth = BUTTON_WIDTH * options.size() + BUTTON_GAP * (options.size() - 1);
        float buttonX = screenWidth / 2 - totalButtonWidth / 2;
        float buttonY = this.label.getY() - BUTTON_HEIGHT - 10;

        assert(options.size() == callbacks.size());

        for(int i = 0; i < options.size(); i++) {
            TextButton button = new TextButton(options.get(i), skin);
            final Runnable callback = callbacks.get(i);
            button.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if(callback != null) {
                        callback.run();
                    }
                    remove();
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
            button.setPosition(buttonX,
                    buttonY);
            buttonX += BUTTON_WIDTH + BUTTON_GAP;
            group.addActor(button);
        }
    }

    public void addToStage(Stage stage) {
        stage.addActor(group);
    }

    public void remove() {
        group.remove();
    }
}
