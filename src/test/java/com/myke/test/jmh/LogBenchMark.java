package com.myke.test.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 在日志输出中使用微基准测试
 * <p>
 * 预期结果：
 * 每秒的操作次数越多，表示性能越好
 */
//@BenchmarkMode(Mode.AverageTime)
//@Fork(value = 2)
//@Threads(10)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@Warmup(iterations = 1)
//@Measurement(iterations = 2)
public class LogBenchMark {
    private static final Logger logger = LoggerFactory.getLogger(LogBenchMark.class);


    /**
     * 在日志中使用字符串连接
     */
    @Benchmark
    public void testConcatenatingStrings() {
        String x = "",
                y = "",
                z = "";

        for (int i = 0; i < 100; i++) {
            x += i;
            y += i;
            z += i;
            logger.debug("Concatenating strings " + x + y + z);
        }
    }

    /**
     * 使用变量参数来代替字符串连接
     */
    @Benchmark
    public void testVariableArguments() {
        String x = "",
                y = "",
                z = "";

        for (int i = 0; i < 100; i++) {
            x += i;
            y += i;
            z += i;
            logger.debug("Variable arguments {} {} {}", x, y, z);
        }
    }

    /**
     * 使用日志输出时使用isDebugEnabled()进行优化
     */
    @Benchmark
    public void testIfDebugEnabled() {
        String x = "",
                y = "",
                z = "";

        for (int i = 0; i < 100; i++) {
            x += i;
            y += i;
            z += i;

            if (logger.isDebugEnabled()) {
                logger.debug("If debug enabled {} {} {}", x, y, z);
            }
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(LogBenchMark.class.getSimpleName())
                .forks(1)
                .threads(2)
                .warmupIterations(2)
                .measurementIterations(5)
                .resultFormat(ResultFormatType.JSON)
                .result("log")
                .output("D:\\log.json")
                .build();
        new Runner(options).run();
    }

}
