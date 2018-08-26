### JMH 性能测试框架

#### 使用场景

-  想定量地知道某个函数需要执行多长时间，以及执行时间和输入 n 的相关性
- 一个函数有两种不同实现（例如实现 A 使用了 FixedThreadPool，
   实现 B 使用了 ForkJoinPool），不知道哪种实现性能更好
- 一个函数有两种不同实现（例如JSON序列化/反序列化有Jackson和Gson实现），不知道哪种实现性能更好

#### Code Sample
http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/

### 第一例子
1. 安装 JMH 插件
2. pom 依赖
```
<!--jmh-->
        <dependency>
            <groupId>org.openjdk.jmh</groupId>
            <artifactId>jmh-core</artifactId>
            <version>1.14.1</version>
        </dependency>
        <dependency>
            <groupId>org.openjdk.jmh</groupId>
            <artifactId>jmh-generator-annprocess</artifactId>
            <version>1.14.1</version>
            <scope>provided</scope>
        </dependency>
```

### 运行 benchmark 测试结果
```
# JMH 1.14.1 (released 705 days ago, please consider updating!)
# VM version: JDK 1.8.0_101, VM 25.101-b13
# VM invoker: D:\devsoft\jdk\jdk1.8\jre\bin\java.exe
# VM options: -javaagent:D:\devsoft\idea\lib\idea_rt.jar=50185:D:\devsoft\idea\bin -Dfile.encoding=UTF-8
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: com.myke.test.jmh.FirstBenchMark.sleepAWhile

# Run progress: 0.00% complete, ETA 00:00:10
# Fork: 1 of 1

# 预热迭代执行
# Warmup Iteration   1: 800.232 ms/op
# Warmup Iteration   2: 800.311 ms/op
# Warmup Iteration   3: 800.077 ms/op
# Warmup Iteration   4: 800.461 ms/op
# Warmup Iteration   5: 800.376 ms/op

# 正常的迭代执行
Iteration   1: 800.116 ms/op
Iteration   2: 800.416 ms/op
Iteration   3: 800.730 ms/op
Iteration   4: 800.197 ms/op
Iteration   5: 800.683 ms/op

# 结果
Result "sleepAWhile":
  800.428 ±(99.9%) 1.066 ms/op [Average]
  (min, avg, max) = (800.116, 800.428, 800.730), stdev = 0.277
  CI (99.9%): [799.362, 801.495] (assumes normal distribution)


# Run complete. Total time: 00:00:17

Benchmark                   Mode  Cnt    Score           Error  Units
FirstBenchMark.sleepAWhile  avgt    5  800.428 ± 1.066           ms/op
```

### 基本概念
### Mode
Mode 表示 JMH 进行 Benchmark 时所使用的模式。通常是测量的维度不同，或是测量的方式不同。目前 JMH 共有四种模式：
- Throughput: 整体吞吐量，例如“1秒内可以执行多少次调用”。
- AverageTime: 调用的平均时间，例如“每次调用平均耗时xxx毫秒”。
- SampleTime: 随机取样，最后输出取样结果的分布，例如“99%的调用在xxx毫秒以内，99.99%的调用在xxx毫秒以内”
- SingleShotTime: 以上模式都是默认一次 iteration 是 1s，唯有 SingleShotTime 是只运行一次。往往同时把 warmup 次数设为0，用于测试冷启动时的性能。
- All(“all”, “All benchmark modes”);

### Iteration
Iteration 是 JMH 进行测试的最小单位。在大部分模式下，一次 iteration 代表的是一秒，JMH 会在这一秒内不断调用需要 benchmark 的方法，然后根据模式对其采样，计算吞吐量，计算平均执行时间等。

### Warmup
Warmup 是指在实际进行 benchmark 前先进行预热的行为。为什么需要预热？因为 JVM 的 JIT 机制的存在，如果某个函数被调用多次之后，JVM 会尝试将其编译成为机器码从而提高执行速度。所以为了让 benchmark 的结果更加接近真实情况就需要进行预热。


### 注解

### @BenchmarkMode
Mode 如之前所说，表示 JMH 进行 Benchmark 时所使用的模式 Mode。

### @Warmup
进行基准测试前需要进行预热。一般我们前几次进行程序测试的时候都会比较慢，所以要让程序进行几轮预热，保证测试的准确性。其中的参数iterations也就非常好理解了，就是预热轮数。

> 为什么需要预热？因为 JVM 的 JIT 机制的存在，如果某个函数被调用多次之后，JVM 会尝试将其编译成为机器码从而提高执行速度。所以为了让 benchmark 的结果更加接近真实情况就需要进行预热。

### @Measurement
度量，其实就是一些基本的测试参数。

- iterations 进行测试的轮次
- time 每轮进行的时长
- timeUnit 时长单位

都是一些基本的参数，可以根据具体情况调整。一般比较重的东西可以进行大量的测试，放到服务器上运行。

### @Threads
每个fork进程使用多少条线程去执行你的测试方法，默认值是Runtime.getRuntime().availableProcessors()。

### @Fork
进行 fork 的次数。如果 fork 数是2的话，则 JMH 会 fork 出两个进程来进行测试。

### @OutputTimeUnit
这个比较简单了，基准测试结果的时间类型。一般选择秒、毫秒、微秒。

### @Benchmark
方法级注解，表示该方法是需要进行 benchmark 的对象，用法和 JUnit 的 @Test 类似。

### @Param
属性级注解，@Param 可以用来指定某项参数的多种情况。特别适合用来测试一个函数在不同的参数输入的情况下的性能。

### @Setup
方法级注解，这个注解的作用就是我们需要在测试之前进行一些准备工作，比如对一些数据的初始化之类的。

### @TearDown
方法级注解，这个注解的作用就是我们需要在测试之后进行一些结束工作，比如关闭线程池，数据库连接等的，主要用于资源的回收等。

### @State
当使用@Setup参数的时候，必须在类上加这个参数，不然会提示无法运行。
State 用于声明某个类是一个“状态”，然后接受一个 Scope 参数用来表示该状态的共享范围。
因为很多 benchmark 会需要一些表示状态的类，JMH 允许你把这些类以依赖注入的方式注入到 benchmark 函数里。

Scope 主要分为三种
- Thread: 该状态为每个线程独享。默认的State，每个测试线程分配一个实例；
- Group: 该状态为同一个组里面所有线程共享。每个线程组共享一个实例；
- Benchmark: 该状态在所有线程间共享。所有测试线程共享一个实例，用于测试有状态实例在多线程共享下的性能；

关于State的用法，参考官方 http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/


### 启动选项
```
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FirstBenchMark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }
```

### include
benchmark 所在的类的名字

### fork
进行 fork 的次数。如果 fork 数是2的话，则 JMH 会 fork 出两个进程来进行测试。

### warmupIterations
预热的迭代次数。

### measurementIterations
实际测量的迭代次数。


### 第二个例子
> 并行算法在哪个问题集下能够超越串行算法？

### 结果
```
Benchmark                          (length)  Mode  Cnt    Score    Error  Units
SecondBenchmark.multiThreadBench      10000  avgt   10   10.926 ±  0.568  us/op
SecondBenchmark.multiThreadBench     100000  avgt   10   29.270 ±  0.999  us/op
SecondBenchmark.multiThreadBench    1000000  avgt   10  138.048 ±  8.744  us/op

SecondBenchmark.singleThreadBench     10000  avgt   10    3.553 ±  0.133  us/op
SecondBenchmark.singleThreadBench    100000  avgt   10   35.975 ±  1.715  us/op
SecondBenchmark.singleThreadBench   1000000  avgt   10  529.205 ± 15.662  us/op
```


### 常用选项

### @CompilerControl
可以在@Benchmark注解中指定编译器行为。控制 compiler 的行为，例如强制 inline，不允许编译等。

### @Group
可以把多个 benchmark 定义为同一个 group，则它们会被同时执行，
> 参考 https://github.com/chrishantha/microbenchmarks/blob/v0.0.1-initial-counter-impl/counters/src/main/java/com/github/chrishantha/microbenchmark/counter/CounterBenchmark.java


### Level
用于控制 @Setup，@TearDown 的调用时机，默认是 Level.Trial，即benchmark开始前和结束后。
- Trial：每个benchmark方法前后；
- Iteration：每个benchmark方法每次迭代前后；
- Invocation：每个benchmark方法每次调用前后，谨慎使用，需留意javadoc注释；

### Profiler
JMH 支持一些 profiler，可以显示等待时间和运行时间比，热点函数等。


### JMH 参考教程
http://tutorials.jenkov.com/java-performance/jmh.html

http://www.importnew.com/12548.html

例子 https://blog.csdn.net/lxbjkben/article/details/79410740

官网  http://openjdk.java.net/projects/code-tools/jmh/

例子  http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/