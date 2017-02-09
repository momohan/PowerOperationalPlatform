package com.handu.poweroperational;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        setTest();
    }

    private void print(Object o) {
        System.out.println(o);
    }

    private void Mp() {
        int number[] = {2, 5, 1, 3, 5, 1, 5, 4, 2, 4, 6, 3, 2, 22, 55, 32, 3, 3, 2, 4, 3};
        int temp;
        for (int i = 0; i < number.length; i++) {
            for (int j = i; j < number.length; j++) {
                if (number[i] < number[j]) {
                    temp = number[i];
                    number[i] = number[j];
                    number[j] = temp;
                }
            }
        }

        String s = "";
        for (int i : number) {
            if (!s.equals(""))
                s = s + ",";
            s += i + "";
        }
        print(s);
    }

    private void setTest() {
        Set<Integer> set = new HashSet<>();
        while (set.size() < 10) {
            int number = (int) (Math.random() * 30 + 1);
            print(number);
            set.add(number);
        }
        print(set.toString());
    }


}