package com.sergetk.cashik.counter;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * TBD
 */
public class Counter {
    private final AtomicLong counter = new AtomicLong(0);
    private final Consumer<Long> action;
    private final long hop;

    /**
     * Creates a counter calling @action each time when counter passes over @hop counts
     * @param hop number of counts to call @action
     * @param action called when a number of counts has reached again
     * @return instance of a counter
     */
    public static Counter each(long hop, Consumer<Long> action) {
        return new Counter(hop, action);
    }

    private Counter(long hop, Consumer<Long> action) {
        if (hop <= 0) throw new IllegalArgumentException("Count must be greater than zero");
        if (action == null) throw new IllegalArgumentException("Action shouldn't be null");
        this.hop = hop;
        this.action = action;
    }

    /**
     * Increases number of counts by 1. Calls an action function when the counter reaches a new hop.
     *
     * The action is called in the same thread as a count().
     */
    public void count() {
        long n = counter.incrementAndGet();
        if (n % hop == 0) {
            action.accept(n);
        }
    }

    /**
     * Increases number of counts by @delta. Calls an action function when the counter reaches a new hop or passes over one or more hops.
     *
     * @param delta
     */
    public void count(long delta) {
        long was = counter.getAndAdd(delta);
        long now = was + delta;
        if (now % hop == 0) {
            action.accept(now);
        } else {
            long pan = (was + delta) / hop;
            if (pan > was / hop) {
                action.accept(pan * hop);
            }
        }
    }

    /**
     * Calls an action function with current count value if it hasn't been called with the same value before.
     */
    public void last() {
        long n = counter.get();
        if (n % hop != 0) {
            action.accept(n);
        }
    }
}
