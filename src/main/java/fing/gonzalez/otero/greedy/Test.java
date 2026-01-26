package fing.gonzalez.otero.greedy;

import fing.gonzalez.otero.utils.ExportCSV;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

class Test {
	public static void main(String[] args) {
		FingGreedy problem = new FingGreedy(
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
		PermutationSolution<Integer> sol = problem.costSolution();
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
		sol = problem.timeSolution();
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
		rowTime.add("Greedy Time");
		rowTime.add(String.valueOf(sol.getObjective(0)));
		rowTime.add(String.valueOf(sol.getObjective(1)));
		rows.add(rowTime);
		// tiempo de inicio
		startTime = System.currentTimeMillis();
		sol = problem.compromiseSolution();
		// tiempo de fin
	    endTime = System.currentTimeMillis();
	    durationMillis = endTime - startTime; // tiempo en milisegundos
	    durationSeconds = durationMillis / 1000.0; // tiempo en segundos
		System.out.println("Soluciones finales, Greedy Compromise:");
		System.out.println(sol.getVariables());
		System.out.println("Costo = " + sol.getObjective(0));
		System.out.println("Tiempo = " + sol.getObjective(1));
		System.out.println("-------------------");
		System.out.println("Tiempo de ejecución: " + durationSeconds + " segundos");
		System.out.println("-------------------");
		rowBoth.add("Greedy Compromise");
		rowBoth.add(String.valueOf(sol.getObjective(0)));
		rowBoth.add(String.valueOf(sol.getObjective(1)));
		rows.add(rowBoth);
		ExportCSV.export("greedys", headers, rows);
		/*
		System.out.println("Distances  es : " + problem.distancesRowLength() + " x " + problem.distancesColumnLength());
		System.out.println("Distances[0]  : " + Arrays.toString(problem.distancesRow(1)));
		System.out.println("Times      es : " + problem.timesRowLength() + " x " + problem.timesColumnLength());
		System.out.println("Times[0]      : " + Arrays.toString(problem.timesRow(1)));
		System.out.println("Pendientes es : " + problem.pendingLength());
		System.out.println("-------------------");
		for (int i = 0; i < problem.getAssignments().size(); i++) {
			System.out.println("Vehiculo " + i + " -> " + problem.getAssignments().get(i));
		}
		*/
	}
}