# Using Dagger 2 for dependency injection in a LibGDX project
This article describes how to implement dependency injection using Dagger 2 in a LibGDX project.

# How Dagger 2 works
Dagger 2 consists of the following building blocks
- Modules providing dependencies declared with @Provides annotation
- @Inject annotations that request for injection
- @Component and @Subcomponent interfaces that glue those things together

# Scopes
typically in a LibGDX game you are going to need some of your dependencies to be 
instantiated once per the entire game while other dependencies per individual screen. We are going 
to take advantage of Scopes to achieve that alongside of Components and Subcomponents.

## @Singleton
This annotation is given to us by Dagger. It ensures that all dependencies annotated with it
will be only created once per the entire game.

## @Screen
This annotation we need to create ourselves. Together with @Subcomponent annotation we ensure that 
all dependencies annotated with it will be created once per screen.

# Using Dagger2 in your project
Create a Module. Notice we are annotating provide method with @Provides annotation and @Singleton 
annotations. That means this methos will be used to create instance of SpriteBatch and it will be 
created once per the whole game. After it is created it will be stored by dagger internally and 
reused making sure that you will use the same instance of SpriteBatch in all places in your code 
```java
@Module
public class GameModule {

    @Singleton
    @Provides
    public SpriteBatch provideSpriteBatch() {
        return new SpriteBatch();
    }
}
```
Create a component. In order for any of the modules to be able to use @Singleton annotation you 
need to annotate your component with that annotation as well. This is how you declare a scope. Next,
declare all your modules inside the @Component annotation. Create inject method that takes the Game
as a parameter, this will allow us to pass our game object to dagger so it can fulfill all our
dependencies. Create factory method that returns ScreenComponent.Factory, we will create this class 
in a moment.
```java
@Singleton
@Component(modules = {
GameModule.class
})
public interface GameComponent {
    void inject(DaggerGame game);

    ScreenComponent.Factory screenComponentFactory();
}
```
Create a subcomponent. Annotate the interface with @Screen annotation which we will create in a 
second. Declare all your modules inside @Subcomponent annotation. create inject method for every 
screen and create @subcomponent.Factory interface.
```java
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
```
Create a @Screen annotation. This will allow us to declare a scope and make sure that all 
dependencies annotated with it will be instantiated once per screen.
```java
@Scope
@Documented
@Retention(RUNTIME)
public @interface Screen {
}
```
Now declare your ScreenModule. I took mapHandler as an example of an object that is instantiated
once per every screen, but depending on you requirements this can be something else entirely.
```java
@Module
public class ScreenModule {

    @Screen
    @Provides
    public MapHandler provideMapHandler() {
        return new MapHandler();
    }

}
```
Now lets have a look at our game class. Inside your create method make a new instance of your 
GameComponent. This is also the correct place to inject all of your game dependencies, so call the 
inject method as well. the rest of the code is self explanatory.
```java
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
```
Finally lets look at our Screens. ScreenA and ScreenB respectfully. In both of the screens we are
passing the GameComponent in a constructor, from which we are creating a new instance of 
subcomponent which then we use to inject our dependencies. because SpriteBatch and Player 
dependencies are declared with different scopes, the same instance of SpriteBatch will
be injected in Game and both of the screens. But the new mapHandler instance will be created once per 
each screen.
```java
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

public class ScreenB extends ScreenAdapter {

    @Inject
    SpriteBatch batch;

    @Inject
    MapHandler mapHandler;

    public ScreenB(GameComponent component) {
        component.screenComponentFactory().create().inject(this);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        ScreenUtils.clear(1, 0, 0, 1);
    }
}
```
the MapHandler class in empty and it's here just as an example
```java
public class MapHandler {

}
```
