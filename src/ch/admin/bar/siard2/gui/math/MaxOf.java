package ch.admin.bar.siard2.gui.math;

import java.util.Arrays;
import java.util.List;

public class MaxOf {

    private List<Number> numbers;

    public MaxOf(Number... number) {
        this.numbers = Arrays.asList(number);
    }

    public Number get() {
        if (this.numbers.size() == 0) throw new IllegalArgumentException("list of number has no elements");
        this.numbers.sort((a, b) -> Double.compare(b.doubleValue(), a.doubleValue()));
        return this.numbers.get(0);
    }
}
