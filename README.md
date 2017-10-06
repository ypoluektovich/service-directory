We all know that God Object is an anti-pattern.
And yet, sometimes we do it anyway.

Some of those times are when we don't want to use Spring or some other initialization-by-configuration tool.
All those services depending on each other in counter-intuitive ways,
either in one huge file or a bunch of separate files. Static fields, signleton enums, you name it.

And then you remember that unit testing is also a thing, and different mocks add to the chaos.

This library wants to help you manage that.

## What's the idea?

- Your environment is represented by an interface (perhaps a combination of several interfaces for ease of use, but
  you'll have to make a single interface extending all those for this library). Each method returns a single
  service object.
- There's also a class implementing that interface. Each method takes dependencies by calling other methods,
  initializes the service it's responsible for, and returns it.
- The library ensures that the objects are initialized only once. You write the interface and the initialization,
  feed it in and get back an implementation of your environment interface that makes and returns singletons lazily.

## ServiceDirectoryBase and env()

I didn't want to bother with code generation, either at compile time or runtime, so this thing is running on
plain old Java proxies. Unfortunately, that means that your implementation class instance and the singleton-controlling
thing that the library builds are different objects. If you call a method in your implementation directly, it will not
benefit from singleton control.

To make the managed environment instance available to your code, you have to extend `ServiceDirectoryBase`
and get your dependencies through the `env()` object.

This will not do:

```java
public class EnvImpl implements Env {
	public ServiceA serviceA() { ... }
	public ServiceB serviceB() {
		return new ServiceB(serviceA());
	}
}
```

This is how you do it:

```java
public class EnvImpl extends ServiceDirectoryBase<Env> implements Env {
	public ServiceA serviceA() { ... }
	public ServiceB serviceB() {
		return new ServiceB(env().serviceA());
	}
}
```

## I will definitely forget to use env()

Yes, you will. That's what tests are for. The `env-tester` contains a test base class that can help catch this mistake.

As an added feature, it will also report initialization loops. Those are bad practice and are thus considered errors.

The initialization loop check is also performed at runtime. It is designed to catch both tight (A depends on A) and
more elaborate loops.

## How about more examples?

I will probably make more elaborate examples later, but for now you can look at the tests. I suggest `DependencyTest`.

## Thread safety?

Yes. Deadlocks will cause exceptions to be thrown.

## Performance?

Well, while this thing *is* lightweight, spamming invocations is probably not a good idea. So if you want to save
an instance of the service into a field or something, feel free. It'll also make your code more readable
if you have a list of dependencies in the beginning of your service instead of a reference to an environment object.

## License?

Mozilla Public License 2.0, see `LICENSE.txt`.

## Distribution?

JAR files are available on Bintray: [https://bintray.com/ypoluektovich/shoushitsu/service-directory]()

Setting up in Gradle:

```groovy
repositories {
    maven { url 'https://dl.bintray.com/ypoluektovich/shoushitsu/' }
}

dependencies {
    compile 'org.shoushitsu.service-directory:service-directory:1.+'
}
```

Versions are [semantic](http://semver.org/).

## Contribution?

You're welcome. Make issues and/or pull requests.