import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.rules.Stopwatch;

import java.util.Arrays;
import java.util.Random;

public class TestArrayCopy {

    public static void main(String[] args) {

        Random random = new Random();
        final int LENGTH = 4096 * 3;
        StopWatch stopwatch = new StopWatch();
        int[][] arr, copy;

        // stream
        arr = generateRandomArray(LENGTH);
        stopwatch.start();
        copy = Arrays.stream(arr).map(int[]::clone).toArray(int[][]::new);
        stopwatch.stop();
        System.out.printf("stream copy in %s\n", stopwatch.toString());

        // arraycopy
        arr = generateRandomArray(LENGTH);
        stopwatch.reset();
        stopwatch.start();
        copy = new int[LENGTH][LENGTH];
        System.arraycopy(arr, 0, copy, 0, LENGTH);
        stopwatch.stop();
        System.out.printf("arraycopy copy in %s\n", stopwatch.toString());

        // hand copy
        arr = generateRandomArray(LENGTH);
        stopwatch.reset();
        stopwatch.start();
        copy = new int[LENGTH][LENGTH];
        for (int x = 0; x < LENGTH; x++) {
            for (int y = 0; y < LENGTH; y++) {
                copy[x][y] = arr[x][y];
            }
        }
        stopwatch.stop();
        System.out.printf("hand copy in %s\n", stopwatch.toString());
    }

    private static int[][] generateRandomArray(int n) {
        int[][] rv = new int[n][n];
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                rv[x][y] = RandomUtils.nextInt();
            }
        }
        return rv;
    }

}

