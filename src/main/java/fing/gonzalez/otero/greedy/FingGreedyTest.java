package fing.gonzalez.otero.greedy;

//import java.util.Arrays;

import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

class FingGreedyTest {
	public static void main(String[] args) {
		FingGreedy problem = new FingGreedy(
				195 /* variables,  V + |R| = ?   */, 
				43  /* vehicles,  V = 43|R|/152 */
		);
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