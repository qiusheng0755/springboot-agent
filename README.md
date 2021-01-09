# JavaAgent 
JavaAgent 是JDK 1.5 以后引入的，也可以叫做Java代理。

JavaAgent 是运行在 main方法之前的拦截器，它内定的方法名叫 premain ，也就是说先执行 premain 方法然后再执行 main 方法。



# ByteBuddy

## 1.ByteBuddy是什么

Simply put, ByteBuddy is a library for generating Java classes dynamically at run-time.
简单来说，ByteBuddy是一个可以在运行时动态生成java class的类库。
In this to-the-point article, we're going to use the framework to manipulate existing classes, create new classes on demand, and even intercept method calls.
在这篇文章中，我们将会使用ByteBuddy这个框架操作已经存在的类，创建指定的新类，甚至拦截方法调用。

## 2.依赖

在开始实现功能之前，需要先将bytebuddy的依赖加入到项目中。我的项目是基于maven的，所以需要将
```
<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy</artifactId>
    <version>1.7.1</version>
</dependency>
```
加入到pom.xml中。如果项目是基于gradle的，可以将compile net.bytebuddy:byte-buddy:1.7.1这个依赖加入到build.gradle文件中

## 3.运行时创建java类

首先我们动态创建一个现有类的子类。我们来看看这个bytebuddy中的景点的Hello World项目。

在这个例子中，我们会创建一个类，这个类是Object.class的子类同时会覆盖Object.class的toString()方法：
```
DynamicType.Unloaded unloadedType = new ByteBuddy()
  .subclass(Object.class)
  .method(ElementMatchers.isToString())
  .intercept(FixedValue.value("Hello World ByteBuddy!"))
  .make();
```
我们一起看下上面的代码的含义：

- new ByteBuddy()很好理解，创建了ByteBuddy类型的一个实例

- subClass(Object.class)含义是动态创建的类是继承Object类的

- method(ElementMatchers.isToString())类似一个筛选器，这里选中的是Object类中的toString()方法

- intercept(FixedValue.value("Hello World ByteBuddy!"))提供了了toString()的实现，这里的实现是返回一个固定的值"Hello World ByteBuddy!"

- make()触发生成一个新的类


这个时候，新的类已经被创建出来了，但是还没有被加载到JVM中。这个新的类的表现形式是DynamicType.Unloaded的一个实例，具体地说是DynamicType.Unloaded中包含了新的类的字节码。

所以在使用生成的类之前我们先要把它加载到JVM中：
```
Class<?> dynamicType = unloadedType.load(getClass()
  .getClassLoader())
  .getLoaded();
```
现在我们可以实例化dynamicType代表的class类型，然后调用这个实例的toString()方法了：
```
assertEquals(dynamicType.newInstance().toString(), "Hello World ByteBuddy!");
```
注意：调用dynamicType.toString()方法是不会生效的，因为这样操作实际上是会调用ByteBuddy.class的toString()方法

newInstance()是一个java反射方法用于创建ByteBuddy对象表示的实例；这个方式就类似于使用无参构造函数创建一个对象。

到目前为止，我们仅仅是在我们的动态创建的类型中覆写了父类中的方法同时返回一个固定的值。在下一节中，我们将定义一个自定义逻辑的方法。

## 4.方法代理和自定义方法逻辑

在前面的例子中，我们覆写了父类的toString()方法并返回了一个固定的值。

事实上，应用中需要的逻辑要比这个复杂很多。为动态类型提供自定义逻辑的一种有效方法是方法调用的委托。

让我们创建一个动态类型，这个动态类型继承Foo.class，同时Foo.class有一个sayHelloFoo()方法
```
public String sayHelloFoo() { 
    return "Hello in Foo!"; 
}
```
此外，让我们再创建一个Bar类，Bar类带有一个静态的sayHelloBar()方法，sayHelloBar()方法的前面和返回类型和sayHelloFoo()方法一致
```
public static String sayHelloBar() { 
    return "Holla in Bar!"; 
}
```
现在，我们使用ByteBuddy的DSL将所有调用sayHelloFoo()的请求都代理到sayHelloBar()上。这样就允许我们使用纯java语言，在我们运行时生成的新类上提供自定义的逻辑。
```
String r = new ByteBuddy()
  .subclass(Foo.class)
  .method(named("sayHelloFoo")
    .and(isDeclaredBy(Foo.class)
    .and(returns(String.class))))        
  .intercept(MethodDelegation.to(Bar.class))
  .make()
  .load(getClass().getClassLoader())
  .getLoaded()
  .newInstance()
  .sayHelloFoo();
         
assertEquals(r, Bar.sayHelloBar());
```
调用sayHelloFoo()方法实际上会调用sayHelloBar()方法

ByteBuddy怎么知道该调用Bar.class中的哪个方法？ByteBuddy根据方法签名、返回值类型、方法名、注解的顺序来匹配方法（越后面的优先级越高）。

sayHelloFoo()方法和sayHelloBar()方法的方法名不一样，但是它们有相同的方法签名和返回值类型。

如果在Bar.class中有超过一个可调用的方法的签名和返回类型一致，我们可以使用@BindingPriority来解决冲突。@BindingPriority有一个整型参数-这个值越大优先级越高。因此，在下面的代码片段中sayHelloBar()方法将会被调用：
```
@BindingPriority(3)
public static String sayHelloBar() { 
    return "Holla in Bar!"; 
}

@BindingPriority(2)
public static String sayBar() { 
    return "bar"; 
}
```
## 5.方法和字段定义

我们可以在我们动态创建的类中覆写父类中的方法了。让我们更进一步，在我们的类中添加一个新的方法和一个新的字段。

我们将使用java反射动态调用创建的方法：
```
Class<?> type = new ByteBuddy()
  .subclass(Object.class)
  .name("MyClassName")
  .defineMethod("custom", String.class, Modifier.PUBLIC)
  .intercept(MethodDelegation.to(Bar.class))
  .defineField("x", String.class, Modifier.PUBLIC)
  .make()
  .load(
    getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
  .getLoaded();

Method m = type.getDeclaredMethod("custom", null);
assertEquals(m.invoke(type.newInstance()), Bar.sayHelloBar());
assertNotNull(type.getDeclaredField("x"));
```
我们创建了一个叫做MyClassName的类，它是Object.class的子类。然后我们定义了一个方法，叫做custom，它是public的，同时返回String。

就像之前的例子，我们通过代理方法请求给Bar.class实现我们的方法。

## 6.重定义一个已经存在的类

虽然我们可以动态创建类，我们也可以操作已经加载的类。ByteBuddy可以重定义已经存在的类，然后使用ByteBuddyAgent将重定义的类重新加载到JVM中。

首先，让我们添加ByteBuddyAgent依赖到pom.xml：
```
<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy-agent</artifactId>
    <version>1.7.1</version>
</dependency>
```
现在，让我们重定义之前我们创建的Foo.sayHelloFoo()方法：
```
ByteBuddyAgent.install();
new ByteBuddy()
  .redefine(Foo.class)
  .method(named("sayHelloFoo"))
  .intercept(FixedValue.value("Hello Foo Redefined"))
  .make()
  .load(
    Foo.class.getClassLoader(), 
    ClassReloadingStrategy.fromInstalledAgent());

Foo f = new Foo();

assertEquals(f.sayHelloFoo(), "Hello Foo Redefined");
```
## 7.总结

在这个精心制作的入门指南中， 我们已经广泛地研究了ByteBuddy库的功能以及可以如何有效地使用ByteBuddy动态的创建类。

ByteBuddy的文档对于内部工作原理以及类库的其他方面提供了更加深入的解释。

最后，可以在

[GitHub]: https://github.com/eugenp/tutorials/tree/master/libraries-5

 上找到这个教程中完整的代码片段。

