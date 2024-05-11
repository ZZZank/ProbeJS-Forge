package com.probejs.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Pair<F, S> {

    private final F first;
    private final S second;

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
