package com.probejs.util;

public class Pair<F, S> {

    private final F first;
    private final S second;

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

    @Override
    public String toString() {
        return String.format("Pair{first=%s, second=%s}", this.first, this.second);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.first, this.second);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Pair)) {
            return false;
        }
        Pair<F, S> p = (Pair<F, S>) obj;
        return this.first.equals(p.first) && this.second.equals(p.second);
    }
}
