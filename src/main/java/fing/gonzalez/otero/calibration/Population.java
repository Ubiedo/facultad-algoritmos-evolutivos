package fing.gonzalez.otero.calibration;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import fing.gonzalez.otero.jmetal.FingCrossover;
import fing.gonzalez.otero.jmetal.FingMutation;
import fing.gonzalez.otero.jmetal.FingProblem;
import fing.gonzalez.otero.utils.ExportCSV;
import fing.gonzalez.otero.utils.MatrixLoader;

import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;

import java.util.ArrayList;
import java.util.List;

public class Population {
	public static void main(String[] args) {
		FingProblem problem = new FingProblem(
				13  /* vehicles,  V = 43|R|/152 */,
                MatrixLoader.idsSet(1) /* ids a usar */
		);
		int [] populationValues = {50, 100, 200};
		int maxGenerations = 200000;
		double crossoverProbability = 0.9;
		double mutationProbability  = 0.2;
		long startTime = 0;
		long endTime = 0;
		long durationMillis = 0;
		double durationSeconds = 0;
		List<String> headers = new ArrayList<String>();
		headers.add("costo");
		headers.add("tiempo");
		for (int initialPopulation : populationValues) {
			for (int run = 0; run < 30; run++) {
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
				ExportCSV.exportPermutation("instancia3/population-"+initialPopulation+"/run"+run, headers, population);
			}
		}
	}
}
