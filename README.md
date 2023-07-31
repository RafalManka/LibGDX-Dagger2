# Using Dagger 2 for dependency injection in a LibGDX project
As software grows in size and complexity, the need for managing dependencies between objects and 
classes effectively becomes increasingly critical. Dagger2, a fully static, compile-time dependency 
injection framework, has emerged as one of the top choices in the Android world for this purpose. 
In this article, we will explore how to utilize Dagger2 within a LibGDX game project.

Before we start, it's important to understand what LibGDX and Dagger2 are, and why we might want to 
use them together.

# How Dagger 2 works
"Dagger 2 is composed of the following fundamental components:
- Modules that deliver dependencies, declared using the @Provides annotation
- @Inject annotations which signify requests for injection
- @Component and @Subcomponent interfaces, which serve as the adhesive binding these elements together."

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
will create this class in the next step.

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

Initiate the creation of a subcomponent. Annotate the interface with the @Screen annotation, which 
we will develop shortly. Inside the @Subcomponent annotation, declare all your modules. For each 
screen, create an inject method and establish a @Subcomponent.Factory interface.

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

Proceed by creating a @Screen annotation. This will enable us to declare a scope, ensuring that all 
dependencies annotated with it will be instantiated once per screen.

```java
@Scope
@Documented
@Retention(RUNTIME)
public @interface Screen {
}
```

Next, declare your ScreenModule. For this example, I've chosen mapHandler, an object instantiated 
once for every screen. However, based on your specific requirements, this could be a completely 
different object.

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

Now, let's examine our game class. Within your create method, generate a new instance of your 
GameComponent. This location is also suitable for injecting all of your game dependencies, so make 
sure to invoke the inject method here as well. The remaining code is self-explanatory.

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

Finally, let's examine our Screens, specifically ScreenA and ScreenB. In both screens, we are 
passing the GameComponent in the constructor, from which we generate a new instance of a subcomponent. 
This subcomponent is then used to inject our dependencies. As SpriteBatch and Player dependencies 
are declared with different scopes, the same SpriteBatch instance will be injected into the Game 
and both screens. However, a new mapHandler instance will be created once for each screen.

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
The MapHandler class is empty and serves solely as an illustrative example.
```java
public class MapHandler {

}
```
# Summary
In this article, we delved into the process of integrating Dagger2, a dependency injection 
framework, into a LibGDX game project. By sequentially creating modules, components, subcomponents, 
and annotations, we managed dependencies with various scopes in a coherent and efficient manner.

We explored how to create a GameComponent for injecting game dependencies and how to generate 
subcomponents for specific screens, managing both global and screen-specific dependencies. Our 
demonstration highlighted the flexibility of Dagger2 in managing different scopes of dependencies, 
allowing the same instance to be utilized globally within the game object while permitting the 
creation of unique instances for each screen.

I encourage you to explore the corresponding repository located at 
https://github.com/RafalManka/LibGDX-Dagger2/ to further understand and experiment with the 
concepts presented in the article. Your feedback and suggestions are highly appreciated.

By employing Dagger2 in your LibGDX game project, you can streamline and organize your codebase, 
leading to more manageable, readable, and testable code. Happy coding!