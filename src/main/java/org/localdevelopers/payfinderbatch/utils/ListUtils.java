package org.localdevelopers.payfinderbatch.utils;

import java.util.List;
import java.util.stream.Collectors;

public class ListUtils<T> {

    public static <T> List<T> getIntersection(final List<T> source, final List<T> target) {
        return source.stream()
                .filter(target::contains)
                .collect(Collectors.toList());
    }

    public static <T> List<T> getDifference(final List<T> source, final List<T> target) {
        return source.stream()
                .filter(it -> !target.contains(it))
                .collect(Collectors.toList());
    }
}
