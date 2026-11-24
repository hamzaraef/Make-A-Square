package model;

public class Board {
    public static final int SIZE = 4;
    private final int[][] cells = new int[SIZE][SIZE];

    public boolean canPlace(Piece piece, int row, int column) {
        if (piece == null || row < 0 || column < 0
                || row > SIZE - piece.getRows()
                || column > SIZE - piece.getColumns()) {
            return false;
        }
        for (int[] boardRow : cells) {
            for (int cell : boardRow) {
                if (cell == piece.getId()) {
                    return false;
                }
            }
        }
        for (int r = 0; r < piece.getRows(); r++) {
            for (int c = 0; c < piece.getColumns(); c++) {
                if (piece.isOccupied(r, c) && cells[row + r][column + c] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void placePiece(Piece piece, int row, int column) {
        if (!canPlace(piece, row, column)) {
            throw new IllegalArgumentException("The piece cannot be placed at this position.");
        }
        for (int r = 0; r < piece.getRows(); r++) {
            for (int c = 0; c < piece.getColumns(); c++) {
                if (piece.isOccupied(r, c)) {
                    cells[row + r][column + c] = piece.getId();
                }
            }
        }
    }

    public void removePiece(Piece piece) {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (cells[row][column] == piece.getId()) {
                    cells[row][column] = 0;
                }
            }
        }
    }

    public boolean isFull() {
        for (int[] row : cells) {
            for (int cell : row) {
                if (cell == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] getState() {
        int[][] copy = new int[SIZE][];
        for (int row = 0; row < SIZE; row++) {
            copy[row] = cells[row].clone();
        }
        return copy;
    }

    public void display() {
        display(cells);
    }

    public static void display(int[][] state) {
        for (int[] row : state) {
            for (int cell : row) {
                System.out.print(cell);
            }
            System.out.println();
        }
    }
}
