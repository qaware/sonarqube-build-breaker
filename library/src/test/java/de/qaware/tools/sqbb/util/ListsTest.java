package de.qaware.tools.sqbb.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ListsTest {
    @Test
    void map() {
        List<String> actual = Lists.map(Arrays.asList(1, 2, 3, 4, 5), i -> Integer.toString(i));

        assertThat(actual).containsExactly("1", "2", "3", "4", "5");
    }
}