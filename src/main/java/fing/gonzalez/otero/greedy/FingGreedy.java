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
		numberOfVariables = variables;
		numberOfVehicles = vehicles;
		vehiclesAt = new int[vehicles];
		vehiclesTimes = new double[vehicles];
		pending = new boolean[variables-vehicles+1];
		assigned = new ArrayList<>();
		try {
			distances = MatrixLoader.load("data/distances.csv");
			times = MatrixLoader.load("data/times.csv");
		} catch (Exception e) {
			throw new RuntimeException("Error cargando matrices en greedy de compromiso", e);
		}
	}
	
	/* Getters */
	public List<List<Integer>> getAssignments(){
		return assigned;
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
	
	public double [] distancesRow(int row) {
		return distances[row];
	}
	
	public double [] timesRow(int row) {
		return times[row];
	}
	
	/* Solciones de los greedy */
	public PermutationSolution<Integer> compromiseSolution() {
		clean();
		IntegerPermutationSolution solution =
				new IntegerPermutationSolution(numberOfVariables, 2);
		boolean keepGoing = true;
		Pair<Integer,Integer> halfClosestHalfFastest;
		while (keepGoing) {
			keepGoing = false;
			// buscar mas cercano por tiempo
			halfClosestHalfFastest = halfClosestHalfFastestTo();
			// hacer las asignaciones y marcar como asignado
			pending[halfClosestHalfFastest.getLeft()] = false;
			assigned.get(halfClosestHalfFastest.getRight()).add(halfClosestHalfFastest.getLeft());
			// actualizar datos del vehiculo
			vehiclesTimes[halfClosestHalfFastest.getRight()] = vehiclesTimes[halfClosestHalfFastest.getRight()] + times[halfClosestHalfFastest.getLeft()][vehiclesAt[halfClosestHalfFastest.getRight()]];
			vehiclesAt[halfClosestHalfFastest.getRight()] = halfClosestHalfFastest.getLeft();
			// actualizar keepGoing
			for (int i = 0; i < pending.length; i++) {
				keepGoing = keepGoing || pending[i];
			}
		}
		int position = 0;
		for (int vehicleId = 0; vehicleId < numberOfVehicles; vehicleId++) {
			// agrega el vehiculo
			solution.setVariable(position, vehicleId);
			position++;
			// agrega los receptores del vehiculo
			for (Integer id : assigned.get(vehicleId)) {
				solution.setVariable(position, MatrixLoader.fromIndex(id, numberOfVehicles));
				position++;
			}
		}
		evaluate(solution);
		return solution;
	}

	public PermutationSolution<Integer> costSolution() {
		clean();
		IntegerPermutationSolution solution =
				new IntegerPermutationSolution(numberOfVariables, 2);
		boolean keepGoing = true;
		Pair<Integer, Integer> closest;
		while (keepGoing) {
			keepGoing = false;
			// buscar par mas cercano
			closest = closestTo();
			// hacer las asignaciones y marcar como asignado
			pending[closest.getLeft()] = false;
			assigned.get(closest.getRight()).add(closest.getLeft());
			// actualizar datos del vehiculo
			vehiclesAt[closest.getRight()] = closest.getLeft().intValue();
			// actualizar keepGoing
			for (int i = 0; i < pending.length; i++) {
				keepGoing = keepGoing || pending[i];
			}
		}
		int position = 0;
		for (int vehicleId = 0; vehicleId < numberOfVehicles; vehicleId++) {
			// agrega el vehiculo
			solution.setVariable(position, vehicleId);
			position++;
			// agrega los receptores del vehiculo
			for (Integer id : assigned.get(vehicleId)) {
				solution.setVariable(position, MatrixLoader.fromIndex(id, numberOfVehicles));
				position++;
			}
		}
		evaluate(solution);
		return solution;
	}
	public PermutationSolution<Integer> timeSolution() {
		clean();
		IntegerPermutationSolution solution =
				new IntegerPermutationSolution(numberOfVariables, 2);
		boolean keepGoing = true;
		Pair<Integer, Integer> fastest;
		while (keepGoing) {
			keepGoing = false;
			// buscar par mas cercano por tiempo
			fastest = fastestTo();
			// hacer las asignaciones y marcar como asignado
			pending[fastest.getLeft()] = false;
			assigned.get(fastest.getRight()).add(fastest.getLeft());
			// actualizar datos del vehiculo
			vehiclesTimes[fastest.getRight()] = vehiclesTimes[fastest.getRight()] + times[fastest.getLeft()][vehiclesAt[fastest.getRight()]];
			vehiclesAt[fastest.getRight()] = fastest.getLeft();
			// actualizar keepGoing
			for (int i = 0; i < pending.length; i++) {
				keepGoing = keepGoing || pending[i];
			}
		}
		int position = 0;
		for (int vehicleId = 0; vehicleId < numberOfVehicles; vehicleId++) {
			// agrega el vehiculo
			solution.setVariable(position, vehicleId);
			position++;
			// agrega los receptores del vehiculo
			for (Integer id : assigned.get(vehicleId)) {
				solution.setVariable(position, MatrixLoader.fromIndex(id, numberOfVehicles));
				position++;
			}
		}
		evaluate(solution);
		return solution;
	}

	/* Auxiliares */
	private Pair<Integer, Integer> halfClosestHalfFastestTo() {
		int i = 0;
		try {
			double min = 0.0;
			int index = 0;
			int vehicle = 0;
			for (int vehicleId = 0; vehicleId < vehiclesAt.length; vehicleId++) {
				for (i = 0; i < pending.length; i++) {
					if (i != vehiclesAt[vehicleId] && pending[i]) {
						if (min > (times[vehiclesAt[vehicleId]+1][i] + vehiclesTimes[vehicleId] + distances[vehiclesAt[vehicleId]+1][i])/2 || min == 0.0) {
							min = (times[vehiclesAt[vehicleId]+1][i] + vehiclesTimes[vehicleId] + distances[vehiclesAt[vehicleId]+1][i])/2;
							index = i;
							vehicle = vehicleId;
						}
					}
				}
			}
			return Pair.of(index, vehicle);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException(
				"Index fuera de rango. i=" + i +
				", vehiclesAt[i]=" + (i < vehiclesAt.length ? vehiclesAt[i] : "i fuera de vehiclesAt"),
				e
			);
		}
	}
	
	private Pair<Integer, Integer> fastestTo() {
		int i = 0;
		try {
			double min = 0.0;
			int index = 0;
			int vehicle = 0;
			for (int vehicleId = 0; vehicleId < vehiclesAt.length; vehicleId++) {
				for (i = 0; i < pending.length; i++) {
					if (i != vehiclesAt[vehicleId] && pending[i]) {
						if (min > times[vehiclesAt[vehicleId]+1][i] + vehiclesTimes[vehicleId] || min == 0.0) {
							min = times[vehiclesAt[vehicleId]+1][i] + vehiclesTimes[vehicleId];
							index = i;
							vehicle = vehicleId;
						}
					}
				}
			}
			return Pair.of(index, vehicle);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException(
				"Index fuera de rango. i=" + i +
				", vehiclesAt[i]=" + (i < vehiclesAt.length ? vehiclesAt[i] : "i fuera de vehiclesAt"),
				e
			);
		}
	}

	private Pair<Integer,Integer> closestTo() {
		int i = 0;
		try {
			double min = 0.0;
			int index = 0;
			int vehicle = 0;
			for (int vehicleId = 0; vehicleId < vehiclesAt.length; vehicleId++) {
				for (i = 0; i < pending.length; i++) {
					if (i != vehiclesAt[vehicleId] && pending[i]) {
						if (min > distances[vehiclesAt[vehicleId]+1][i] || min == 0.0) {
							min = distances[vehiclesAt[vehicleId]+1][i];
							index = i;
							vehicle = vehicleId;
						}
					}
				}
			}
			return Pair.of(index, vehicle);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException(
				"Index fuera de rango. i=" + i +
				", vehiclesAt[i]=" + (i < vehiclesAt.length ? vehiclesAt[i] : "i fuera de vehiclesAt"),
				e
			);
		}
	}
	
	/* para limpiar solucion vieja */
	private void clean() {
		for (int i = 0; i < numberOfVehicles; i++) {
			vehiclesAt[i] = 0;
			vehiclesTimes[i] = 0;
		}
		pending[0] = false;
		for (int i = 1; i <= numberOfVariables-numberOfVehicles; i++) {
			pending[i] = true;
		}
		assigned.clear();
		for (int v = 0; v < numberOfVehicles; v++) {
			assigned.add(new ArrayList<>());
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