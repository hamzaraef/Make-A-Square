package solver;

import java.util.ArrayList;
import java.util.List;
import model.Board;
import model.Piece;

public class BacktrackingSolver {

    private final Piece[] pieces;
    private final Piece[][] rotations;
    private final List<int[][]> solutions = new ArrayList<>();

    public BacktrackingSolver(List<Piece> pieces) {

        if (pieces == null) {
            throw new IllegalArgumentException("Invalid piece list.");
        }

        this.pieces = new Piece[pieces.size()];
        this.rotations = new Piece[pieces.size()][];

        for (int i = 0; i < pieces.size(); i++) {

            Piece piece = pieces.get(i);

            if (piece == null) {
                throw new IllegalArgumentException("Invalid piece.");
            }

            for (int j = 0; j < i; j++) {
                if (this.pieces[j].getId() == piece.getId()) {
                    throw new IllegalArgumentException("Duplicate piece ID.");
                }
            }

            this.pieces[i] = piece;
            rotations[i] = piece.getUniqueRotations();
        }
    }

    public List<int[][]> solve() {

        solutions.clear();

        long totalArea = 0;

        for (int i = 0; i < pieces.length; i++) {
            totalArea += pieces[i].getOccupiedCellCount();
        }

        if (totalArea == Board.SIZE * Board.SIZE) {
            search(new Board(), 0);
        }

        return getSolutions();
    }

    private void search(Board board, int pieceIndex) {

        if (pieceIndex == pieces.length) {

            if (board.isFull()) {
                solutions.add(board.getState());
            }

            return;
        }

        for (int i = 0; i < rotations[pieceIndex].length; i++) {

            Piece rotation = rotations[pieceIndex][i];

            for (int row = 0;
                    row <= Board.SIZE - rotation.getRows(); row++) {

                for (int column = 0;
                        column <= Board.SIZE - rotation.getColumns(); column++) {

                    if (board.canPlace(rotation, row, column)) {

                        board.placePiece(rotation, row, column);

                        search(board, pieceIndex + 1);

                        board.removePiece(rotation);
                    }
                }
            }
        }
    }

    public List<int[][]> getSolutions() {

        List<int[][]> copies = new ArrayList<>();

        for (int i = 0; i < solutions.size(); i++) {

            int[][] solution = solutions.get(i);
            int[][] copy = new int[Board.SIZE][Board.SIZE];

            for (int row = 0; row < Board.SIZE; row++) {
                for (int column = 0; column < Board.SIZE; column++) {
                    copy[row][column] = solution[row][column];
                }
            }

            copies.add(copy);
        }

        return copies;
    }
}
