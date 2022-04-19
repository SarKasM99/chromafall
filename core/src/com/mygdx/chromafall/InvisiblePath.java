package com.mygdx.chromafall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

public class InvisiblePath {
    private final int w = Gdx.graphics.getWidth();
    private final int h = Gdx.graphics.getHeight();
    private float[] amplitudes;
    private float[] waveLengths;
    private float[] phaseDifferences;
    private int nTerms;

    public InvisiblePath(int n) {
        nTerms = n;
        amplitudes = new float[n];
        waveLengths = new float[n];
        phaseDifferences = new float[n];
        for (int i = 0; i < n; i++) {
            amplitudes[i] = MathUtils.random(0.20f*w, 0.40f*w);
            waveLengths[i] = MathUtils.random(700, 1000);
            phaseDifferences[i] = MathUtils.random(0, 20);
        }
    }

    public float evaluate(float x) {
        float evalSum = 0;
        for (int i = 0; i < nTerms; i++) {
            evalSum += amplitudes[i] * (float) Math.sin(2* Math.PI * x / waveLengths[i] + phaseDifferences[i]);
        }
        assert evalSum < w;
        return evalSum + (float)w/2;
    }
}
