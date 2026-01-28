package fing.gonzalez.otero.greedy;

import fing.gonzalez.otero.utils.ExportCSV;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

class Main {
	public static void main(String[] args) {
	    Cost greedyCost = new Cost(
				195 /* variables,  V + |R| = ?   */, 
				43  /* vehicles,  V = 43|R|/152 */
		);
        Time greedyTime = new Time(
                195 /* variables,  V + |R| = ?   */, 
                43  /* vehicles,  V = 43|R|/152 */
        );
		// para exportar resultados
		List<String> headers = new ArrayList<String>();
		headers.add("algoritmo");
		headers.add("costo");
		headers.add("tiempo");
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> rowCost = new ArrayList<String>();
		List<String> rowTime = new ArrayList<String>();
		List<String> rowBoth = new ArrayList<String>();
		// tiempo de inicio
		long startTime = System.currentTimeMillis();
		PermutationSolution<Integer> sol = greedyCost.solution();
		// tiempo de fin
	    long endTime = System.currentTimeMillis();
	    long durationMillis = endTime - startTime; // tiempo en milisegundos
	    double durationSeconds = durationMillis / 1000.0; // tiempo en segundos
		System.out.println("Soluciones finales, Greedy Cost:");
		System.out.println(sol.getVariables());
		System.out.println("Costo = " + sol.getObjective(0));
		System.out.println("Tiempo = " + sol.getObjective(1));
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
        System.out.println("Soluciones finales, Greedy Time:");
        System.out.println(sol.getVariables());
        System.out.println("Costo = " + sol.getObjective(0));
        System.out.println("Tiempo = " + sol.getObjective(1));
        System.out.println("-------------------");
        System.out.println("Tiempo de ejecución: " + durationSeconds + " segundos");
        System.out.println("-------------------");
        rowCost.add("Greedy Time");
        rowCost.add(String.valueOf(sol.getObjective(0)));
        rowCost.add(String.valueOf(sol.getObjective(1)));
        rows.add(rowCost);
		ExportCSV.export("greedys", headers, rows);
	}
}