package com.compilerworks.interview.nqueens;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * NQueens entry point
 * <p>
 * References:
 * 1. https://en.wikipedia.org/wiki/Eight_queens_puzzle
 * 2. https://www.cs.utexas.edu/users/EWD/transcriptions/EWD03xx/EWD316.9.html
 */
public class NQueens {
    private static final Logger log = LogManager.getLogger(NQueens.class);

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: NQueens rows");
            System.exit(1);
        }

        StopWatch stopWatch = new StopWatch();

        try {
            int n = Integer.parseInt(args[0]);
            List<Board> result = new ArrayList<>();

            stopWatch.start();

            // Since there can be at most 1 queen per row, we simply start a number of threads
            // from each column of first row.
            for (int i = 0; i < n; i++) {
                result.addAll(Algorithm.search(n, i));
            }

            System.out.printf("Results (%d):\n\n", result.size());
            for(Board b : result) {
                System.out.println(b);
            }

        } catch (NumberFormatException e) {
            log.error("invalid \"rows\" parameter, please enter an integer");
            System.exit(1);

        } catch (SanityCheckException e) {
            e.printStackTrace();
            System.exit(1);

        } finally {
            if (stopWatch.isStarted()) {
                stopWatch.stop();
                log.info("completed in {}", stopWatch.toString());
            }
        }
    }
}
