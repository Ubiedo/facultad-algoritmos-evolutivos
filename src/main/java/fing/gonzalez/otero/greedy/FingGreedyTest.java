package fing.gonzalez.otero.greedy;

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
		System.out.println("Distances  es : " + problem.distancesRowLength() + " x " + problem.distancesColumnLength());
		System.out.println("Times      es : " + problem.timesRowLength() + " x " + problem.timesColumnLength());
		System.out.println("Pendientes es : " + problem.pendingLength());
	}
}