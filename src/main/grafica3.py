import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import friedmanchisquare

# =========================
# CARGAR MATRIZ HV
# =========================
df = pd.read_csv("resources/hv_population/instancia3_hv_population.csv")

configs = df.columns.tolist()

# =========================
# TEST DE FRIEDMAN
# =========================
stat, p_value = friedmanchisquare(
    *[df[c] for c in configs]
)

print("Test de Friedman")
print(f"Estadístico chi² = {stat:.6f}")
print(f"p-valor = {p_value:.6e}")

# =========================
# RANKINGS (mayor HV = mejor)
# =========================
ranks = df.rank(axis=1, ascending=False)
mean_ranks = ranks.mean()

# =========================
# COLORES POR POBLACIÓN
# =========================
colors = {
    "population-50":  "#4C72B0",  # azul
    "population-100": "#DD8452",  # naranja
    "population-200": "#55A868"   # verde
}

# =========================
# GRÁFICA DE FRECUENCIA DE RANKINGS
# =========================
fig, axes = plt.subplots(
    ncols=len(configs),
    figsize=(12, 5),
    sharey=True
)

for ax, config in zip(axes, configs):
    ax.hist(
        ranks[config],
        bins=np.arange(0.5, len(configs) + 1.5, 1),
        orientation="horizontal",
        color=colors.get(config, "gray"),
        edgecolor="black",      # línea negra separadora
        linewidth=1.0
    )

    ax.set_title(
        f"{config}\nMean Rank = {mean_ranks[config]:.2f}"
    )
    ax.set_xlabel("Frequency")
    ax.set_ylim(0.5, len(configs) + 0.5)

axes[0].set_ylabel("Rank")

fig.suptitle(
    "Related-Samples Friedman's Two-Way Analysis of Variance by Ranks",
    fontsize=12
)

plt.tight_layout(rect=[0, 0, 1, 0.93])
plt.show()
