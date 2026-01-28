package fing.gonzalez.otero.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MatrixLoader {
	public static int [] order = {0,57,20,16,26,59,17,19,3,55,43,8,99,12,25,15,30,6,75,4,130,85,28,48,53,97,102,96,46,56,52
			,39,58,60,51,50,78,40,77,38,32,13,14,18,22,24,33,79,63,66,49,45,68,111,21,108,109,88,120,76,92,86,93,2,31,74,62,138
			,70,133,27,11,44,94,137,136,41,100,81,73,90,104,135,80,95,106,126,91,103,87,101,112,82,114,113,115,117,119,1,34,23
			,42,47,36,61,89,37,69,10,72,9,5,122,105,125,116,127,118,98,84,142,64,129,29,132,71,123,124,65,67,7,131,54,35,83,107
			,140,110,134,128,139,150,145,146,147,148,149,141,121,144,143,151,152};
	
	public static double[][] load(String resourcePath) throws Exception {
		List<double[]> filas = new ArrayList<>();
		InputStream is = MatrixLoader.class.getClassLoader().getResourceAsStream(resourcePath);
		if (is == null) {
			throw new RuntimeException("No se encontró el recurso: " + resourcePath);
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split(",");
				double[] fila = new double[partes.length];
				for (int i = 0; i < partes.length; i++) {
					fila[i] = Double.parseDouble(partes[i]);
				}
				filas.add(fila);
			}
		}
		return filas.toArray(new double[0][]);
	}
	
	public static int toIndex (int id, int vehicles) {
		if (id == 0) {
			return 0; // es el Centro de distribucion
		}
		if (id-vehicles > 152 || id-vehicles < 0) {
			throw new RuntimeException("El id no esta en el rango de la matriz: " + id + ", " + vehicles);
		}
		/*
		int index = 0;
		while (order[index] != id-vehicles) {
			index++;
		}
		*/
		return id - vehicles;
	}
	
	public static int fromIndex(int index, int vehicles) {
		if (index < 0 || index >= order.length) {
			throw new RuntimeException("El indice no esta en el rango de la matriz: " + index + ", " + vehicles);
		}
		return order[index]+vehicles;
	}
}
