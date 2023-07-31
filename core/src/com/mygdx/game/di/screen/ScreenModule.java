package com.mygdx.game.di.screen;

import com.mygdx.game.dependencies.MapHandler;

import dagger.Module;
import dagger.Provides;

@Module
public class ScreenModule {

    @Screen
    @Provides
    public MapHandler provideMapHandler() {
        return new MapHandler();
    }

}

