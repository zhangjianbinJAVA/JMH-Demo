package com.myke.test.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * String 和 StringBuilder 哪个效率好
 */
@BenchmarkMode(Mode.Throughput)//基准测试类型
@Warmup(iterations = 3) //进行基准测试前需要进行预热
//测试参数设置 iterations进行测试的轮次，time每轮进行的时长，timeUnit时长单位
@Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(10)//每个进程中的测试线程
@Fork(value = 2)//进行 fork 的次数
@OutputTimeUnit(TimeUnit.MILLISECONDS) //基准测试结果的时间类型
@State(Scope.Thread)//当使用@Setup参数的时候，必须在类上加这个参数，不然会提示无法运行
public class StringBuildTest2 {

    private static final int SIZE = 0x4ff;

    private int index = 0;

    private final String[] a = new String[SIZE];
    private final String[] b = new String[SIZE];
    private final String[] c = new String[SIZE];

    @Setup//对一些数据的初始化
    public void setup() {

        Random random = new Random();
        for (int i = 0; i < SIZE; i++) {
            a[i] = random.nextInt() + "";
            b[i] = random.nextLong() + "";
            c[i] = random.nextDouble() + "";
        }
    }

    @Benchmark
    public String testStringFormat() {
        if (++index >= SIZE)
            index = 0;
        return String.format("%s:%s:%s", a[index], b[index], c[index]);
    }

    @Benchmark
    public String testStringAdd() {
        if (++index >= SIZE)
            index = 0;
        return a[index] + ':' + b[index] + ':' + c[index];
    }

    @Benchmark
    public String testStringBuilder() {
        if (++index >= SIZE)
            index = 0;
        return new StringBuilder(a[index])
                .append(':')
                .append(b[index])
                .append(':')
                .append(c[index])
                .toString();
    }


    /**
     * 执行 测试
     * <p>
     * 测试结果：
     * # Run complete. Total time: 00:05:41
     * <p>
     * Benchmark                            Mode  Cnt      Score      Error   Units
     * StringBuildTest2.testStringAdd      thrpt   20  22024.021 ± 1654.028  ops/ms
     * StringBuildTest2.testStringBuilder  thrpt   20  27400.445 ± 1168.679  ops/ms
     * StringBuildTest2.testStringFormat   thrpt   20   2785.392 ±   81.917  ops/ms
     * <p>
     * error那列其实没有内容，score的结果是xxx ± xxx，单位是每毫秒多少个操作
     * <p>
     * StringBuilder的速度确实是要比String进行文字叠加的效率好
     *
     * @param args
     */
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(StringBuildTest2.class.getSimpleName())
                .output("E:/Benchmark.log")
                .build();
        new Runner(options).run();
    }
}
