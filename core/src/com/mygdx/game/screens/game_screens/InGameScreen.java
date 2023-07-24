package com.mygdx.game.screens.game_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.screens.GameScreen;
import org.javatuples.Triplet;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class InGameScreen extends GameScreen {
    private final int playerId;
    private long lastUpdateTime = -1;

    class Scoreboard {
        private final String TITLE = "LEADERBOARD";
        private final int playerNum;
        private final float width;
        private final float height;
        private final float x;
        private final float y;
        private final Actor scoreboardBackground;
        private final Label scoreboardTitle;
        private final Label[] players;
        Scoreboard(JSONObject playerInfo, float x, float y, float width, float height) {
            this.playerNum = playerInfo.length();
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;

            scoreboardBackground = new Actor() {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    super.draw(batch, parentAlpha);

                    batch.end();

                    ShapeRenderer sr = new ShapeRenderer();
                    sr.begin(ShapeRenderer.ShapeType.Filled);
                    sr.setColor(Color.GRAY);
                    sr.rect(Scoreboard.this.x, Scoreboard.this.y, Scoreboard.this.width, Scoreboard.this.height);
                    sr.end();

                    batch.begin();
                }
            };

            this.scoreboardTitle = new Label(TITLE, game.getSkin());

            final float titleX = scoreboardBackground.getX()
                    + width / 2
                    - scoreboardTitle.getWidth() / 2;
            final float titleY = scoreboardBackground.getY()
                    + height
                    - scoreboardTitle.getHeight();

            scoreboardTitle.setX(titleX);
            scoreboardTitle.setY(titleY);

//            final float cardHeight = scoreboardTitle.getHeight();
            players = new Label[playerNum];
            for(int i = 0; i < playerNum; i++) {
                JSONObject userInfo = playerInfo.getJSONObject(String.valueOf(i));
                String username = userInfo.getString("username");
                float score = BigDecimal.valueOf(userInfo.getFloat("score")).setScale(2, RoundingMode.HALF_UP).floatValue();
                players[i] = new Label(username + " (" + score + ")", game.getSkin());
                players[i].setX(scoreboardBackground.getX() + 10);
                float prevY = i == 0 ? scoreboardTitle.getY() : players[i - 1].getY();
                players[i].setY(prevY - players[i].getHeight());
            }
        }

        void update(JSONObject playerInfo) {
            Integer[] userIds = new Integer[playerNum];
            float[] score = new float[playerNum];
            for(int i = 0; i < playerNum; i++) {
                JSONObject userInfo = playerInfo.getJSONObject(String.valueOf(i));
                userIds[i] = i;
                score[i] = BigDecimal.valueOf(userInfo.getFloat("score")).setScale(2, RoundingMode.HALF_UP).floatValue();
            }
            Arrays.sort(userIds, (o1, o2) -> score[o1] < score[o2]? 1: -1);
            for(int i = 0; i < playerNum; i++) {
                int id = userIds[i];
                JSONObject userInfo = playerInfo.getJSONObject(String.valueOf(id));
                String username = userInfo.getString("username");
                players[i].setText(username + " (" + score[id] + ")");
                if(id == playerId) {
                    players[i].getColor().a = 1;
                }
                else {
                    players[i].getColor().a = 0.5f;
                }
            }
        }

        void addToStage(Stage stage) {
//            stage.addActor(scoreboardBackground);
            stage.addActor(scoreboardTitle);
            for(int i = 0; i < playerNum; i++) {
                stage.addActor(players[i]);
            }
        }

        void remove() {
            scoreboardBackground.remove();
            scoreboardTitle.remove();
            for(int i = 0; i < playerNum; i++) {
                players[i].remove();
            }
        }

    }

    class Circle {
        private static final float RADIUS = 25;
        private static final float MISCLICK_ACCEPTABLE_RANGE = 1.2f;
        private final int category;
        private final int id;
        private final Label label;
        private final Actor bound;
        private final long lifetime;
        private long timeLeft;
        float centerX, centerY;
        private Button button;
        Circle(int id, JSONObject circleJSON) {
            this.id = id;
            this.category = circleJSON.getInt("category");
            this.label = new Label(String.valueOf(category), game.getSkin());
            this.lifetime = circleJSON.getInt("lifetime");
            this.timeLeft = lifetime;
            this.centerX = circleJSON.getJSONObject("coordinate").getFloat("x") * getScreenWidth();
            this.centerY = circleJSON.getJSONObject("coordinate").getFloat("y") * getScreenHeight();

            this.label.setPosition(centerX - label.getWidth() / 2, centerY - label.getHeight() / 2);
            this.label.setTouchable(Touchable.disabled);
            this.bound = new Actor() {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    super.draw(batch, parentAlpha);

                    batch.end();

                    Gdx.gl.glEnable(GL20.GL_BLEND);
                    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

                    ShapeRenderer sr = new ShapeRenderer();
                    sr.begin(ShapeRenderer.ShapeType.Line);

                    sr.setColor(getColor());
                    System.out.println(getColor().a);
                    sr.circle(Circle.this.centerX, Circle.this.centerY, RADIUS);
                    sr.end();

                    batch.begin();
                }

                @Override
                public Actor hit(float x, float y, boolean touchable) {
                    if(Math.pow(x - Circle.this.centerX, 2) + Math.pow(y - Circle.this.centerY,2) <= Math.pow(RADIUS, 2)
                            * MISCLICK_ACCEPTABLE_RANGE) {
                        return this;
                    }
                    return super.hit(x, y, touchable);
                }
            };
            this.bound.setColor(Color.WHITE);

            this.bound.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    circleHit(Circle.this);
                    return true;
                }
            });

            this.button = new Button(game.getSkin());
            button.setPosition(this.centerX, this.centerY);
        }

        public int getId() {
            return id;
        }

        public float getX() {
            return centerX;
        }

        public float getY() {
            return centerY;
        }

        void addToStage(Stage stage) {
            stage.addActor(bound);
            stage.addActor(label);
//            stage.addActor(button);
        }

        long getTimeLeft() {
            return timeLeft;
        }

        void update(JSONObject circleJson) {
            long timePassed = circleJson.getInt("timePassed");
            System.out.println(lifetime + " " + timePassed + " " + timeLeft);
            timeLeft = lifetime - timePassed;

            if(timeLeft < 0) {
                remove();
            }
            else {
                Triplet rgb = ColorConverter.convert(categoryScore.get(category));
                float a = timeLeft / (float)lifetime;
                label.getColor().r = bound.getColor().r = (float)rgb.getValue0();
                label.getColor().g = bound.getColor().g = (float)rgb.getValue1();
                label.getColor().b = bound.getColor().b = (float)rgb.getValue2();
                label.getColor().a = bound.getColor().a = a; // = 0 ofc..... my 15 minute

                this.centerX = circleJson.getJSONObject("coordinate").getFloat("x") * getScreenWidth();
                this.centerY = circleJson.getJSONObject("coordinate").getFloat("y") * getScreenHeight();
                this.label.setPosition(centerX - label.getWidth() / 2, centerY - label.getHeight() / 2);
            }
        }

        void remove() {
            label.remove();
            bound.remove();
//            button.remove();
        }

        public void markHit() {
            bound.setColor(Color.RED);
            label.setColor(Color.WHITE);
        }
    }

    class Effect {
        static final float DEFAULT_DURATION = 2000;
        float timeLeft = DEFAULT_DURATION;
        private final float value;
        private final Label label;

        public Effect(float x, float y, float value) {
            this.value = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).floatValue();
            this.label = new Label(String.valueOf(value), game.getSkin());
            this.label.setPosition(x, y);

            Triplet rgb = ColorConverter.convert(value);
            this.label.setColor(
                    (float)rgb.getValue0(),
                    (float)rgb.getValue1(),
                    (float)rgb.getValue2(),
                    timeLeft / DEFAULT_DURATION
            );
        }

        public float getTimeLeft() {
            return timeLeft;
        }

        public void update(float delta) {
            timeLeft -= delta * 1000; // delta is in secondds.
            System.out.println(timeLeft + " " + delta);
            if(timeLeft < 0) {
                remove();
            }
            else {
                Triplet rgb = ColorConverter.convert(value);
                this.label.setColor(
                        (float)rgb.getValue0(),
                        (float)rgb.getValue1(),
                        (float)rgb.getValue2(),
                        timeLeft / DEFAULT_DURATION
                );
            }
        }

        public void addToStage(Stage stage) {
            stage.addActor(label);
        }
        private void remove() {
            label.remove();
        }
    }

//    private final Actor categoriesBackground;
//    private final CategoryCard[] categoryCards;
    private final HashMap<Integer, Float> categoryScore = new HashMap<>();
    private final Scoreboard scoreboard;
    private final HashMap<Integer, Circle> circles = new HashMap<>();
    private final HashSet<Effect> effects = new HashSet<>();
    private int hitCount = 0;
    private Label hitCountLabel;

    public InGameScreen(MyGdxGame game, JSONObject initialGameState, int playerId) {
        super(game);

        this.playerId = playerId;
        JSONObject players = initialGameState.getJSONObject("players");
        JSONObject categories = players.getJSONObject(String.valueOf(playerId)).getJSONObject("categories");

        final float screenWidth = Gdx.graphics.getWidth();
        final float screenHeight = Gdx.graphics.getHeight();

        this.scoreboard = new Scoreboard(players, 0, 0, 140, 300);
        this.scoreboard.addToStage(stage);

        this.hitCountLabel = new Label("0", game.getSkin());
        hitCountLabel.setPosition(30, 0);
        stage.addActor(hitCountLabel);
    }

    @Override
    public void render(float delta) {
        if(lastUpdateTime < game.getLastGameStateUpdateTime()) {
            lastUpdateTime = game.getLastGameStateUpdateTime();
            updateGameState(game.getLastGameState());
        }
        updateLocal(delta);
        super.render(delta);
    }

    private void updateGameState(JSONObject gameState) {
        JSONObject players = gameState.getJSONObject("players");
        JSONObject circleJson = gameState.getJSONObject("circles");
        JSONObject categories = players.getJSONObject(String.valueOf(playerId)).getJSONObject("categories");
        scoreboard.update(players);

        ArrayList<Integer> removedCircles = new ArrayList<>();
        for(Integer id: circles.keySet()) {
            if (circleJson.has(String.valueOf(id))) {
                circles.get(id).update(circleJson.getJSONObject(String.valueOf(id)));
            } else {
                removedCircles.add(id);
            }
        }

        for(Integer id: removedCircles) {
            circles.get(id).remove();
            circles.remove(id);
        }

        for(String idString: circleJson.keySet()) {
            int id = Integer.valueOf(idString);
            if (!circles.containsKey(id)) {
                Circle newCircle = new Circle(id, circleJson.getJSONObject(idString));
                newCircle.addToStage(stage);
                circles.put(id, newCircle);
            }
        }

        for(String categoryName: categories.keySet()) {
            categoryScore.put(Integer.valueOf(categoryName),
                    categories
                            .getJSONObject(categoryName)
                            .getJSONObject("score")
                            .getFloat("score"));
        }
    }

    void updateLocal(final float delta) {
        ArrayList<Effect> removed = new ArrayList<>();
        for(Effect effect: effects) {
            effect.update(delta);
            if(effect.getTimeLeft() <= 0) {
                removed.add(effect);
            }
        }
        for(Effect effect: removed) {
            effects.remove(effect);
        }
    }

    private void circleHit(Circle circle) {
        hitCount += 1;
        hitCountLabel.setText(String.valueOf(hitCount));
        circle.markHit();
        createEffect(circle.getX(), circle.getY(), categoryScore.get(circle.category));
        game.sendHitMessage(playerId, circle.getId());
    }

    private void createEffect(float x, float y, float score) {
        Effect effect = new Effect(x, y, score);
        effects.add(effect);
        effect.addToStage(stage);
    }
}
