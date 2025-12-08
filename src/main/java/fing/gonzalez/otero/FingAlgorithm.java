package fing.gonzalez.otero;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;

import java.util.List;

public class FingAlgorithm {
	public static void main(String[] args) {
		FingProblem problem = new FingProblem(
				0/* variables, |R| = 0         */, 
				43/* vehicles,  V  = 43|R|/154 */
		);
		FingCrossover crossover = new FingCrossover(0.9);
		FingMutation mutation = new FingMutation(0.2);
		SelectionOperator<List<PermutationSolution<Integer>>,
				PermutationSolution<Integer>> selection =
				new BinaryTournamentSelection<>(
						new RankingAndCrowdingDistanceComparator<>()
				);
		Algorithm<List<PermutationSolution<Integer>>> algorithm =
				new NSGAIIBuilder<>(problem, crossover, mutation, 100)
						.setSelectionOperator(selection)
						.setMaxEvaluations(20000)
						.build();

		algorithm.run();
		List<PermutationSolution<Integer>> population = algorithm.getResult();
		System.out.println("Soluciones finales:");
		for (PermutationSolution<Integer> sol : population) {
			System.out.println(sol.getVariables());
			System.out.println("Costo = " + sol.getObjective(0));
			System.out.println("Tiempo = " + sol.getObjective(1));
			System.out.println("-------------------");
		}
	}
}
