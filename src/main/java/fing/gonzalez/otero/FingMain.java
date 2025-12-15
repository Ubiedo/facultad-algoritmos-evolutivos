package fing.gonzalez.otero;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import fing.gonzalez.otero.utils.ExportCSV;

import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;

import java.util.ArrayList;
import java.util.List;

public class FingMain {
	public static void main(String[] args) {
		FingProblem problem = new FingProblem(
				195 /* variables,  V + |R| = ?   */, 
				43  /* vehicles,  V = 43|R|/152 */,
				"completa"
		);
		double crossoverProbability = 0.9;
		double mutationProbability = 0.2;
		int initialPopulation = 100;
		int maxGenerations = 100000;
		FingCrossover crossover = new FingCrossover(crossoverProbability);
		FingMutation mutation = new FingMutation(mutationProbability);
		SelectionOperator<List<PermutationSolution<Integer>>,
				PermutationSolution<Integer>> selection =
				new BinaryTournamentSelection<>(
						new RankingAndCrowdingDistanceComparator<>()
				);
		// tiempo de inicio
		long startTime = System.currentTimeMillis();
		Algorithm<List<PermutationSolution<Integer>>> algorithm =
				new NSGAIIBuilder<>(problem, crossover, mutation, initialPopulation)
						.setSelectionOperator(selection)
						.setMaxEvaluations(maxGenerations)
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
		List<String> headers = new ArrayList<String>();
		headers.add("costo");
		headers.add("tiempo");
		ExportCSV.exportPermutation("ae-"+initialPopulation+"-"+maxGenerations, headers, population);
	}
}
