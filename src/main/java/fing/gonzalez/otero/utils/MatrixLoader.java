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
	// instancias reducidas para calibracion
    public static int [] set1 = {1,2,4,6,10,13,16,20,23,26,29,34,35,38,41,42,46,47,51,54,55,58,61,63,65,68,72,75,78,79,83,84,85,86,91,94,99,109,110,114,116,119,122,132,138,142,145,148};
    public static int [] set2 = {5,9,12,15,17,19,22,25,28,31,33,37,40,44,49,50,53,57,60,64,66,70,71,77,81,88,90,93,95,96,103,106,107,108,112,113,117,121,124,128,130,131,134,137,140,141,147,151};
    // instancias para evaluacion,TODO, ahora los tres son iguales
    public static int [] set3 = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36
            ,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76
            ,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112
            ,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142
            ,143,144,145,146,147,148,149,150,151,152};
    public static int [] set4 = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36
            ,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76
            ,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112
            ,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142
            ,143,144,145,146,147,148,149,150,151,152};
    public static int [] set5 = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36
            ,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76
            ,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112
            ,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142
            ,143,144,145,146,147,148,149,150,151,152};

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
	
	public static int toIndex (int id) {
		for (int index = 0; index < order.length; index++) {
		    if (order[index] == id) {
		        return index;
		    }
		}
		throw new RuntimeException("El id no esta en el vector order: " + id);
	}
	
	public static int [] idsSet(int set) {
	    if (set == 1) {
	        return set1;
	    } else if (set == 2) {
	        return set2;
	    } else if (set == 3) {
	        return set3;
	    } else if (set == 4) {
	        return set4;
	    } else if (set == 5) {
	        return set5;
	    } else {
	        throw new RuntimeException("No hay un conjunto de ids asociado al pedido: " + set);
	    }
	}
}
