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

    // array representing board. Queen's attack square is represented by the value of the int.
    // Each time a queen attacks a square, we increment that square by 1.
    // This allows us to more easily remove a queen from the board compared to a boolean array.
    private final int[][] board;

    // keep a tally of how many queens on the board
    private int queensPlaced = 0;

    /**
     * Construct a board of size boardSize
     *
     * @param boardSize size of board
     */
    public Board(int boardSize) {
        this.boardSize = boardSize;
        board = new int[boardSize][boardSize];
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
        board = new int[boardSize][boardSize];
        for (int row = 0; row < boardSize; row++) {
            System.arraycopy(b.board[row], 0, board[row], 0, boardSize);
        }
        queens = new int[boardSize];
        System.arraycopy(b.queens, 0, queens, 0, boardSize);
    }

    /**
     * Attempt to place a Queen at coordinate.
     *
     * @param row the row coord
     * @param col the col coord
     * @return true if queen could be placed on the board.
     */
    public boolean placeQueen(int row, int col) {

        if (isSquareCovered(row, col) || placeFormsRow(row, col)) {
            return false;
        }

        board[row][col]++;
        queens[row] = col;
        queensPlaced++;

        traverseXYSpace(row, col, this::squareAdd);
        traverseDiagonalSpace(row, col, this::squareAdd);

        return true;
    }

    /**
     * Remove Queen from coordinate.
     *
     * @param row the row coord
     * @param col the col coord
     */
    public void removeQueen(int row, int col) throws SanityCheckException {

        if (board[row][col] != 1) {
            log.error("Sanity check failed: removeQueen({}, {}) = {}",
                    row, col, board[row][col]);
            log.error("\n{}", this.toString());
            throw new SanityCheckException("sanity check failed");
        }

        board[row][col]--;
        queens[row] = -1;
        queensPlaced--;

        traverseXYSpace(row, col, this::squareSub);
        traverseDiagonalSpace(row, col, this::squareSub);
    }

    /**
     * Given a row/col coordinate, add or remove presence from the diagonal vectors covered by the queen from that coordinate.
     *
     * @param row the row coord
     * @param col the col coord
     */
    private void traverseDiagonalSpace(int row, int col, SquareOperation op) {

        // up/left
        for (int colUp = col - 1, rowLeft = row - 1; colUp >= 0 && rowLeft >= 0; colUp--, rowLeft--) {
            op.operate(rowLeft, colUp);
        }

        // up/right
        for (int colUp = col - 1, rowRight = row + 1; colUp >= 0 && rowRight < boardSize; colUp--, rowRight++) {
            op.operate(rowRight, colUp);
        }

        // down/left
        for (int colDown = col + 1, rowLeft = row - 1; colDown < boardSize && rowLeft >= 0; colDown++, rowLeft--) {
            op.operate(rowLeft, colDown);
        }

        // down/right
        for (int colDown = col + 1, rowRight = row + 1; colDown < boardSize && rowRight < boardSize; colDown++, rowRight++) {
            op.operate(rowRight, colDown);
        }
    }

    /**
     * Given a row/col coordinate, occupy the perpendicular(?) vectors covered by the queen from that coordinate.
     *
     * @param row the row coord
     * @param col the col coord
     */
    private void traverseXYSpace(int row, int col, SquareOperation op) {
        for (int i = 0; i < boardSize; i++) {
            // make sure to not +/- the queen's square again
            if (col != i) {
                op.operate(row, i);
            }
            if (row != i) {
                op.operate(i, col);
            }
        }
    }

    /**
     * Used as lambda for decrement a square
     *
     * @param row the row coord
     * @param col the col coord
     */
    private void squareSub(int row, int col) {
        board[row][col]--;
    }

    /**
     * Used as lambda for incrementing a square
     *
     * @param row the row coord
     * @param col the col coord
     */
    private void squareAdd(int row, int col) {
        board[row][col]++;
    }

    /**
     * Given a coordinate, check that 2 or more other queens already on the board create a vector
     *
     * @param row the row coord
     * @param col the col coord
     * @return true if this moves creates a vector with 2 other queens
     */
    private boolean placeFormsRow(int row, int col) {

        if (queensPlaced < 2) {
            return false;
        }

        // determine if any 3 points make a vector by calculating the area of the triangle they form.
        for (int q1Idx = 0; q1Idx < boardSize; q1Idx++) {
            if (queens[q1Idx] == -1) {
                continue;
            }

            for (int q2Idx = q1Idx + 1; q2Idx < boardSize; q2Idx++) {
                if (queens[q2Idx] == -1) {
                    continue;
                }

                // alias points 1 & 2
                int row1 = q1Idx;
                int col1 = queens[q1Idx];
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
     * @param x1 row1 coord
     * @param y1 col1 coord
     * @param x2 row2 coord
     * @param y2 col2 coord
     * @param x3 row3 coord
     * @param y3 col3 coord
     * @return true if slope of each sets of points matches
     */
    private boolean collinear(int x1, int y1, int x2, int y2, int x3, int y3) {
        return (y1 - y2) * (x1 - x3) == (y1 - y3) * (x1 - x2);
    }

    /**
     * Checks if square at row/col is covered by another queen
     *
     * @param row the row coord
     * @param col the col coord
     * @return true if covered, false otherwise
     */
    private boolean isSquareCovered(int row, int col) {
        return board[row][col] > 0;
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
                    sb.append(board[row][col] > 0 ? "* " : ". ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * queensPlaced getter
     *
     * @return count of queens on board
     */
    public int getQueensPlaced() {
        return queensPlaced;
    }

    /**
     * interface to perform some operation on a board square (+/-)
     */
    private interface SquareOperation {
        void operate(int row, int col);
    }
}
