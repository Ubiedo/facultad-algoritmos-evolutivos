package fing.gonzalez.otero.calibration;

import fing.gonzalez.otero.jmetal.FingCrossover;
import fing.gonzalez.otero.jmetal.FingMutation;
import fing.gonzalez.otero.jmetal.FingProblem;
import fing.gonzalez.otero.utils.ExportCSV;
import fing.gonzalez.otero.utils.MatrixLoader;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Iterations {
	private static PermutationSolution<Integer> minCostExtreme(
			List<PermutationSolution<Integer>> front) {
		double minCost = front.stream()
				.mapToDouble(s -> s.getObjective(0))
				.min()
				.orElse(Double.MAX_VALUE);
		return front.stream()
				.filter(s -> s.getObjective(0) == minCost)
				.max(Comparator.comparingDouble(s -> s.getObjective(1)))
				.orElse(null);
	}

	private static PermutationSolution<Integer> minTimeExtreme(
			List<PermutationSolution<Integer>> front) {
		double minTime = front.stream()
				.mapToDouble(s -> s.getObjective(1))
				.min()
				.orElse(Double.MAX_VALUE);
		return front.stream()
				.filter(s -> s.getObjective(1) == minTime)
				.max(Comparator.comparingDouble(s -> s.getObjective(0)))
				.orElse(null);
	}

	public static void main(String[] args) {
		FingProblem problem = new FingProblem(
				13,   // vehículos
                MatrixLoader.idsSet(1) /* ids a usar */
		);
		int populationSize = 100;
		int maxIterations = 10_000;
		int step = 10_000;
		int maxLimit = 500_000;
		double crossoverProbability = 0.9;
		double mutationProbability = 0.2;
		PermutationSolution<Integer> bestMinCost = null;
		PermutationSolution<Integer> bestMinTime = null;
		int noChangeCounter = 0;
		int maxNoChange = 10;
		List<String> headers = List.of(
				"generations",
				"best_min_cost",
				"time_at_best_min_cost",
				"best_min_time",
				"cost_at_best_min_time",
				"no_change_counter"
		);
		List<List<String>> records = new ArrayList<>();
		while (maxIterations <= maxLimit) {
			FingCrossover crossover = new FingCrossover(crossoverProbability);
			FingMutation mutation = new FingMutation(mutationProbability);
			SelectionOperator<List<PermutationSolution<Integer>>,
					PermutationSolution<Integer>> selection =
					new BinaryTournamentSelection<>(
							new RankingAndCrowdingDistanceComparator<>()
					);
			Algorithm<List<PermutationSolution<Integer>>> algorithm =
					new NSGAIIBuilder<>(problem, crossover, mutation, populationSize)
							.setSelectionOperator(selection)
							.setMaxEvaluations(maxIterations)
							.build();
			algorithm.run();
			List<PermutationSolution<Integer>> population = algorithm.getResult();
			DominanceRanking<PermutationSolution<Integer>> ranking =
					new DominanceRanking<>();
			ranking.computeRanking(population);
			List<PermutationSolution<Integer>> paretoFront =
					ranking.getSubFront(0);
			PermutationSolution<Integer> minCost = minCostExtreme(paretoFront);
			PermutationSolution<Integer> minTime = minTimeExtreme(paretoFront);
			boolean improved = false;
			if (bestMinCost == null ||
					minCost.getObjective(0) < bestMinCost.getObjective(0)) {
				bestMinCost = minCost;
				improved = true;
			}
			if (bestMinTime == null ||
					minTime.getObjective(1) < bestMinTime.getObjective(1)) {
				bestMinTime = minTime;
				improved = true;
			}
			if (improved) {
				noChangeCounter = 0;
			} else {
				noChangeCounter++;
			}
			System.out.println(
					"Gen=" + maxIterations +
					" | BestMinCost=" + bestMinCost.getObjective(0) +
					" | BestMinTime=" + bestMinTime.getObjective(1) +
					" | NoChange=" + noChangeCounter
			);
			records.add(List.of(
					String.valueOf(maxIterations),
					String.valueOf(bestMinCost.getObjective(0)),
					String.valueOf(bestMinCost.getObjective(1)),
					String.valueOf(bestMinTime.getObjective(1)),
					String.valueOf(bestMinTime.getObjective(0)),
					String.valueOf(noChangeCounter)
			));
			if (noChangeCounter >= maxNoChange) {
				System.out.println(
						"No hubo mejoras en los extremos durante "
								+ maxNoChange + " ejecuciones consecutivas."
				);
				break;
			}
			maxIterations += step;
		}
		ExportCSV.export(
				"generaciones/instancia3",
				headers,
				records
		);
	}
}
