package com.mygdx.game.di.screen;

import com.mygdx.game.dependencies.Player;

import dagger.Module;
import dagger.Provides;

@Module
public class ScreenModule {

    @Screen
    @Provides
    public Player providePlayer() {
        return new Player();
    }

}

