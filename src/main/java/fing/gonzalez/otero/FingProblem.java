package fing.gonzalez.otero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

import fing.gonzalez.otero.utils.MatrixLoader;

import org.apache.commons.lang3.tuple.Pair;

import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

public class FingProblem extends AbstractIntegerPermutationProblem {
	private int numberOfVehicles;
	private double[][] distances;
	private double[][] times;
	
	public FingProblem (int variables, int vehicles) {
		setNumberOfConstraints(0);
		setNumberOfVariables(variables);
		setNumberOfObjectives(2);
		setNumberOfVehicles(vehicles);
		setName("FingProblem");
		try {
			distances = MatrixLoader.load("data/distances.csv");
			times = MatrixLoader.load("data/times.csv");
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
		double accumulatedTime = 0.0;
		Pair<Double, Double> values;
		for (int i = 1; i < solution.getLength(); i++) {
			/* chequear si i esta en el intervalo de enteros reservado para identificadores de vehiculos */
			int thisNode = solution.getVariable(i);
			if (thisNode < getNumberOfVehicles()) {
				cost += obtainCost(fromNode, 0);
				fromNode = 0;
				accumulatedTime = 0;
			} else {
				cost += obtainCost(fromNode, thisNode);
				accumulatedTime += obtainTime(fromNode, 0, accumulatedTime);
				time += accumulatedTime*urgency(MatrixLoader.matrixIndex(thisNode, getNumberOfVehicles()));
				fromNode = thisNode;
			}
		}
		time = time / (getNumberOfVariables() - getNumberOfVehicles());
		solution.setObjective(0, cost); // pesos, gasto de combustible
		solution.setObjective(1, time); // segundos, tiempo medio de llegada al receptor
	}
	
	public double obtainCost(int from, int to) {
		if (from == to) {
			return 0.0;
		}
		from = MatrixLoader.matrixIndex(from, getNumberOfVehicles());
		to = MatrixLoader.matrixIndex(to, getNumberOfVehicles());
		return distances[from][to]*(0.0104);
	}
	
	public double obtainTime(int from, int to, double accumulatedTime) {
		if (from == to) {
			return 0.0;
		}
		from = MatrixLoader.matrixIndex(from, getNumberOfVehicles());
		to = MatrixLoader.matrixIndex(to, getNumberOfVehicles());
		return accumulatedTime + times[from][to];
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
	
	/* Evitar que se cree una solucion sin vehiculo al comienzo */
	@Override
	public PermutationSolution<Integer> createSolution() {
		List<Integer> perm = new ArrayList<>();
		for (int i = 0; i < getNumberOfVariables(); i++) {
			perm.add(i);
		}
		Random rand = new Random();
		int firstValue = rand.nextInt(getNumberOfVehicles());
		perm.remove((Integer) firstValue);
		Collections.shuffle(perm);
		perm.add(0, firstValue);
		IntegerPermutationSolution solution =
				new IntegerPermutationSolution(getNumberOfVariables(), getNumberOfObjectives());
		for (int i = 0; i < getNumberOfVariables(); i++) {
			solution.setVariable(i, perm.get(i));
		}
		return solution;
	}

}