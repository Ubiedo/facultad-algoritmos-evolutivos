package fing.gonzalez.otero.greedy;

import fing.gonzalez.otero.utils.MatrixLoader;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

// ejemplo de solucion valida, esta 0 como vehiculo
// [20, 145, 105, 40, 144, 14, 81, 34, 74, 175, 59, 31, 22, 135, 5, 80, 196, 134, 123, 171, 127, 30, 187, 36, 103, 102, 191, 160, 76, 161, 84, 132, 130, 9, 23, 18, 58, 148, 106, 124, 153, 69, 114, 92, 39, 143, 107, 67, 19, 52, 26, 17, 116, 94, 50, 49, 146, 108, 185, 192, 25, 77, 158, 189, 109, 99, 57, 4, 27, 179, 63, 71, 170, 41, 60, 120, 176, 133, 45, 37, 174, 115, 33, 104, 46, 96, 154, 70, 65, 111, 149, 44, 117, 141, 173, 190, 113, 8, 128, 164, 12, 122, 151, 165, 167, 1, 147, 47, 138, 157, 125, 15, 119, 195, 129, 88, 181, 54, 7, 131, 97, 53, 78, 182, 62, 86, 98, 156, 168, 3, 48, 93, 188, 42, 66, 55, 2, 137, 90, 136, 89, 139, 91, 32, 162, 73, 166, 142, 79, 35, 180, 13, 177, 75, 21, 24, 155, 183, 56, 100, 194, 11, 10, 163, 61, 16, 140, 193, 172, 0, 112, 6, 126, 121, 178, 43, 110, 72, 68, 85, 95, 159, 38, 184, 28, 186, 51, 101, 87, 64, 29, 152, 82, 169, 83, 118, 150]

class Time {
    private double [][] distances;
    private double [][] times;
    private int numberOfVariables;
    private int numberOfVehicles;
    private List<List<Integer>> routes;
    
    public Time (int variables, int vehicles) {
        numberOfVariables = variables;
        numberOfVehicles = vehicles;
        routes = new ArrayList<>();
        try {
            distances = MatrixLoader.load("data/distances_c.csv");
            times = MatrixLoader.load("data/times_c.csv");
        } catch (Exception e) {
            throw new RuntimeException("Error cargando matrices en greedy de compromiso", e);
        }
    }
    
    /* Getters */
    public List<List<Integer>> getRoutes(){
        return routes;
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
    
    public double [] distancesRow(int row) {
        return distances[row];
    }
    
    public double [] timesRow(int row) {
        return times[row];
    }

    public PermutationSolution<Integer> solution() {
        clean();
        IntegerPermutationSolution solution =
                new IntegerPermutationSolution(numberOfVariables, 2);
        boolean keepGoing = true;
        /* sigue idea de algoritmo de Clark & Wright (Savings) */
        /* inicializar rutas CD -> r -> CD, para cada r */
        int position;
        Pair<Integer, Integer> bestOption; // ruta, receptor
        for (int receptor = numberOfVehicles; receptor < numberOfVariables; receptor++) {
            // calcular los deltas
            bestOption = calculateBestOption();
            // realizar la mejor opcion
            addToRoute(bestOption.getLeft(), bestOption.getRight());
        }
        position = 0;
        for (int vehicleId = 0; vehicleId < numberOfVehicles; vehicleId++) {
            // agrega el vehiculo
            solution.setVariable(position, vehicleId);
            position++;
            // agrega los receptores del vehiculo
            if (vehicleId < routes.size()) {
                for (Integer id : routes.get(vehicleId)) {
                    solution.setVariable(position, id);
                    position++;
                }
            }
        }
        evaluate(solution);
        return solution;
    }

    /* Auxiliares */
    private Pair<Integer, Integer> calculateBestOption(){
        // buscar la mejor opcion de entre las rutas existentes o crear una nueva, maxima cantidad de rutas = numberOfVehicles
        return Pair.of(1,1);
    }
    
    private void addToRoute(int routePosition, int receptorId) {
        if (routes.size() >= routePosition) {
            throw new RuntimeException("No hay una ruta en la posicion: " + routePosition + ", receptor id: " + receptorId);
        }
        routes.get(routePosition).addLast(receptorId);
    }
    
    private double routeWeight(List<Integer> route) {
        double totalWeight = 0;
        for (Integer id: route) {
            totalWeight += weight(id);
        }
        return totalWeight/100;
    }
    
    /* para limpiar solucion vieja */
    private void clean() {
        routes.clear();
    }
    
    /* para evaluar soluciones, idem a FingProblem */
    public void evaluate(PermutationSolution<Integer> solution) {
        /* gCosto = ∑c(vi) (2.1) */
        double cost = 0.0;
        /* gTiempo = ∑(t(r)* u(r))/|R| */
        double time = 0.0;
        int fromNode = 0;
        double accumulatedTime = 0.0;
        int vehicleCapacity = 100;
        for (int i = 1; i < solution.getLength(); i++) {
            /* chequear si i esta en el intervalo de enteros reservado para identificadores de vehiculos */
            int thisNode = solution.getVariable(i);
            if (thisNode < numberOfVehicles) {
                cost += obtainCost(fromNode, 0);
                fromNode = 0;
                accumulatedTime = 0;
            } else {
                // chequear que tenga espacio para enviarle a ese receptor
                if (vehicleCapacity >= weight(thisNode)) {
                    vehicleCapacity -= weight(thisNode);
                } else {
                    // agregar coste de volver al centro de distribucion
                    cost += obtainCost(fromNode, 0);
                    accumulatedTime += obtainTime(fromNode, 0);
                    fromNode = 0;
                    // resetear capacidad
                    vehicleCapacity = 100 - weight(thisNode);
                }
                cost += obtainCost(fromNode, thisNode);
                accumulatedTime += obtainTime(fromNode, thisNode);
                time += accumulatedTime*urgency(MatrixLoader.toIndex(thisNode, numberOfVehicles));
                fromNode = thisNode;
            }
        }
        time = time / (numberOfVariables - numberOfVehicles);
        solution.setObjective(0, cost); // pesos, gasto de combustible
        solution.setObjective(1, time); // segundos, tiempo medio de llegada al receptor
    }
    
    public double obtainCost(int from, int to) {
        if (from == to) {
            return 0.0;
        }
        from = MatrixLoader.toIndex(from, numberOfVehicles);
        to = MatrixLoader.toIndex(to, numberOfVehicles);
        return distances[from][to]*(0.0104);
    }
    
    public double obtainTime(int from, int to) {
        if (from == to) {
            return 0.0;
        }
        from = MatrixLoader.toIndex(from, numberOfVehicles);
        to = MatrixLoader.toIndex(to, numberOfVehicles);
        return times[from][to];
    }
    
    public int urgency(int id) {
        if (id < 47) {
            return 3;
        }
        if (id < 113) {
            return 5;
        }
        return 7;
    }
    
    public int weight(int id) {
        if (id < 47) {
            return 15;
        }
        if (id < 113) {
            return 9;
        }
        return 4;
    }
}