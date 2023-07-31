package com.mygdx.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.dependencies.Player;
import com.mygdx.game.di.game.GameComponent;

import javax.inject.Inject;

public class ScreenB extends ScreenAdapter {

    @Inject
    SpriteBatch batch;

    @Inject
    Player player;

    public ScreenB(GameComponent component) {
        component.screenComponentFactory().create().inject(this);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        ScreenUtils.clear(1, 0, 0, 1);
    }
}
