package model;

public class Piece {

    public enum PieceType {
        I, O, T, L, J, S, Z
    }

    private final int id;
    private final int[][] shape;

    public Piece(int id, PieceType type) {
        this(id, standardShape(type));
    }

    public Piece(int id, int[][] shape) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid piece ID.");
        }

        this.id = id;
        this.shape = validateAndTrim(shape);
    }

    private static int[][] standardShape(PieceType type) {

        if (type == null) {
            throw new IllegalArgumentException("Invalid piece type.");
        }

        switch (type) {
            case I:
                return new int[][] {{1, 1, 1, 1}};

            case O:
                return new int[][] {{1, 1}, {1, 1}};

            case T:
                return new int[][] {{1, 1, 1}, {0, 1, 0}};

            case L:
                return new int[][] {{1, 0}, {1, 0}, {1, 1}};

            case J:
                return new int[][] {{0, 1}, {0, 1}, {1, 1}};

            case S:
                return new int[][] {{0, 1, 1}, {1, 1, 0}};

            case Z:
                return new int[][] {{1, 1, 0}, {0, 1, 1}};

            default:
                throw new IllegalArgumentException("Invalid piece type.");
        }
    }

    private static int[][] validateAndTrim(int[][] input) {

        if (input == null || input.length == 0
                || input[0] == null || input[0].length == 0) {
            throw new IllegalArgumentException("Empty piece shape.");
        }

        int rows = input.length;
        int columns = input[0].length;

        int top = rows;
        int bottom = -1;
        int left = columns;
        int right = -1;

        for (int i = 0; i < rows; i++) {

            if (input[i] == null || input[i].length != columns) {
                throw new IllegalArgumentException("Invalid matrix dimensions.");
            }

            for (int j = 0; j < columns; j++) {

                if (input[i][j] != 0 && input[i][j] != 1) {
                    throw new IllegalArgumentException("Only 0 and 1 are allowed.");
                }

                if (input[i][j] == 1) {
                    top = Math.min(top, i);
                    bottom = Math.max(bottom, i);
                    left = Math.min(left, j);
                    right = Math.max(right, j);
                }
            }
        }

        if (bottom == -1) {
            throw new IllegalArgumentException("Piece has no occupied cells.");
        }

        int[][] result = new int[bottom - top + 1][right - left + 1];

        for (int i = top; i <= bottom; i++) {
            for (int j = left; j <= right; j++) {
                result[i - top][j - left] = input[i][j];
            }
        }
        return result;
    }

    public int getId() {
        return id;
    }

    public int getRows() {
        return shape.length;
    }

    public int getColumns() {
        return shape[0].length;
    }

    public int[][] getShape() {

        int[][] copy = new int[getRows()][getColumns()];

        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                copy[i][j] = shape[i][j];
            }
        }

        return copy;
    }

    public boolean isOccupied(int row, int column) {
        return shape[row][column] == 1;
    }

    public int getOccupiedCellCount() {

        int count = 0;

        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                count += shape[i][j];
            }
        }

        return count;
    }

    public Piece rotateClockwise() {

        int[][] rotated = new int[getColumns()][getRows()];

        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                rotated[j][getRows() - 1 - i] = shape[i][j];
            }
        }

        return new Piece(id, rotated);
    }

    private boolean sameShape(Piece other) {

        if (getRows() != other.getRows()
                || getColumns() != other.getColumns()) {
            return false;
        }

        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                if (shape[i][j] != other.shape[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public Piece[] getUniqueRotations() {

        Piece[] rotations = new Piece[4];
        Piece current = this;
        int count = 0;

        for (int i = 0; i < 4; i++) {

            boolean duplicate = false;

            for (int j = 0; j < count; j++) {
                if (current.sameShape(rotations[j])) {
                    duplicate = true;
                    break;
                }
            }

            if (!duplicate) {
                rotations[count] = current;
                count++;
            }

            current = current.rotateClockwise();
        }
        Piece[] result = new Piece[count];

        for (int i = 0; i < count; i++) {
            result[i] = rotations[i];
        }

        return result;
    }
}