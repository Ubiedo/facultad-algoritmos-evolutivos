import os
import pandas as pd
import numpy as np
from pymoo.indicators.hv import HV

# ============================
# CONFIGURACIÓN GENERAL
# ============================
BASE_PATH = "resources/out"
OUTPUT_PATH = "resources/hv_matrices"
MARGIN = 0.05  # 5% de margen para el punto de referencia

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

    for conf in os.listdir(instancia_path):
        conf_path = os.path.join(instancia_path, conf)
        if not os.path.isdir(conf_path):
            continue

        for run_file in os.listdir(conf_path):
            if not run_file.endswith(".csv"):
                continue

            front = load_front(os.path.join(conf_path, run_file))
            current_max = front.max(axis=0)

            if worst is None:
                worst = current_max
            else:
                worst = np.maximum(worst, current_max)

    return worst * (1.0 + MARGIN)


def compute_hv_matrix(instancia_path, ref_point):
    """
    Calcula el hipervolumen de cada corrida y arma la matriz
    runs × configuraciones
    """
    hv_indicator = HV(ref_point=ref_point)
    records = []

    for conf in sorted(os.listdir(instancia_path)):
        conf_path = os.path.join(instancia_path, conf)
        if not os.path.isdir(conf_path):
            continue

        for run_file in sorted(os.listdir(conf_path)):
            if not run_file.endswith(".csv"):
                continue

            front = load_front(os.path.join(conf_path, run_file))
            hv_value = hv_indicator(front)

            records.append({
                "run": run_file,
                "config": conf,
                "hv": hv_value
            })

    df = pd.DataFrame(records)

    matrix = df.pivot_table(
        index="run",
        columns="config",
        values="hv"
    )

    return matrix


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
        OUTPUT_PATH, f"{instancia}_hv_matrix.csv"
    )
    hv_matrix.to_csv(output_csv)

    print(f"Matriz HV guardada en: {output_csv}")
