package fing.gonzalez.otero.greedy;

import fing.gonzalez.otero.utils.ExportCSV;
import fing.gonzalez.otero.utils.MatrixLoader;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

class Main {
	public static void main(String[] args) {
	    Cost greedyCost = new Cost(
				43  /* vehicles,  V = 43|R|/152 */,
                MatrixLoader.idsSet(3) /* ids a usar */
		);
        Time greedyTime = new Time(
                43  /* vehicles,  V = 43|R|/152 */,
                MatrixLoader.idsSet(3) /* ids a usar */
        );
		// para exportar resultados
		List<String> headers = new ArrayList<String>();
		headers.add("algoritmo");
		headers.add("costo");
		headers.add("tiempo");
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> rowCost = new ArrayList<String>();
		List<String> rowTime = new ArrayList<String>();
		// tiempo de inicio
		long startTime = System.currentTimeMillis();
		PermutationSolution<Integer> sol = greedyCost.solution();
		// tiempo de fin
	    long endTime = System.currentTimeMillis();
	    long durationMillis = endTime - startTime; // tiempo en milisegundos
	    double durationSeconds = durationMillis / 1000.0; // tiempo en segundos
		System.out.println("Soluciones finales, \u001B[33mGreedy Cost\u001B[0m:");
		System.out.println(sol.getVariables());
		System.out.println("\u001B[33mCosto = " + sol.getObjective(0) + "\u001B[0m");
		System.out.println("\u001B[33mTiempo = " + sol.getObjective(1) + "\u001B[0m");
		System.out.println("-------------------");
		System.out.println("Tiempo de ejecución: " + durationSeconds + " segundos");
		System.out.println("-------------------");
		rowCost.add("Greedy Cost");
		rowCost.add(String.valueOf(sol.getObjective(0)));
		rowCost.add(String.valueOf(sol.getObjective(1)));
		rows.add(rowCost);
        // tiempo de inicio
        startTime = System.currentTimeMillis();
        sol = greedyTime.solution();
        // tiempo de fin
        endTime = System.currentTimeMillis();
        durationMillis = endTime - startTime; // tiempo en milisegundos
        durationSeconds = durationMillis / 1000.0; // tiempo en segundos
        System.out.println("Soluciones finales, \u001B[33mGreedy Time\u001B[0m:");
        System.out.println(sol.getVariables());
        System.out.println("\u001B[33mCosto = " + sol.getObjective(0) + "\u001B[0m");
        System.out.println("\u001B[33mTiempo = " + sol.getObjective(1) + "\u001B[0m");
        System.out.println("-------------------");
        System.out.println("Tiempo de ejecución: " + durationSeconds + " segundos");
        System.out.println("-------------------");
        rowTime.add("Greedy Time");
        rowTime.add(String.valueOf(sol.getObjective(0)));
        rowTime.add(String.valueOf(sol.getObjective(1)));
        rows.add(rowTime);
		ExportCSV.export("greedys", headers, rows);
	}
}