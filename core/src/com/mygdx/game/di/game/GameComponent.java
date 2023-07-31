package com.mygdx.game.di.game;

import com.mygdx.game.DaggerGame;
import com.mygdx.game.di.screen.ScreenComponent;

import dagger.Component;

@Game
@Component(modules = {
        GameModule.class
})
public interface GameComponent {
    void inject(DaggerGame game);

    ScreenComponent.Factory screenComponentFactory();
}

