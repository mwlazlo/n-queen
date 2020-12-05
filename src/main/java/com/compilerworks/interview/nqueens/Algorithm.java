package com.compilerworks.interview.nqueens;

import java.util.ArrayList;
import java.util.List;

class Algorithm {
    private final int boardSize;

    private Algorithm(int boardSize) {
        this.boardSize = boardSize;
    }

    /**
     *
     * @param n size of board
     * @param col starting column on row 0
     * @return Board with final solution, if any
     */
    public static List<Board> search(int n, int col) {
        Algorithm algorithm = new Algorithm(n);
        Board b = new Board(n);

        // place first queen at designated column
        b.placeQueen(0, col);

        return algorithm.search(b, 1);
    }

    /**
     * Check each position in row for a solution. Recursively calls itself with next row
     *
     * @param board the board
     * @param row the row
     * @return Boards with final solution, if any
     */
    private List<Board> search(Board board, int row) {
        List<Board> rv = new ArrayList<>();

        for(int col = 0; col < boardSize; col++) {

            // clone board and try placing queen, discarding copy if it queen can't be placed...
            Board next = new Board(board);

            if(next.placeQueen(row, col)) {
                if (next.getQueensPlaced() == boardSize) {
                    rv.add(next);
                } else {
                    rv.addAll(search(next, row + 1));
                }
            }
        }
        return rv;
    }
}
