package com.myke.test.jmh;

import com.myke.test.jmh.demo.Calculator;
import com.myke.test.jmh.demo.MultithreadCalculator;
import com.myke.test.jmh.demo.SinglethreadCalculator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 计算 1 ~ n 之和，比较串行算法和并行算法的效率，看 n 在大约多少时并行算法开始超越串行算法
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class SecondBenchmark {

    @Param({"10000", "100000", "1000000"})//用来指定某项参数的多种情况
    private int length;

    private int[] numbers;
    private Calculator singleThreadCalc;
    private Calculator multiThreadCalc;

    @Setup //会在执行 benchmark 之前被执行，正如其名，主要用于初始化
    public void prepare() {
        numbers = IntStream.rangeClosed(1, length).toArray();
        singleThreadCalc = new SinglethreadCalculator();
        multiThreadCalc = new MultithreadCalculator(Runtime.getRuntime().availableProcessors());
    }

    @TearDown //会在所有 benchmark 执行结束以后执行，主要用于资源的回收等。
    public void shutdown() {
        singleThreadCalc.shutdown();
        multiThreadCalc.shutdown();
    }


    @Benchmark
    public long singleThreadBench() {
        return singleThreadCalc.sum(numbers);
    }

    @Benchmark
    public long multiThreadBench() {
        return multiThreadCalc.sum(numbers);
    }


    /**
     * 执行测试
     *
     * @param args
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SecondBenchmark.class.getSimpleName())
                .forks(2)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }


}
