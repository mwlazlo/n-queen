package com.compilerworks.interview.nqueens;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Board representation
 * <p>
 * Shortcut: each row will contain one queen, so track only column position of queens.
 */
public class Board {

    private static final Logger log = LogManager.getLogger(Board.class);

    // size of board
    private final int boardSize;

    // column position of queens, in row order
    private final int[] queens;

    // keep a tally of how many queens on the board
    private int queensPlaced = 0;

    // row indexed (row, col) 2d array
    // NB: potential space optimisation, as described by Djikstra, use integer instead of boolean and
    // increment/decrement on place/unplace queen. In this version we simply discard the unsolved board.
    private final boolean[][] board;

    /**
     * Construct a board of size boardSize
     *
     * @param boardSize size of board
     */
    public Board(int boardSize) {
        this.boardSize = boardSize;
        board = new boolean[boardSize][boardSize];
        queens = new int[boardSize];

        // initialise queens with invalid position
        for (int i = 0; i < boardSize; i++) {
            queens[i] = -1;
        }
    }

    /**
     * Copy ctor
     *
     * @param b Board to clone
     */
    public Board(Board b) {
        boardSize = b.boardSize;
        queensPlaced = b.queensPlaced;
        board = new boolean[boardSize][boardSize];
        for (int row = 0; row < boardSize; row++) {
            System.arraycopy(b.board[row], 0, board[row], 0, boardSize);
        }
        queens = new int[boardSize];
        System.arraycopy(b.queens, 0, queens, 0, boardSize);
    }


    /**
     * Attempt to place a Queen at coordinate.
     *
     * @param row
     * @param col
     * @return true if queen could be placed on the board.
     */
    public boolean placeQueen(int row, int col) {

        if (isSquareCovered(row, col) || placeFormsRow(row, col)) {
            return false;
        }

        board[row][col] = true;
        queens[row] = col;
        queensPlaced++;

        occupyXYSpace(row, col);
        occupyDiagonalSpace(row, col);

        return true;
    }

    /**
     * Given a row/col coordinate, occupy the diagonal vectors covered by the queen from that coordinate.
     *
     * @param row
     * @param col
     */
    private void occupyDiagonalSpace(int row, int col) {

        // up/left
        for (int colUp = col - 1, rowLeft = row - 1; colUp >= 0 && rowLeft >= 0; colUp--, rowLeft--) {
            board[rowLeft][colUp] = true;
        }

        // up/right
        for (int colUp = col - 1, rowRight = row + 1; colUp >= 0 && rowRight < boardSize; colUp--, rowRight++) {
            board[rowRight][colUp] = true;
        }

        // down/left
        for (int colDown = col + 1, rowLeft = row - 1; colDown < boardSize && rowLeft >= 0; colDown++, rowLeft--) {
            board[rowLeft][colDown] = true;
        }

        // down/right
        for (int colDown = col + 1, rowRight = row + 1; colDown < boardSize && rowRight < boardSize; colDown++, rowRight++) {
            board[rowRight][colDown] = true;
        }
    }

    /**
     * Given a row/col coordinate, occupy the perpendicular vectors covered by the queen from that coordinate.
     *
     * @param row
     * @param col
     */
    private void occupyXYSpace(int row, int col) {
        for (int i = 0; i < boardSize; i++) {
            board[row][i] = true;
            board[i][col] = true;
        }
    }

    /**
     * Given a coordinate, check that 2 or more other queens already on the board create a vector
     *
     * @param row
     * @param col
     * @return
     */
    private boolean placeFormsRow(int row, int col) {

        if (queensPlaced < 2) {
            return false;
        }

        // determine if any 3 points make a vector by calculating the area of the triangle they form.
        // generate sets of points
        for (int q1Idx = 0; q1Idx < boardSize; q1Idx++) {
            if (queens[q1Idx] == -1) {
                continue;
            }

            // alias point 1
            int row1 = q1Idx;
            int col1 = queens[q1Idx];

            for (int q2Idx = q1Idx + 1; q2Idx < boardSize; q2Idx++) {
                if (queens[q2Idx] == -1) {
                    continue;
                }


                // alias point 2
                int row2 = q2Idx;
                int col2 = queens[q2Idx];

                if (collinear(row, col, row1, col1, row2, col2)) {
                    log.debug("row detected placing queen at {}/{}:\n" +
                                    "{}/{} <-> {}/{} <-> {}/{}\n{}", row, col,
                            row2, col2, row1, col1, row, col, this.toString());
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Someone much smarter than me came up with this solution: https://stackoverflow.com/a/3813723/9825752
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @return true if slope of each sets of points matches
     */
    private boolean collinear(int x1, int y1, int x2, int y2, int x3, int y3) {
        return (y1 - y2) * (x1 - x3) == (y1 - y3) * (x1 - x2);
    }

    /**
     * Checks if square at row/col is covered by another queen
     *
     * @param row
     * @param col
     * @return true if covered, false otherwise
     */
    private boolean isSquareCovered(int row, int col) {
        return board[row][col];
    }

    /**
     * Print out a nice ASCII art chess board.
     *
     * @return a nice ASCII art chess board.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // column header
        sb.append("  ");
        for (int col = 0; col < boardSize; col++) {
            sb.append(col).append(" ");
        }
        sb.append("\n");

        for (int row = 0; row < boardSize; row++) {

            // row header
            sb.append(row).append(" ");

            // the actual row
            for (int col = 0; col < boardSize; col++) {
                if (queens[row] == col) {
                    sb.append("Q ");
                } else {
                    sb.append(board[row][col] ? "* " : ". ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * queensPlaced getter
     *
     * @return
     */
    public int getQueensPlaced() {
        return queensPlaced;
    }
}
