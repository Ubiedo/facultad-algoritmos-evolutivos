package fing.gonzalez.otero.greedy;

import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

class Test {
	public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            Time greedyTime = new Time(
                    195 /* variables,  V + |R| = ?   */, 
                    43  /* vehicles,  V = 43|R|/152 */
            );
            // tiempo de inicio
            long startTime = System.currentTimeMillis();
            PermutationSolution<Integer> sol = greedyTime.solution();
            // tiempo de fin
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime; // tiempo en milisegundos
            double durationSeconds = durationMillis / 1000.0; // tiempo en segundos
            System.out.println("-------------------");
            System.out.println("Resultados, Greedy Time ( "+ i +" ):");
            //System.out.println(sol.getVariables());
            System.out.println("Costo = " + sol.getObjective(0));
            System.out.println("Tiempo = " + sol.getObjective(1));
            System.out.println("Tiempo de ejecución: " + durationSeconds + " segundos");
            System.out.println("-------------------");
        }
	}
}