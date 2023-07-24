package com.mygdx.game.screens.game_screens;

import com.badlogic.gdx.graphics.Color;
import org.javatuples.Triplet;
import org.javatuples.Tuple;

import java.util.ArrayList;

public class ColorConverter {
    static public Triplet<Float, Float, Float> convert(double score) {
        float r = (score < 0? 255: 100 + (1 - (float)Math.abs(score)) * 155)/255f;
        float g = (score > 0? 255: 100 + (1 - (float)Math.abs(score)) * 155)/255f;
        float b = 0;
        return new Triplet(r,g,b);
    }
}
