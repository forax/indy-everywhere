# indy-everywhere
A sample code that rewrite a folder of classes to use invokedynamic instead of getfield/putfield/invokevirtual/invokeinterface

### How to build and run the project

This project uses pro as build tool.
First, you need to download pro using the wrapper (which requires Java 11)
```
  java pro_wrapper.java
```

then you can build and run using
```
  ./pro/bin/pro
```

### What does this project do exactly

The class [Rewriter](https://github.com/forax/indy-everywhere/blob/master/src/main/java/fr.umlv.indyeverywhere/fr/umlv/indyeverywhere/tool/Rewriter.java) takes a directory containing classes as parameter and rewrite the bytecode to use invokedynamic
instead of getfield/putfield/invokevirtual/invokeinterface (i.e. when the Java code access to a field or call an instance method).

In order to help to write overload methods without having to conform to the Java rules, all method names that are enclosed in one or several underscores have their name rewritten to remove the underscores (so \_foo\_ and \_\_foo\_\_ are both rewritten as foo).

The two bootstrap methods in [RT](https://github.com/forax/indy-everywhere/blob/master/src/main/java/fr.umlv.indyeverywhere/fr/umlv/indyeverywhere/RT.java) use the java.lang.invoke API to do the same thing as the JVM does, i.e. get the field value,
set the field value, call the virtual method.

By default, all the classes of the module fr.umlv.indyeverywhere.test are rewritten, in particular,
the class [IndyEveryWhereTest](https://github.com/forax/indy-everywhere/blob/master/src/main/java/fr.umlv.indyeverywhere.test/fr/umlv/indyeverywhere/test/IndyEveryWhereTest.java)
