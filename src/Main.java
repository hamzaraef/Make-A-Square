import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import model.Board;
import model.Piece;
import solver.BacktrackingSolver;
import utils.InputFileReader;

public class Main {
    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Usage: java -jar dist/Make-A-Square.jar [input-file]");
            return;
        }
        try {
            Path input = args.length == 1 ? Paths.get(args[0]) : selectSample();
            if (input == null) {
                return;
            }
            List<Piece> pieces = InputFileReader.readPieces(input);
            int totalArea = 0;
            for (Piece piece : pieces) {
                totalArea += piece.getOccupiedCellCount();
            }
            System.out.println("Input: " + input);
            if (totalArea != Board.SIZE * Board.SIZE) {
                System.out.println("Total piece area is " + totalArea + "; a 4x4 board requires 16.");
            }
            List<int[][]> solutions = new BacktrackingSolver(pieces).solve();
            if (solutions.isEmpty()) {
                System.out.println("No solution possible");
            } else {
                for (int index = 0; index < solutions.size(); index++) {
                    System.out.println("Solution " + (index + 1) + ":");
                    Board.display(solutions.get(index));
                    System.out.println();
                }
            }
            System.out.println("Total solutions: " + solutions.size());
        } catch (IOException exception) {
            System.err.println("Could not read input file: " + exception.getMessage());
        } catch (IllegalArgumentException exception) {
            System.err.println("Invalid input: " + exception.getMessage());
        }
    }

    private static Path selectSample() throws IOException {
        Path directory = Paths.get("samples");
        if (!Files.isDirectory(directory)) {
            throw new IOException("samples folder not found. Run from the project root or provide a file path.");
        }
        List<Path> samples;
        try (Stream<Path> paths = Files.list(directory)) {
            samples = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .sorted().collect(Collectors.toList());
        }
        if (samples.isEmpty()) {
            throw new IOException("No sample input files found.");
        }
        System.out.println("Make-A-Square - select a sample:");
        for (int index = 0; index < samples.size(); index++) {
            System.out.println((index + 1) + ". " + samples.get(index).getFileName());
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter sample number (1-" + samples.size() + ", 0 to exit): ");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String answer = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(answer);
                if (choice == 0) {
                    return null;
                }
                if (choice >= 1 && choice <= samples.size()) {
                    return samples.get(choice - 1);
                }
            } catch (NumberFormatException exception) {
            }
            System.out.println("Enter a number from 0 to " + samples.size() + ".");
        }
    }
}
