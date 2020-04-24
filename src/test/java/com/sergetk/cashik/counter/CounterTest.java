package com.sergetk.cashik.counter;

import org.junit.Test;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class CounterTest {
    @Test
    public void testIncrement() {
        TestAction act = new TestAction();
        Counter cnt = Counter.each(10, act);
        assertEquals("Counter shouldn't call action function at creation", 0, act.calls);
        IntStream.range(1, 9 + 1).forEach(x -> {
            cnt.count();
            assertEquals("Counter shouldn't call action function until first hop reached", 0, act.calls);
        });

        cnt.count(); // 10
        assertEquals("Action function should be called when first hop reached", 1, act.calls);
        assertEquals("Action function should be called with reached value", 10, act.value.longValue());
        IntStream.range(11, 19 + 1).forEach(x -> {
            cnt.count();
            assertEquals("Counter shouldn't call action function until the next hop reached", 1, act.calls);
        });

        cnt.count(); // 20
        assertEquals("Action function should be called when next hop reached", 2, act.calls);
        assertEquals("Action function should be called with next reached value hop reached", 20, act.value.longValue());
    }

    @Test
    public void testLastOverHop() {
        TestAction act = new TestAction();
        Counter cnt = Counter.each(10, act);
        IntStream.range(1, 15 + 1).forEach(x -> cnt.count());
        assertEquals("Action function should be called once when counter passed over the hop", 1, act.calls);
        cnt.last();
        assertEquals("Action function should be called on last if counter passed over the hop", 2, act.calls);
        assertEquals("Action function should be called on last if counter passed over the hop", 15, act.value.longValue());
    }

    @Test
    public void testLastOnHop() {
        TestAction act = new TestAction();
        Counter cnt = Counter.each(10, act);
        IntStream.range(1, 10 + 1).forEach(x -> cnt.count());
        assertEquals("Action function should be called once ", 1, act.calls);
        cnt.last();
        assertEquals("Action function shouldn't be called on last if counter reached the hop", 1, act.calls);
        assertEquals("Action function shouldn't be called on last if counter reached the hop", 10, act.value.longValue());
    }

    @Test
    public void testAdd() {
        TestAction act = new TestAction();
        Counter cnt = Counter.each(10, act);
        cnt.count(5);
        assertEquals("Action function shouldn't be called if counter didn't reach the hop", 0, act.calls);
        cnt.count(5);
        assertEquals("Action function should be called if counter reached the hop", 1, act.calls);
        assertEquals("Action function should be called if counter reached the hop", 10, act.value.longValue());
        cnt.count();
        cnt.count(25);
        assertEquals("Action function should be called if counter jumps over the hop", 2, act.calls);
        assertEquals("Action function should be called if counter jumps over the hop", 30, act.value.longValue());
    }

    private static class TestAction implements Consumer<Long> {
        private Long value;
        private long calls = 0;

        @Override
        public void accept(Long count) {
            this.value = count;
            this.calls++;
        }
    }
}
