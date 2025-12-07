package fing.gonzalez.otero;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.operator.selection.SelectionOperator;

import java.util.List;

/* ChatGPT */
public class FingAlgorithm {
	public static void main(String[] args) {
		// 1. Instanciar el problema
		FingProblem problem = new FingProblem(
				0/* variables */, 
				0/* vehicles */
		);
		// 2. Instanciar crossover y mutación
		FingCrossover crossover = new FingCrossover(0.9);
		FingMutation mutation = new FingMutation(0.2);
		// 3. Instanciar selección
		SelectionOperator<List<PermutationSolution<Integer>>,
				PermutationSolution<Integer>> selection =
				new BinaryTournamentSelection<>(
						new RankingAndCrowdingDistanceComparator<>()
				);
		// 4. Crear el algoritmo NSGA-II
		Algorithm<List<PermutationSolution<Integer>>> algorithm =
				new NSGAIIBuilder<>(problem, crossover, mutation)
						.setSelectionOperator(selection)
						.setPopulationSize(100)
						.setMaxEvaluations(20000)
						.build();
		// 5. Ejecutar
		algorithm.run();
		// 6. Obtener resultados
		List<PermutationSolution<Integer>> population = algorithm.getResult();
		// 7. Mostrar/guardar resultados
		System.out.println("Soluciones finales:");
		for (PermutationSolution<Integer> sol : population) {
			System.out.println(sol.getVariables());
			System.out.println("Costo = " + sol.getObjective(0));
			System.out.println("Tiempo = " + sol.getObjective(1));
			System.out.println("-------------------");
		}
	}
}
