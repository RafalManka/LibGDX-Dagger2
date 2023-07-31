package com.mygdx.game.di.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class GameModule {

    @Singleton
    @Provides
    public SpriteBatch provideSpriteBatch() {
        return new SpriteBatch();
    }
}
