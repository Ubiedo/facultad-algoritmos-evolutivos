package fing.gonzalez.otero.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

public class ExportCSV {

	public static void export(String fileName, List<String> headers, List<List<String>> rows) {
		Path path = Path.of("src/main/resources/out", fileName + ".csv");
		try {
			Files.createDirectories(path.getParent());
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toFile()))) {
				// encabezado
				bw.write(String.join(",", headers));
				bw.newLine();
				// filas
				for (List<String> row : rows) {
					bw.write(String.join(",", row));
					bw.newLine();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Error escribiendo CSV", e);
		}
	}
	
	public static void exportPermutation(String fileName, List<String> headers, List<PermutationSolution<Integer>> population) {
		Path path = Path.of("src/main/resources/out", fileName + ".csv");
		try {
			Files.createDirectories(path.getParent());
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toFile()))) {
				// encabezado
				bw.write(String.join(",", headers));
				bw.newLine();
				// filas
				for (PermutationSolution<Integer> solution : population) {
						bw.write((int)solution.getObjective(0) + ", " + (int)solution.getObjective(1));
						bw.newLine();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Error escribiendo CSV", e);
		}
	}
}
