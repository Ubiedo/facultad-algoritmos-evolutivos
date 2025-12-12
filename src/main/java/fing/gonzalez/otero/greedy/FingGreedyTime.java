package fing.gonzalez.otero.greedy;

import fing.gonzalez.otero.utils.MatrixLoader;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

// ejemplo de solucion valida, esta 0 como vehiculo
// [20, 145, 105, 40, 144, 14, 81, 34, 74, 175, 59, 31, 22, 135, 5, 80, 196, 134, 123, 171, 127, 30, 187, 36, 103, 102, 191, 160, 76, 161, 84, 132, 130, 9, 23, 18, 58, 148, 106, 124, 153, 69, 114, 92, 39, 143, 107, 67, 19, 52, 26, 17, 116, 94, 50, 49, 146, 108, 185, 192, 25, 77, 158, 189, 109, 99, 57, 4, 27, 179, 63, 71, 170, 41, 60, 120, 176, 133, 45, 37, 174, 115, 33, 104, 46, 96, 154, 70, 65, 111, 149, 44, 117, 141, 173, 190, 113, 8, 128, 164, 12, 122, 151, 165, 167, 1, 147, 47, 138, 157, 125, 15, 119, 195, 129, 88, 181, 54, 7, 131, 97, 53, 78, 182, 62, 86, 98, 156, 168, 3, 48, 93, 188, 42, 66, 55, 2, 137, 90, 136, 89, 139, 91, 32, 162, 73, 166, 142, 79, 35, 180, 13, 177, 75, 21, 24, 155, 183, 56, 100, 194, 11, 10, 163, 61, 16, 140, 193, 172, 0, 112, 6, 126, 121, 178, 43, 110, 72, 68, 85, 95, 159, 38, 184, 28, 186, 51, 101, 87, 64, 29, 152, 82, 169, 83, 118, 150]

class FingGreedyTime {
	private double [][] times;
	private int numberOfVariables;
	private int numberOfVehicles;
	private int [] vehiclesAt;
	private double [] vehiclesTimes;
	private boolean [] pending;
	private List<List<Integer>> assigned;
	
	public FingGreedyTime (int variables, int vehicles) {
		int [] vehiclesAt = new int[vehicles];
		for (int i = 0; i < vehicles; i++) {
			vehiclesAt[i] = 0;
			vehiclesTimes[i] = 0;
		}
		boolean [] pending = new boolean[variables-vehicles];
		for (int i = 0; i < variables-vehicles; i++) {
			pending[i] = true;
		}
		assigned = new ArrayList<>();
		for (int v = 0; v < vehicles; v++) {
			assigned.add(new ArrayList<>());
		}
		try {
			times = MatrixLoader.load("/data/times.csv");
		} catch (Exception e) {
			throw new RuntimeException("Error cargando matrices en greedy de tiempo", e);
		}
	}
	
	public PermutationSolution<Integer> solution() {
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
			assigned.get(vehicle).addLast(fastest);
			// actualizar datos del vehiculo
			vehiclesAt[vehicle] = fastest;
			vehiclesTimes[vehicle] = vehiclesTimes[vehicle] + times[pin][vehiclesAt[vehicle]];
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
		return solution;
	}

	private int fastestTo(int pin) {
		boolean initialized = false;
		double min = 0.0;
		int index = 0;
		for (int i = 1; i <= pending.length; i++) {
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
	}
	
	private int fastestVehicle(int pin) {
		double min = times[pin][vehiclesAt[0]] + vehiclesTimes[0];
		int vehicle = 0;
		for (int i = 1; i < vehiclesAt.length; i++) {
			if (min > times[pin][vehiclesAt[i]] + vehiclesTimes[i]) {
				min = times[pin][vehiclesAt[i]] + vehiclesTimes[i];
				vehicle = i;
			}
		}
		return vehicle;
	}
}