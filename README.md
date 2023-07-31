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
- Create a Module. Notice we are annotating provide method with @Provides annotation and @Singleton 
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

- Create a component. In order for any of the modules to be able to use @Singleton annotation you 
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

- Create a subcomponent. Annotate the interface with @Screen annotation which we will create in a 
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

- Create a @Screen annotation. This will allow us to declare a scope and make sure that all 
dependencies annotated with it will be instantiated once per screen.
```java
@Scope
@Documented
@Retention(RUNTIME)
public @interface Screen {
}
```

- Now declare your ScreenModule. I took player as an example of an object that is instantiated
once per every screen, but depending on you requirements this can be something else entirely.
```java
@Module
public class ScreenModule {

    @Screen
    @Provides
    public Player providePlayer() {
        return new Player();
    }

}
```
