package fing.gonzalez.otero;

import org.apache.commons.lang3.tuple.Pair;

import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

public class FingProblem extends AbstractIntegerPermutationProblem {
	/* Extra variables for our problem */
	private int numberOfVehicles;
	
	public FingProblem (int vehicles, int variables) {
		setNumberOfConstraints(0);
		setNumberOfVariables(variables);
		setNumberOfObjectives(2);
		setNumberOfVehicles(vehicles);
		setName("FingProblem");
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
	
	// funcion que devluevle el coste operativo de ir de una ubicacion a otra, si es la misma devuelve 0
	// y devuelve tambien el tiempo de ir de una ubicacion a otra
	// devuelve (costo, tiempo)
	public Pair<Double, Double> obtainCostAndTime(int from, int to) {
		// TODO
		if (from == to) {
			return Pair.of(0.0, 0.0);
		}
		return Pair.of(0.0, 0.0); // esto no es, aca deberia de ir obtener la distancia de una a otra, y calcularle el gasto de combustible y el tiempo medio
	}
	
	// devuelve una constante para cada tipo de urgencia
	public int urgency(int id) {
		// TODO
		return 1;
	}
}