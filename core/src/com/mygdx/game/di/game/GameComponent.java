package com.mygdx.game.di.game;

import com.mygdx.game.DaggerGame;
import com.mygdx.game.di.screen.ScreenComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        GameModule.class
})
public interface GameComponent {
    void inject(DaggerGame game);

    ScreenComponent.Factory screenComponentFactory();
}

