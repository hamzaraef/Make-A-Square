package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import model.Piece;

public class InputFileReader {
    public static List<Piece> readPieces(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String firstLine = requiredLine(reader, "the number of pieces");
            if (firstLine.startsWith("\uFEFF")) {
                firstLine = firstLine.substring(1);
            }
            int count = positiveInteger(firstLine, "Number of pieces");
            if (count != 4 && count != 5) {
                throw new IllegalArgumentException("The file must contain 4 or 5 pieces.");
            }
            List<Piece> pieces = new ArrayList<>();
            for (int id = 1; id <= count; id++) {
                String[] dimensions = requiredLine(reader, "dimensions for piece " + id)
                        .split("\\s+");
                if (dimensions.length != 2) {
                    throw new IllegalArgumentException("Piece " + id
                            + ": expected dimensions as rows columns.");
                }
                int rows = positiveInteger(dimensions[0], "Rows for piece " + id);
                int columns = positiveInteger(dimensions[1], "Columns for piece " + id);
                List<String> shapeRows = new ArrayList<>();
                for (int row = 0; row < rows; row++) {
                    String line = requiredLine(reader, "row " + (row + 1) + " of piece " + id);
                    if (line.length() != columns || !line.matches("[01]+")) {
                        throw new IllegalArgumentException("Piece " + id + ", row " + (row + 1)
                                + ": expected exactly " + columns + " binary digits (0 or 1).");
                    }
                    shapeRows.add(line);
                }
                int[][] shape = new int[rows][columns];
                for (int row = 0; row < rows; row++) {
                    for (int column = 0; column < columns; column++) {
                        shape[row][column] = shapeRows.get(row).charAt(column) - '0';
                    }
                }
                try {
                    pieces.add(new Piece(id, shape));
                } catch (IllegalArgumentException exception) {
                    throw new IllegalArgumentException("Piece " + id + ": " + exception.getMessage());
                }
            }
            String remaining;
            while ((remaining = reader.readLine()) != null) {
                if (!remaining.trim().isEmpty()) {
                    throw new IllegalArgumentException("Unexpected content after the last piece.");
                }
            }
            return pieces;
        }
    }

    private static String requiredLine(BufferedReader reader, String description) throws IOException {
        String line = reader.readLine();
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing " + description + ".");
        }
        return line.trim();
    }

    private static int positiveInteger(String text, String description) {
        try {
            int value = Integer.parseInt(text);
            if (value > 0) {
                return value;
            }
        } catch (NumberFormatException exception) {
        }
        throw new IllegalArgumentException(description + " must be a positive integer.");
    }
}
