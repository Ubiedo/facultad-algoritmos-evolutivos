package fing.gonzalez.otero.greedy;

import fing.gonzalez.otero.utils.MatrixLoader;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

// ejemplo de solucion valida, esta 0 como vehiculo
// [20, 145, 105, 40, 144, 14, 81, 34, 74, 175, 59, 31, 22, 135, 5, 80, 196, 134, 123, 171, 127, 30, 187, 36, 103, 102, 191, 160, 76, 161, 84, 132, 130, 9, 23, 18, 58, 148, 106, 124, 153, 69, 114, 92, 39, 143, 107, 67, 19, 52, 26, 17, 116, 94, 50, 49, 146, 108, 185, 192, 25, 77, 158, 189, 109, 99, 57, 4, 27, 179, 63, 71, 170, 41, 60, 120, 176, 133, 45, 37, 174, 115, 33, 104, 46, 96, 154, 70, 65, 111, 149, 44, 117, 141, 173, 190, 113, 8, 128, 164, 12, 122, 151, 165, 167, 1, 147, 47, 138, 157, 125, 15, 119, 195, 129, 88, 181, 54, 7, 131, 97, 53, 78, 182, 62, 86, 98, 156, 168, 3, 48, 93, 188, 42, 66, 55, 2, 137, 90, 136, 89, 139, 91, 32, 162, 73, 166, 142, 79, 35, 180, 13, 177, 75, 21, 24, 155, 183, 56, 100, 194, 11, 10, 163, 61, 16, 140, 193, 172, 0, 112, 6, 126, 121, 178, 43, 110, 72, 68, 85, 95, 159, 38, 184, 28, 186, 51, 101, 87, 64, 29, 152, 82, 169, 83, 118, 150]

class FingGreedy {
	private double [][] distances;
	private double [][] times;
	private int numberOfVariables;
	private int numberOfVehicles;
	private int [] vehiclesAt;
	private double [] vehiclesTimes;
	private boolean [] pending;
	private List<List<Integer>> assigned;
	
	public FingGreedy (int variables, int vehicles) {
		vehiclesAt = new int[vehicles];
		vehiclesTimes = new double[vehicles];
		for (int i = 0; i < vehicles; i++) {
			vehiclesAt[i] = 0;
			vehiclesTimes[i] = 0;
		}
		pending = new boolean[variables-vehicles+1];
		pending[0] = false;
		for (int i = 1; i <= variables-vehicles; i++) {
			pending[i] = true;
		}
		assigned = new ArrayList<>();
		for (int v = 0; v < vehicles; v++) {
			assigned.add(new ArrayList<>());
		}
		try {
			distances = MatrixLoader.load("data/distances.csv");
			times = MatrixLoader.load("data/times.csv");
		} catch (Exception e) {
			throw new RuntimeException("Error cargando matrices en greedy de compromiso", e);
		}
	}
	
	/* Para ver detalles que pueden dar fallo en indice */
	public int distancesRowLength() {
		return distances.length;
	}

	public int distancesColumnLength() {
		return distances[0].length;
	}
	
	public int timesRowLength() {
		return times.length;
	}

	public int timesColumnLength() {
		return times[0].length;
	}
	
	public int pendingLength() {
		return pending.length;
	}
	
	/* Solciones de los greedy */
	public PermutationSolution<Integer> compromiseSolution() {
		IntegerPermutationSolution solution =
				new IntegerPermutationSolution(numberOfVariables, 2);
		boolean keepGoing = true;
		int pin = 0;
		int closest = 0;
		int vehicle = 0;
		while (keepGoing) {
			keepGoing = false;
			// buscar mas cercano por tiempo
			closest = closestTo(pin);
			// buscar vehiculo mas cercano por tiempo
			vehicle = fastestVehicle(closest);
			// hacer las asignaciones y marcar como asignado
			pending[closest] = false;
			assigned.get(vehicle).add(closest);
			// actualizar datos del vehiculo
			vehiclesTimes[vehicle] = vehiclesTimes[vehicle] + times[closest][vehiclesAt[vehicle]];
			vehiclesAt[vehicle] = closest;
			// actualizar pin
			pin = closest;
			// actualizar keepGoing
			for (int i = 0; i < pending.length; i++) {
				keepGoing = keepGoing || pending[i];
			}
		}
		for (int i = 0; i < numberOfVariables; i++) {
			// agrega el vehiculo
			solution.setVariable(i, i);
			// agrega los receptores del vehiculo
			for (Integer id : assigned.get(i)) {
				solution.setVariable(i, id);
				i++;
			}
		}
		evaluate(solution);
		return solution;
	}
	
	public PermutationSolution<Integer> costSolution() {
		IntegerPermutationSolution solution =
				new IntegerPermutationSolution(numberOfVariables, 2);
		boolean keepGoing = true;
		int pin = 0;
		int closest = 0;
		int vehicle = 0;
		while (keepGoing) {
			keepGoing = false;
			// buscar mas cercano
			closest = closestTo(pin);
			// buscar vehiculo mas cercano
			vehicle = closestVehicle(closest);
			// hacer las asignaciones y marcar como asignado
			pending[closest] = false;
			assigned.get(vehicle).add(closest);
			// actualizar datos del vehiculo
			vehiclesAt[vehicle] = closest;
			// actualizar pin
			pin = closest;
			// actualizar keepGoing
			for (int i = 0; i < pending.length; i++) {
				keepGoing = keepGoing || pending[i];
			}
		}
		for (int i = 0; i < numberOfVariables; i++) {
			// agrega el vehiculo
			solution.setVariable(i, i);
			// agrega los receptores del vehiculo
			for (Integer id : assigned.get(i)) {
				solution.setVariable(i, id);
				i++;
			}
		}
		evaluate(solution);
		return solution;
	}
	public PermutationSolution<Integer> timeSolution() {
		IntegerPermutationSolution solution =
				new IntegerPermutationSolution(numberOfVariables, 2);
		boolean keepGoing = true;
		int pin = 0;
		int fastest = 0;
		int vehicle = 0;
		while (keepGoing) {
			keepGoing = false;
			// buscar mas cercano por tiempo
			fastest = fastestTo(pin);
			// buscar vehiculo mas cercano por tiempo
			vehicle = fastestVehicle(fastest);
			// hacer las asignaciones y marcar como asignado
			pending[fastest] = false;
			assigned.get(vehicle).add(fastest);
			// actualizar datos del vehiculo
			vehiclesTimes[vehicle] = vehiclesTimes[vehicle] + times[fastest][vehiclesAt[vehicle]];
			vehiclesAt[vehicle] = fastest;
			// actualizar pin
			pin = fastest;
			// actualizar keepGoing
			for (int i = 0; i < pending.length; i++) {
				keepGoing = keepGoing || pending[i];
			}
		}
		for (int i = 0; i < numberOfVariables; i++) {
			// agrega el vehiculo
			solution.setVariable(i, i);
			// agrega los receptores del vehiculo
			for (Integer id : assigned.get(i)) {
				solution.setVariable(i, id);
				i++;
			}
		}
		evaluate(solution);
		return solution;
	}

	/* Auxiliares */
	private int fastestTo(int pin) {
		int i = 0;
		try {
			boolean initialized = false;
			double min = 0.0;
			int index = 0;
			for (i = 0; i < pending.length; i++) {
				if (i != pin && pending[i]) {
					if (!initialized) {
						min = times[pin][i];
						index = i;
					} else {
						if (min > times[pin][i]) {
							min = times[pin][i];
							index = i;
						}
					}
				}
			}
			return MatrixLoader.fromIndex(index, numberOfVehicles);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException(
				"Index fuera de rango. pin=" + pin +
				", i=" + i +
				", pin+1=" + (pin + 1) +
				", vehiclesAt[i]=" + (i < vehiclesAt.length ? vehiclesAt[i] : "i fuera de vehiclesAt"),
				e
			);
		}
	}
	
	private int closestVehicle(int pin) {
		int i = 0;
		try {
			double min = distances[pin][vehiclesAt[0]];
			int vehicle = 0;
			for (i = 1; i < vehiclesAt.length; i++) {
				if (min > distances[pin][vehiclesAt[i]]) {
					min = distances[pin][vehiclesAt[i]];
					vehicle = i;
				}
			}
			return vehicle;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException(
				"Index fuera de rango. pin=" + pin +
				", i=" + i +
				", pin+1=" + (pin + 1) +
				", vehiclesAt[i]=" + (i < vehiclesAt.length ? vehiclesAt[i] : "i fuera de vehiclesAt"),
				e
			);
		}
	}

	private int closestTo(int pin) {
		int i = 0;
		try {
			boolean initialized = false;
			double min = 0.0;
			int index = 0;
			for (i = 0; i < pending.length; i++) {
				if (i != pin && pending[i]) {
					if (!initialized) {
						min = distances[pin][i];
						index = i;
					} else {
						if (min > distances[pin][i]) {
							min = distances[pin][i];
							index = i;
						}
					}
				}
			}
			return MatrixLoader.fromIndex(index, numberOfVehicles);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException(
				"Index fuera de rango. pin=" + pin +
				", i=" + i +
				", pin+1=" + (pin + 1) +
				", vehiclesAt[i]=" + (i < vehiclesAt.length ? vehiclesAt[i] : "i fuera de vehiclesAt"),
				e
			);
		}
	}
	
	private int fastestVehicle(int pin) {
		int i = 0;
		try {
			double min = times[pin][vehiclesAt[0]] + vehiclesTimes[0];
			int vehicle = 0;
			for (i = 1; i < vehiclesAt.length; i++) {
				if (min > times[pin][vehiclesAt[i]] + vehiclesTimes[i]) {
					min = times[pin][vehiclesAt[i]] + vehiclesTimes[i];
					vehicle = i;
				}
			}
			return vehicle;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException(
				"Index fuera de rango. pin=" + pin +
				", i=" + i +
				", pin+1=" + (pin + 1) +
				", vehiclesAt[i]=" + (i < vehiclesAt.length ? vehiclesAt[i] : "i fuera de vehiclesAt"),
				e
			);
		}
	}
	
	/* para evaluar soluciones, idem a FingProblem */
	
	private void evaluate(PermutationSolution<Integer> solution) {
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
			if (thisNode < numberOfVehicles) {
				cost += obtainCost(fromNode, 0);
				fromNode = 0;
				accumulatedTime = 0;
			} else {
				cost += obtainCost(fromNode, thisNode);
				accumulatedTime += obtainTime(fromNode, 0, accumulatedTime);
				time += accumulatedTime*urgency(MatrixLoader.toIndex(thisNode, numberOfVehicles));
				fromNode = thisNode;
			}
		}
		time = time / (numberOfVariables - numberOfVehicles);
		solution.setObjective(0, cost); // pesos, gasto de combustible
		solution.setObjective(1, time); // segundos, tiempo medio de llegada al receptor
	}
	
	private double obtainCost(int from, int to) {
		if (from == to) {
			return 0.0;
		}
		from = MatrixLoader.toIndex(from, numberOfVehicles);
		to = MatrixLoader.toIndex(to, numberOfVehicles);
		return distances[from][to]*(0.0104);
	}
	
	private double obtainTime(int from, int to, double accumulatedTime) {
		if (from == to) {
			return 0.0;
		}
		from = MatrixLoader.toIndex(from, numberOfVehicles);
		to = MatrixLoader.toIndex(to, numberOfVehicles);
		return accumulatedTime + times[from][to];
	}
	
	private int urgency(int id) {
		if (id < 47) {
			return 7;
		}
		if (id < 113) {
			return 5;
		}
		return 3;
	}
}