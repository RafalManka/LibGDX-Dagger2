package com.mygdx.game.di.screen;

import com.mygdx.game.screens.ScreenA;
import com.mygdx.game.screens.ScreenB;

import dagger.Subcomponent;

@Screen
@Subcomponent(modules = {
        ScreenModule.class
})
public interface ScreenComponent {
    void inject(ScreenA screen);

    void inject(ScreenB screen);

    @Subcomponent.Factory
    interface Factory {
        ScreenComponent create();
    }
}
