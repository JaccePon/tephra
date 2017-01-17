package org.lpw.tephra.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lpw.tephra.test.CoreTestSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lpw
 */
public class GeneratorTest extends CoreTestSupport {
    @Inject
    private Generator generator;

    @Test
    public void randomString() {
        String string = generator.random(1000);
        Assert.assertEquals(1000, string.length());
        Set<Character> set = new HashSet<>();
        for (char ch : string.toCharArray())
            set.add(ch);
        Assert.assertEquals(10 + 26, set.size());
        randomString(set, '0', '9');
        randomString(set, 'a', 'z');
    }

    @Test
    public void number() {
        String string = generator.number(1000);
        Assert.assertEquals(1000, string.length());
        Set<Character> set = new HashSet<>();
        for (char ch : string.toCharArray())
            set.add(ch);
        Assert.assertEquals(10, set.size());
        randomString(set, '0', '9');
    }

    @Test
    public void chars() {
        String string = generator.chars(1000);
        Assert.assertEquals(1000, string.length());
        Set<Character> set = new HashSet<>();
        for (char ch : string.toCharArray())
            set.add(ch);
        Assert.assertEquals(26, set.size());
        randomString(set, 'a', 'z');
    }

    protected void randomString(Set<Character> set, char start, char end) {
        for (char ch = start; ch < end; ch++)
            Assert.assertTrue(set.contains(ch));
    }

    @Test
    public void randomRange() {
        Assert.assertEquals(0, generator.random(0, 0));
        Assert.assertEquals(5, generator.random(5, 5));
        Assert.assertEquals(2, generator.random(5, 0));
        Assert.assertEquals(4, generator.random(8, 0));

        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < 100; i++)
            set.add(generator.random(0, 9));
        Assert.assertEquals(10, set.size());
        for (int i = 0; i < 10; i++)
            Assert.assertTrue(set.contains(i));
    }

    @Test
    public void uuid() {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < 10; i++)
            set.add(generator.uuid());
        Assert.assertEquals(10, set.size());
        for (String string : set)
            Assert.assertEquals(36, string.length());
    }
}
