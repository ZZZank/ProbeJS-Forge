package com.probejs.util;

import com.github.bsideup.jabel.Desugar;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Desugar
public record Pair<F, S>(F first, S second) {

    public static <F, S> Collector<Pair<F, S>, ?, Map<F, S>> toMapCollector() {
        return Collectors.toMap(Pair::first, Pair::second);
    }

    public Pair<S, F> swap() {
        return new Pair<>(second, first);
    }

    public <F2> Pair<F2, S> mapFirst(final Function<F, ? extends F2> function) {
        return new Pair<>(function.apply(first), second);
    }

    public <S2> Pair<F, S2> mapSecond(final Function<S, ? extends S2> function) {
        return new Pair<>(first, function.apply(second));
    }
}
