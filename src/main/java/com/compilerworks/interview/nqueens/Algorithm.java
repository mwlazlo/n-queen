package com.compilerworks.interview.nqueens;

import java.util.ArrayList;
import java.util.List;

class Algorithm {
    private final int boardSize;

    private Algorithm(int boardSize) {
        this.boardSize = boardSize;
    }

    /**
     * @param n   size of board
     * @param col starting column on row 0
     * @return Board with final solution, if any
     */
    public static List<Board> search(int n, int col) throws SanityCheckException {
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
     * @param row   the starting row
     * @return Boards with final solution, if any
     */
    private List<Board> search(Board board, int row) throws SanityCheckException {
        List<Board> rv = new ArrayList<>();

        for (int col = 0; col < boardSize; col++) {

            if (board.placeQueen(row, col)) {

                if (board.getQueensPlaced() == boardSize) {
                    rv.add(new Board(board));
                } else {
                    rv.addAll(search(board, row + 1));
                }

                board.removeQueen(row, col);
            }
        }
        return rv;
    }
}
