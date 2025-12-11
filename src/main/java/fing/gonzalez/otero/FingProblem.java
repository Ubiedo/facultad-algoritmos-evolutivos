package fing.gonzalez.otero;

import org.apache.commons.lang3.tuple.Pair;

import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

public class FingProblem extends AbstractIntegerPermutationProblem {
	private int numberOfVehicles;
	private double[][] distances;
	private double[][] times;
	
	public FingProblem (int vehicles, int variables) {
		setNumberOfConstraints(0);
		setNumberOfVariables(variables);
		setNumberOfObjectives(2);
		setNumberOfVehicles(vehicles);
		setName("FingProblem");
		try {
			distances = MatrixLoader.load("/distances.csv");
			times = MatrixLoader.load("/times.csv");
		} catch (Exception e) {
			throw new RuntimeException("Error cargando matrices", e);
		}
	}
	
	/* Setters */
	public void setNumberOfVehicles(int vehicles) {
		numberOfVehicles = vehicles;
	}
	
	/* Getters */
	@Override
	public int getLength() {
		return getNumberOfVariables();
	}
	
	public int getNumberOfVehicles() {
		return numberOfVehicles;
	}
	
	/* Others */
	@Override
	public void evaluate(PermutationSolution<Integer> solution) {
		/* gCosto = ∑c(vi) (2.1) */
		double cost = 0.0;
		/* gTiempo = ∑(t(r)* u(r))/|R| */
		double time = 0.0;
		int fromNode = 0;
		Pair<Double, Double> values;
		for (int i = 1; i < solution.getLength(); i++) {
			/* chequear si i esta en el intervalo de enteros reservado para identificadores de vehiculos */
			int thisNode = solution.getVariable(i);
			if (thisNode < getNumberOfVehicles()) {
				values = obtainCostAndTime(fromNode, 0);
			} else {
				values = obtainCostAndTime(fromNode, thisNode);
				time += values.getRight() * urgency(thisNode);
			}
			cost += values.getLeft();
			fromNode = thisNode;
		}
		time = time / (getNumberOfVariables() - getNumberOfVehicles());
		solution.setObjective(0, cost);
		solution.setObjective(1, time);
	}
	
	public Pair<Double, Double> obtainCostAndTime(int from, int to) {
		if (from == to) {
			return Pair.of(0.0, 0.0);
		}
		from = MatrixLoader.matrixIndex(from, getNumberOfVehicles());
		to = MatrixLoader.matrixIndex(to, getNumberOfVehicles());
		return Pair.of(distances[from][to]*(10.4), times[from][to]*urgency(to));
	}
	
	public int urgency(int id) {
		if (id < 47) {
			return 7;
		}
		if (id < 113) {
			return 5;
		}
		return 3;
	}
}