# fire
use java class as cli easily, inspired by https://github.com/google/python-fire

### Use case

> just fire target class in your main method
```
    public static void main(String[] args) throws Exception {
        Fire.fire(Foo.class);
    }
```
> then run your main method

### Useful command

> /exit           // exit program
>
> /help        // print target class' method
>
> /refresh    //refresh context

### Invoke methods

> When invoke method, you should use pattern **methodName parameters**,
>
> For example, Foo.class has a method named **increment** and an integer parameter, you can invoke the method like this:

> ```increment 3```

### Parameters

#### primitive type and String
> Primitive type and String type in parameters can just write as **literal**, for example:
>
> ```toString hello```
>
> ```increment 3```

#### object
> when parameter cames to object, things will be a little bit tricky. you should write parameter using pattern
>
> **(classNameWithFullPath: constructor parameters)**
>
> For example
>
> ```setList (java.util.ArrayList: 10)```
>
> ```setBar (io.abiton.fire.Bar: hello, 3)```
>
> ```setBar (io.abiton.fire.Bar: (io.abiton.fire.Bar: world, 9))```
>
> 

