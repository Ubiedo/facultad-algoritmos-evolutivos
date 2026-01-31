package fing.gonzalez.otero.greedy;

import fing.gonzalez.otero.utils.MatrixLoader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
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
    private List<Triple<Double, Integer, Integer>> logs;
    
    public Time (int variables, int vehicles) {
        numberOfVariables = variables;
        numberOfVehicles = vehicles;
        routes = new ArrayList<>();
        logs = new ArrayList<>();
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
        // listas de los receptores separados por urgencia
        List<Integer> high = new ArrayList<>();
        List<Integer> mid = new ArrayList<>();
        List<Integer> low = new ArrayList<>();
        for (int receptorId = numberOfVehicles; receptorId < numberOfVariables; receptorId++) {
            if (urgency(MatrixLoader.toIndex(receptorId, numberOfVehicles)) == 7) {
                high.add(receptorId);
            } else if (urgency(MatrixLoader.toIndex(receptorId, numberOfVehicles)) == 5) {
                mid.add(receptorId);
            } else {
                low.add(receptorId);
            }
        }
        // ordenados por tiempo al CD
        high.sort(Comparator.comparingDouble(r -> obtainTime(0, r)));
        mid.sort(Comparator.comparingDouble(r -> obtainTime(0, r)));
        low.sort(Comparator.comparingDouble(r -> obtainTime(0, r)));
        /* establecer los cabeza de ruta */
        for (int i = 0; i < numberOfVehicles; i++) {
            if (high.size() > 0) {
                routes.get(i).add(high.remove(0));
            } else if (mid.size() > 0) {
                routes.get(i).add(mid.remove(0));
            } else if (low.size() > 0) {
                routes.get(i).add(low.remove(0));
            }
        }
        int position, receptor;
        Pair<Double, Integer> best = null, found;
        List<Integer> route = null;
        while (high.size() + mid.size() + low.size() > 0) {
            // tomar un receptor
            if (high.size() > 0) {
                receptor = high.remove(0);
            } else if (mid.size() > 0) {
                receptor = mid.remove(0);
            } else {
                receptor = low.remove(0);
            }
            // buscar mejor delta
            for (List<Integer> r: routes) {
                found = calculateDeltaAndBestPosition(receptor, r);
                if (found != null && (best == null || found.getLeft() < best.getLeft())) {
                    best = found;
                    route = r;
                }
            }
            if (best == null) {
                throw new RuntimeException("best = null");
            }
            // agregar el receptor en la posicion encontrada de la ruta obtenida
            if (route == null) {
                throw new RuntimeException("no se le asigno ninguna ruta");
            }
            route.add(best.getRight(), receptor);
            // guardo el log de que accion se realiza
            logs.addLast(Triple.of(best.getLeft(), best.getRight(), receptor));
            route = null;
            best = null;
        }
        int elementsInRoutes = numberOfVehicles;
        for (List<Integer> r: routes) {
            elementsInRoutes += r.size();
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
    private String duplicateInList(List<Integer> solution) {
        boolean [] indexs = new boolean[numberOfVariables];
        for (int i = 0; i < solution.size(); i++) {
            indexs[i] = false;
        }
        for (Integer index: solution) {
            if (indexs[index]) return "\u001B[33m" + "Hay duplicados!" + "\u001B[0m";
            indexs[index] = true;
        }
        return "\u001B[36m" + "Sin duplicados." + "\u001B[0m";
    }
    
    private Pair<Double, Integer> calculateDeltaAndBestPosition(int receptor, List<Integer> route){
        // devuelve el delta minimo, y la posicion dentro de la ruta donde sucede
        // donde delta = tiempo_ponderado_nuevo - tiempo_ponderado_viejo
        if (weight(MatrixLoader.toIndex(receptor, numberOfVehicles)) + routeWeight(route) > 100) return null;
        Pair<Double, Integer> result = Pair.of(Double.MAX_VALUE, 0);
        List<Integer> newRoute = new ArrayList<>(route);
        double delta;
        // iteramos el receptor en cada posible posicion de la ruta y devolvemos el par con mejor delta y en que posicion
        for (int pos = 0; pos <= newRoute.size(); pos++) {
            newRoute.add(pos, receptor);
            delta = routeTime(newRoute) - routeTime(route);
            if (delta < result.getLeft()) {
                result = Pair.of(delta, pos);
            }
            newRoute.remove(pos);
        }
        return result;
    }
    
    private double routeTime(List<Integer> route) {
        double totalTime = 0;
        double acumulatedTime = 0;
        int pre = 0;
        for (Integer id: route) {
            acumulatedTime += obtainTime(pre, id);
            totalTime += acumulatedTime * urgency(MatrixLoader.toIndex(id, numberOfVehicles));
            pre = id;
        }
        return totalTime;
    }
    
    private double routeWeight(List<Integer> route) {
        double totalWeight = 0;
        for (Integer id: route) {
            totalWeight += weight(MatrixLoader.toIndex(id, numberOfVehicles));
        }
        return totalWeight;
    }
    
    /* para limpiar solucion vieja */
    private void clean() {
        routes.clear();
        logs.clear();
        for (int vehicle = 0; vehicle < numberOfVehicles; vehicle++) {
            routes.add(new ArrayList<>());
        }
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
                vehicleCapacity = 100;
            } else {
                // chequear que tenga espacio para enviarle a ese receptor
                if (vehicleCapacity >= weight(MatrixLoader.toIndex(thisNode, numberOfVehicles))) {
                    vehicleCapacity -= weight(MatrixLoader.toIndex(thisNode, numberOfVehicles));
                } else {
                    // agregar coste de volver al centro de distribucion
                    cost += obtainCost(fromNode, 0);
                    accumulatedTime += obtainTime(fromNode, 0);
                    fromNode = 0;
                    // resetear capacidad
                    vehicleCapacity = 100 - weight(MatrixLoader.toIndex(thisNode, numberOfVehicles));
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
        return distances[from+1][to]*(0.0104);
    }
    
    public double obtainTime(int from, int to) {
        if (from == to) {
            return 0.0;
        }
        from = MatrixLoader.toIndex(from, numberOfVehicles);
        to = MatrixLoader.toIndex(to, numberOfVehicles);
        return times[from+1][to];
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