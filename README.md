# Using Dagger 2 for dependency injection in a LibGDX project
As software grows in size and complexity, the need for managing dependencies between objects and 
classes effectively becomes increasingly critical. Dagger2, a fully static, compile-time dependency 
injection framework, has emerged as one of the top choices in the Android world for this purpose. 
In this article, we will explore how to utilize Dagger2 within a LibGDX game project.

Before we start, it's important to understand what LibGDX and Dagger2 are, and why we might want to 
use them together.

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


## Let's Dive In: Integrating Dagger2 into a LibGDX Project
Add Dagger2 Dependency
To begin, we need to add the Dagger2 library to our LibGDX project's Gradle build file. Add the 
following lines to your project's build.gradle file in the "core" project block:
```groovy
dependencies {
    implementation 'com.google.dagger:dagger:2.45'
    annotationProcessor 'com.google.dagger:dagger-compiler:2.45'
}
```
# Using Dagger2 in your project
Begin by creating a Module. Take note that the provision method is annotated with both @Provides and 
@Singleton annotations. This signifies that this method will be used to generate a SpriteBatch 
instance, and this creation will only occur once throughout the entire game. Once created, Dagger 
will internally store and subsequently reuse this instance, ensuring that the same SpriteBatch 
instance is utilized consistently throughout your code.
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
Create a component. To enable any of the modules to use the @Singleton annotation, your component 
must be annotated with the same. This is how you declare a scope. Subsequently, declare all your 
modules within the @Component annotation. Proceed to create an injection method that accepts 'Game' 
as a parameter, which will allow us to provide our game object to Dagger for the fulfillment of all 
our dependencies. Generate a factory method that returns a ScreenComponent.Factory instance. We 
will create this class shortly.
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
