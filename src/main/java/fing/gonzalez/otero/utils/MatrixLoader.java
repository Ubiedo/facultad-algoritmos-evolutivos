package fing.gonzalez.otero;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MatrixLoader {
	// TODO, despues quiza quitar esto y re hacer la matriz con los indices bien
	private static int[] order = {0,98,63,8,19,111,17,130,11,110,108,71,13,41,42,15,3,6,43,7,2,54,44,100,45,14,4,70,22,123,16,64,40,
			46,99,133,103,106,39,31,37,76,101,10,72,51,28,102,23,50,35,34,30,24,132,9,29,1,32,5,33,104,66,48,121,128,49,129,
			52,107,68,125,109,79,65,18,59,38,36,47,83,78,92,134,119,21,61,89,57,105,80,87,60,62,73,84,27,25,118,12,77,90,26,
			88,81,113,85,135,55,56,137,53,91,94,93,95,115,96,117,97,58,148,112,126,127,114,86,116,139,122,20,131,124,69,138,
			133,82,75,74,67,140,136,147,120,150,149,142,143,148,145,151,146,141,151,152,153};
	
	public static double[][] load(String resourcePath) throws Exception {
		List<double[]> filas = new ArrayList<>();
		try (InputStream is = MatrixLoader.class.getResourceAsStream(resourcePath);
			 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
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
	
	public static int matrixIndex (int id, int vehicles) {
		if (id-vehicles > 155) {
			return 0;
		}
		return order[id-vehicles];
	}
}
