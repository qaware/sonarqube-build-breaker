package de.qaware.tools.sqbb.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Helper class for lists.
 */
public final class Lists {
    private Lists() {
    }

    /**
     * Maps from a collection of type I to a list of type O
     *
     * @param in     source collection
     * @param mapper mapper function. Converts I to O
     * @param <I>    input type
     * @param <O>    output type
     * @return mapped list of type O
     */
    public static <I, O> List<O> map(Collection<I> in, Function<I, O> mapper) {
        List<O> result = new ArrayList<>(in.size());

        for (I element : in) {
            result.add(mapper.apply(element));
        }

        return result;
    }
}
