package com.myke.test.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime) //Benchmark 时所使用的模式
@OutputTimeUnit(TimeUnit.MILLISECONDS) //结果所使用的时间单位
@State(Scope.Thread)// 用于声明某个类是一个“状态”
public class FirstBenchMark {
    private static Logger log = LoggerFactory.getLogger(FirstBenchMark.class);

    @Benchmark
    public int sleepAWhile() {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            // ignore
        }
        return 0;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                // benchmark 所在的类的名字
                .include(FirstBenchMark.class.getSimpleName())
                //进行 fork 的次数
                .forks(1)
                //预热的迭代次数
                .warmupIterations(5)
                //实际测量的迭代次数。
                .measurementIterations(5)
                //.jvmArgs()
                .build();

        new Runner(opt).run();
    }

}
