package fing.gonzalez.otero.greedy;

import fing.gonzalez.otero.utils.MatrixLoader;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

class FingGreedyCost {
	private double [][] distances;
	
	public PermutationSolution<Integer> solution(int variables, int vehicles) {
		// TODO
		IntegerPermutationSolution solution =
				new IntegerPermutationSolution(variables, 2);
		// cargar matriz de distancia
		try {
			distances = MatrixLoader.load("/data/distances.csv");
		} catch (Exception e) {
			throw new RuntimeException("Error cargando matrices en greedy de costo", e);
		}
		// encontrar solucion greedy
		int [] vehiclesAt = new int[vehicles];
		for (int i = 0; i < vehicles; i++) {
			vehiclesAt[i] = 0;
		}
		boolean keepGoing = true;
		boolean [] pending = new boolean[variables-vehicles];
		int [] assigned = new int[variables-vehicles]; // guarda el receptor anterior a si mismo en el vehiculo o -1 si es primero
		for (int i = 0; i < variables-vehicles; i++) {
			pending[i] = true;
			assigned[i] = -1;
		}
		int pin = 0;
		int closest = 0;
		int closestVehicle = 0;
		// bucles de obtener el mas cercano al pin pendiente y 
		// asignarlo al final de vehiculo mas cercano
		while (keepGoing) {
			keepGoing = false;
			// hallar receptor mas cerccano
			closest = closestTo(pin, variables, vehicles, pending);
			pending[closest] = false;
			// asignarlo a vehiculo mas cercano
			closestVehicle = closestVehicle(closest, vehiclesAt);
			if (vehiclesAt[closestVehicle] > 0) { // sino fue el primero y mantiene el -1
				assigned[closest] = vehiclesAt[closestVehicle];
			}
			vehiclesAt[closestVehicle] = closest;
			// actualizar pin
			pin  = closest;
			// chequear si falta alguno
			for (int i = 0; i < variables-vehicles; i++) {
				keepGoing = keepGoing || pending[i];
			}
		}
		// reconstruir la solucion yendo vehiculo por vehiculo y se tiene el ultimo receptor de cada vehiculo donde este guarda su anterior
		// se recorre esto hasta que se llega a uno con -1
		for (int i = 0; i < variables; i++) {
			for (int v = 0; v < vehicles; v++) {
				solution.setVariable(i, v);
				i++;
				// cargar sus receptores
				List<Integer> variablesOfV = obtainVariablesOfVehicle(vehiclesAt[v], assigned);
				for (Integer var : variablesOfV) {
					solution.setVariable(i, var);
					i++;
				}
			}
		}
		return solution;
	}
	
	private List<Integer> obtainVariablesOfVehicle(int var, int [] assigned) {
		List<Integer> variables = new ArrayList<>();
		while (assigned[var] != -1) {
			variables.addFirst(var);
			var = assigned[var];
		}
		return variables;
	}

	private int closestTo(int pin, int variables, int vehicles, boolean [] pending) {
		boolean initialized = false;
		double min = 0.0;
		int index = 0;
		for (int i = 0; i < variables-vehicles; i++) {
			if (i != pin && pending[i]) {
				if (!initialized) {
					min = distances[pin][i+1];
					index = i;
				} else {
					if (min > distances[pin][i+1]) {
						min = distances[pin][i+1];
						index = i+1;
					}
				}
			}
		}
		return MatrixLoader.fromIndex(index, vehicles)-1;
	}
	
	private int closestVehicle(int pin, int [] vehiclesAt) {
		double min = distances[pin][vehiclesAt[0]];
		int vehicle = 0;
		for (int i = 1; i < vehiclesAt.length; i++) {
			if (min > distances[pin][vehiclesAt[i]]) {
				min = distances[pin][vehiclesAt[i]];
				vehicle = i;
			}
		}
		return vehicle;
	}
}