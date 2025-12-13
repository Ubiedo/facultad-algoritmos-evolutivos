package fing.gonzalez.otero;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;

import java.util.List;

public class FingMain {
	public static void main(String[] args) {
		FingProblem problem = new FingProblem(
				195 /* variables,  V + |R| = ?   */, 
				43  /* vehicles,  V = 43|R|/152 */
		);
		FingCrossover crossover = new FingCrossover(0.9);
		FingMutation mutation = new FingMutation(0.2);
		SelectionOperator<List<PermutationSolution<Integer>>,
				PermutationSolution<Integer>> selection =
				new BinaryTournamentSelection<>(
						new RankingAndCrowdingDistanceComparator<>()
				);
		// tiempo de inicio
		long startTime = System.currentTimeMillis();
		Algorithm<List<PermutationSolution<Integer>>> algorithm =
				new NSGAIIBuilder<>(problem, crossover, mutation, 1000)
						.setSelectionOperator(selection)
						.setMaxEvaluations(10000000)
						.build();

		algorithm.run();
		// tiempo de fin
	    long endTime = System.currentTimeMillis();
	    long durationMillis = endTime - startTime; // tiempo en milisegundos
	    double durationSeconds = durationMillis / 1000.0; // tiempo en segundos
		List<PermutationSolution<Integer>> population = algorithm.getResult();
		System.out.println("Soluciones finales:");
		for (PermutationSolution<Integer> sol : population) {
			System.out.println(sol.getVariables());
			System.out.println("Costo = " + sol.getObjective(0));
			System.out.println("Tiempo = " + sol.getObjective(1));
			System.out.println("-------------------");
		}
		System.out.println("Tiempo de ejecución: " + durationSeconds + " segundos");
	}
}
