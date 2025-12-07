package fing.gonzalez.otero;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

import java.util.Random;

public class FingMutation implements MutationOperator<PermutationSolution<Integer>> {
	private double mutationProbability;
	private Random random = new Random();

	public FingMutation(double probability) {
		this.mutationProbability = probability;
	}

	/* Getters */
	@Override
	public double getMutationProbability() {
		return mutationProbability;
	}

	/* Others */
	@Override
	public PermutationSolution<Integer> execute(PermutationSolution<Integer> solution) {
		if (random.nextDouble() < mutationProbability) {
			int size = solution.getVariables().size();

			int i = random.nextInt(size);
			int j = random.nextInt(size);

			while (j == i)
				j = random.nextInt(size);

			// swap
			Integer tmp = solution.getVariables().get(i);
			solution.getVariables().set(i, solution.getVariables().get(j));
			solution.getVariables().set(j, tmp);
		}
		return solution;
	}
}
