package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.di.game.DaggerGameComponent;
import com.mygdx.game.di.game.GameComponent;
import com.mygdx.game.screens.ScreenA;
import com.mygdx.game.screens.ScreenB;

import javax.inject.Inject;

public class DaggerGame extends Game {

    private GameComponent gameComponent;

    @Inject
    SpriteBatch batch;

    protected boolean isA = false;

    @Override
    public void create() {
        gameComponent = DaggerGameComponent.create();
        gameComponent.inject(this);
        changeScreen();
    }

    @Override
    public void render() {
        super.render();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            changeScreen();
        }
    }

    private void changeScreen() {
        if (isA) {
            setScreen(new ScreenB(gameComponent));
        } else {
            setScreen(new ScreenA(gameComponent));
        }
        isA = !isA;
    }
}
