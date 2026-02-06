package fing.gonzalez.otero.jmetal;

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
	private int [] idsToUse;
	
	public FingProblem (int vehicles, int [] ids) {
		setNumberOfConstraints(0);
		setNumberOfVariables(ids.length + vehicles);
		setNumberOfObjectives(2);
		setNumberOfVehicles(vehicles);
		setName("FingProblem");
		setIdsToUse(ids);
		try {
			times = MatrixLoader.load("data/times.csv");
			distances = MatrixLoader.load("data/distances.csv");
		} catch (Exception e) {
			throw new RuntimeException("Error cargando matrices", e);
		}
	}
	
	/* Setters */
	public void setNumberOfVehicles(int vehicles) {
		numberOfVehicles = vehicles;
	}
	
	public void setIdsToUse(int [] ids) {
	    idsToUse = ids;
	}
	
	/* Getters */
	@Override
	public int getLength() {
		return getNumberOfVariables();
	}
	
	public int getNumberOfVehicles() {
		return numberOfVehicles;
	}
	
	public int [] getIdsToUse() {
	    return idsToUse;
	}
	
	/* Others */
	@Override
	public void evaluate(PermutationSolution<Integer> solution) {
        /* gCosto = ∑c(vi) (2.1) */
        /* gTiempo = ∑(t(r)* u(r))/|R| */
        double cost = 0.0, time = 0.0, accumulatedTime = 0.0;
        int fromNode = 0, vehicleCapacity = 100;
        for (int i = 1; i < solution.getLength(); i++) {
            /* chequear si i esta en el intervalo de enteros reservado para identificadores de vehiculos */
            int thisNode = solution.getVariable(i);
            if (thisNode <= 0) {
                cost += obtainCost(fromNode, 0);
                fromNode = 0;
                accumulatedTime = 0;
                vehicleCapacity = 100;
            } else {
                // chequear que tenga espacio para enviarle a ese receptor
                if (vehicleCapacity >= weight(MatrixLoader.toIndex(thisNode))) {
                    vehicleCapacity -= weight(MatrixLoader.toIndex(thisNode));
                } else {
                    // agregar coste de volver al centro de distribucion
                    cost += obtainCost(fromNode, 0);
                    accumulatedTime += obtainTime(fromNode, 0);
                    fromNode = 0;
                    // resetear capacidad
                    vehicleCapacity = 100 - weight(MatrixLoader.toIndex(thisNode));
                }
                cost += obtainCost(fromNode, thisNode);
                accumulatedTime += obtainTime(fromNode, thisNode);
                time += accumulatedTime*urgency(MatrixLoader.toIndex(thisNode));
                fromNode = thisNode;
            }
        }
        time = time / (idsToUse.length);
        solution.setObjective(0, cost); // pesos, gasto de combustible
        solution.setObjective(1, time); // segundos, tiempo medio de llegada al receptor
    }
	
	public double obtainCost(int from, int to) {
		if (from == to) {
			return 0.0;
		}
		from = MatrixLoader.toIndex(from);
		to = MatrixLoader.toIndex(to);
		return distances[from+1][to]*(0.0104);
	}
	
	public double obtainTime(int from, int to) {
		if (from == to) {
			return 0.0;
		}
		from = MatrixLoader.toIndex(from);
		to = MatrixLoader.toIndex(to);
		return times[from+1][to];
	}
	
	public int urgency(int index) {
		if (index < 47) {
			return 7;
		}
		if (index < 113) {
			return 5;
		}
		return 3;
	}
	
	public int weight(int index) {
		if (index < 47) {
			return 15;
		}
		if (index < 113) {
			return 9;
		}
		return 4;
	}
	
	/* Evitar que se cree una solucion sin vehiculo al comienzo */
	@Override
	public PermutationSolution<Integer> createSolution() {
		List<Integer> perm = new ArrayList<>();
		// agregamos los vehiculos
		for (int i = 1; i < getNumberOfVehicles(); i++) {
		    perm.add(-1*i);
		}
		// agregamos los receptores
		for (int id : getIdsToUse()) {
			perm.add(id);
		}
		Collections.shuffle(perm);
		perm.add(0, 0);
		IntegerPermutationSolution solution =
				new IntegerPermutationSolution(getNumberOfVariables(), getNumberOfObjectives());
		for (int i = 0; i < getNumberOfVariables(); i++) {
			solution.setVariable(i, perm.get(i));
		}
		return solution;
	}

}