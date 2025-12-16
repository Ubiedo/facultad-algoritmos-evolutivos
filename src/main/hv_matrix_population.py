import os
import pandas as pd
import numpy as np
from pymoo.indicators.hv import HV

# ============================
# CONFIGURACIÓN GENERAL
# ============================
BASE_PATH = "resources/out"
OUTPUT_PATH = "resources/hv_population"
MARGIN = 0.05  # 5% de margen para el punto de referencia

POPULATIONS = ["population-50", "population-100", "population-200"]

os.makedirs(OUTPUT_PATH, exist_ok=True)

# ============================
# FUNCIONES AUXILIARES
# ============================
def load_front(csv_path):
    """
    Carga un frente final.
    Columnas: costo, tiempo (minimización)
    """
    return pd.read_csv(csv_path)[["costo", "tiempo"]].values


def compute_reference_point(instancia_path):
    """
    Calcula el punto de referencia usando el peor costo y tiempo
    observados en TODA la instancia
    """
    worst = None

    for pop in POPULATIONS:
        pop_path = os.path.join(instancia_path, pop)
        if not os.path.isdir(pop_path):
            continue

        for run_file in os.listdir(pop_path):
            if not run_file.endswith(".csv"):
                continue

            front = load_front(os.path.join(pop_path, run_file))
            current_max = front.max(axis=0)

            if worst is None:
                worst = current_max
            else:
                worst = np.maximum(worst, current_max)

    return worst * (1.0 + MARGIN)


def compute_hv_matrix(instancia_path, ref_point):
    """
    Calcula el hipervolumen de cada corrida y arma la matriz
    ejecuciones × tamaños de población (sin columna run)
    """
    hv_indicator = HV(ref_point=ref_point)
    data = {}

    for pop in POPULATIONS:
        pop_path = os.path.join(instancia_path, pop)
        if not os.path.isdir(pop_path):
            continue

        hv_values = []

        for run_file in sorted(os.listdir(pop_path)):
            if not run_file.endswith(".csv"):
                continue

            front = load_front(os.path.join(pop_path, run_file))
            hv_values.append(hv_indicator(front))

        data[pop] = hv_values

    return pd.DataFrame(data)

# ============================
# PROCESO PRINCIPAL
# ============================
for instancia in sorted(os.listdir(BASE_PATH)):
    instancia_path = os.path.join(BASE_PATH, instancia)
    if not os.path.isdir(instancia_path):
        continue

    print(f"\nProcesando {instancia}...")

    # 1️⃣ Punto de referencia
    ref_point = compute_reference_point(instancia_path)
    print(f"Punto de referencia ({instancia}): {ref_point}")

    # 2️⃣ Matriz de hipervolumen
    hv_matrix = compute_hv_matrix(instancia_path, ref_point)

    # 3️⃣ Guardar CSV
    output_csv = os.path.join(
        OUTPUT_PATH, f"{instancia}_hv_population.csv"
    )
    hv_matrix.to_csv(output_csv)

    print(f"Matriz HV guardada en: {output_csv}")
