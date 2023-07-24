package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.notification.Notification;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public abstract class SurviScreen extends ScreenAdapter {
    protected final MyGdxGame game;
    protected Stage stage;

    static Queue<Long> renderTimes;
    private Label frameRate;

    public static float getScreenWidth() {
        return Gdx.graphics.getWidth();
    }

    public static float getScreenHeight() {
        return Gdx.graphics.getHeight();
    }

    public SurviScreen(MyGdxGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.renderTimes = new LinkedList<>();
        this.frameRate = new Label(" 0 fps", game.getSkin());
        this.frameRate.setPosition(0, 0);
        this.stage.addActor(this.frameRate);
    }

    public void putNotification(Notification notification) {
        notification.addToStage(stage);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 0.1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        long currentTime = System.currentTimeMillis();
        renderTimes.add(currentTime);

        while(renderTimes.peek() < currentTime - 1000) {
            renderTimes.remove();
        }
        frameRate.setText(String.valueOf(renderTimes.size()) + " fps");

        stage.act(delta);
        stage.draw();
    }
}
