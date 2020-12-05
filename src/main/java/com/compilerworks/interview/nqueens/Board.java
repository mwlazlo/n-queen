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

        if (isSquareCovered(row, col)) {
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
     * Print out a nice ACII art chess board.
     *
     * @return a nice ACII art chess board.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // row header
        sb.append("  ");
        for (int col = 0; col < boardSize; col++) {
            sb.append(col).append(" ");
        }
        sb.append("\n");

        for (int row = 0; row < boardSize; row++) {
            // column header
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
