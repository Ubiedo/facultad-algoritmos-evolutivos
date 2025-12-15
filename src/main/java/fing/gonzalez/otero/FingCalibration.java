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

public class FingCalibration {
	public static void main(String[] args) {
		FingProblem problem = new FingProblem(
				195 /* variables,  V + |R| = ?   */, 
				43  /* vehicles,  V = 43|R|/152 */
		);
		int initialPopulation = 100;
		int maxGenerations = 100000;
		double [] crossoverValues = {0.3, 0.6, 0.9};
		double [] mutationValues  = {0.2, 0.1, 0.05};
		long startTime = 0;
		long endTime = 0;
		long durationMillis = 0;
		double durationSeconds = 0;
		int config = 0;
		List<String> headers = new ArrayList<String>();
		headers.add("costo");
		headers.add("tiempo");
		for (double crossoverProbability : crossoverValues) {
			for (double mutationProbability : mutationValues) {
				for (int run = 0; run < 30; run++) {
					config++;
					FingCrossover crossover = new FingCrossover(crossoverProbability);
					FingMutation mutation = new FingMutation(mutationProbability);
					SelectionOperator<List<PermutationSolution<Integer>>,
							PermutationSolution<Integer>> selection =
							new BinaryTournamentSelection<>(
									new RankingAndCrowdingDistanceComparator<>()
							);
					// tiempo de inicio
					startTime = System.currentTimeMillis();
					Algorithm<List<PermutationSolution<Integer>>> algorithm =
							new NSGAIIBuilder<>(problem, crossover, mutation, initialPopulation)
									.setSelectionOperator(selection)
									.setMaxEvaluations(maxGenerations)
									.build();
					algorithm.run();
					// tiempo de fin
					endTime = System.currentTimeMillis();
					durationMillis = endTime - startTime; // tiempo en milisegundos
					durationSeconds = durationMillis / 1000.0; // tiempo en segundos
					List<PermutationSolution<Integer>> population = algorithm.getResult();
					System.out.println("Soluciones finales:");
					for (PermutationSolution<Integer> sol : population) {
						System.out.println(sol.getVariables());
						System.out.println("Costo = " + sol.getObjective(0));
						System.out.println("Tiempo = " + sol.getObjective(1));
						System.out.println("-------------------");
					}
					System.out.println("Tiempo de ejecución: " + durationSeconds + " segundos");
					ExportCSV.exportPermutation("instancia1/conf-param-"+config+"/run"+run, headers, population);
				}
			}
		}
	}
}
