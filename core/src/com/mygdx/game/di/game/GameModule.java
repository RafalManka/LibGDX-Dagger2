package com.mygdx.game.di.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.ScreenA;
import com.mygdx.game.screens.ScreenB;

import dagger.Module;
import dagger.Provides;


@Module
public class GameModule {

    @Provides
    public ScreenA provideMainMenuScreen(GameComponent component) {
        return new ScreenA(component);
    }

    @Provides
    public ScreenB provideGameScreen(GameComponent component) {
        return new ScreenB(component);
    }

    @Game
    @Provides
    public SpriteBatch provideSpriteBatch() {
        return new SpriteBatch();
    }
}
