package ch.admin.bar.siard2.gui.math;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class MaxOfTests {

    @Test
    public void shouldGetMaxOfIntegers() {
        assertThat(new MaxOf(5, 6, 8, 3).get(), is(8));
        assertThat(new MaxOf(1).get(), is(1));
        assertThat(new MaxOf(1, 1, 0, -1).get(), is(1));
    }

    @Test
    public void shouldGetMaxOfDoubles() {
        assertThat(new MaxOf(1.1, 1.2, 0, -0.1).get(), is(1.2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldGetMaxOfEmptyList() {
        new MaxOf().get();
    }


}
