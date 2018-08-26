package com.myke.test.jmh.demo;

/**
 * 串行算法：使用 for-loop 来计算 n 个正整数之和。
 */
public class SinglethreadCalculator implements Calculator {

    public long sum(int[] numbers) {
        long total = 0L;
        for (int i : numbers) {
            total += i;
        }
        return total;
    }

    @Override
    public void shutdown() {
        // nothing to do
    }
}
