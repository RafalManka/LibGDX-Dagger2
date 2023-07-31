package com.mygdx.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.dependencies.MapHandler;
import com.mygdx.game.di.game.GameComponent;

import javax.inject.Inject;

public class ScreenA extends ScreenAdapter {

    @Inject
    SpriteBatch batch;

    @Inject
    MapHandler mapHandler;

    public ScreenA(GameComponent component) {
        component.screenComponentFactory().create().inject(this);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        ScreenUtils.clear(0, 1, 0, 1);
    }
}
