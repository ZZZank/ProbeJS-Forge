package com.probejs.util;

public class Pair<F, S> {
    public final F first;
    public final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * get the first element in such pair
     */
    public F first() {
        return first;
    }

    /**
     * get the second element in such pair
     */
    public S second() {
        return second;
    }
}
