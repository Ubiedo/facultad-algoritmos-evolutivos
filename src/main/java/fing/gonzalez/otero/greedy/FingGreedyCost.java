package fing.gonzalez.otero.greedy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

class FingGreedyCost {
	public PermutationSolution<Integer> solution(int variables, int vehicles) {
		// TODO, esto lo copie de FingProblem, createSolution, falta hacer que cree la solution greedy
		List<Integer> perm = new ArrayList<>();
		for (int i = 0; i < variables; i++) {
			perm.add(i);
		}
		Random rand = new Random();
		int firstValue = rand.nextInt(variables);
		perm.remove((Integer) firstValue);
		Collections.shuffle(perm);
		perm.add(0, firstValue);
		IntegerPermutationSolution solution =
				new IntegerPermutationSolution(variables, 2);
		for (int i = 0; i < variables; i++) {
			solution.setVariable(i, perm.get(i));
		}
		return solution;
	}
}