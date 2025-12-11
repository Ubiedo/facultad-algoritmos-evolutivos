package fing.gonzalez.otero;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FingCrossover implements CrossoverOperator<PermutationSolution<Integer>> {
	private double crossoverProbability;
	private Random random = new Random();

	public FingCrossover(double probability) {
		this.crossoverProbability = probability;
	}

	/* Getters */
	@Override
	public double getCrossoverProbability() {
		return crossoverProbability;
	}

	@Override
	public int getNumberOfRequiredParents() {
		return 2;
	}

	@Override
	public int getNumberOfGeneratedChildren() {
		return 1;
	}

	/* Others */
	/* ChatGPT */
	@Override
	public List<PermutationSolution<Integer>> execute(List<PermutationSolution<Integer>> parents) {
		PermutationSolution<Integer> parent1 = parents.get(0);
		PermutationSolution<Integer> parent2 = parents.get(1);

		List<PermutationSolution<Integer>> offspring = new ArrayList<>(1);

		if (random.nextDouble() < crossoverProbability) {
			offspring.add(ox(parent1, parent2));
		} else {
			offspring.add((PermutationSolution<Integer>) parent1.copy());
		}
		return offspring;
	}
	
	private PermutationSolution<Integer> ox(PermutationSolution<Integer> p1, PermutationSolution<Integer> p2) {
		int size = p1.getVariables().size();

		PermutationSolution<Integer> child = (PermutationSolution<Integer>) p1.copy(); // copia plantilla

		// limpiar todo
		for (int i = 1; i < size; i++)
			child.getVariables().set(i, null);

		// puntos de corte
		int i = random.nextInt(size);
		int j = random.nextInt(size);

		if (i > j) { int tmp = i; i = j; j = tmp; }

		// copiar segmento de P1
		for (int k = i; k <= j; k++) {
			child.getVariables().set(k, p1.getVariables().get(k));
		}

		// rellenar con P2
		int idx = (j + 1) % size;
		for (int k = 0; k < size; k++) {
			int elem = p2.getVariables().get((j + 1 + k) % size);

			if (!child.getVariables().contains(elem)) {
				if (idx == 0) {
					idx = (idx + 1) % size;
				}
				child.getVariables().set(idx, elem);
				idx = (idx + 1) % size;
			}
		}
		return child;
	}
}
