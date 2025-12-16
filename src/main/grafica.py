# "resources/hv_matrices/instancia1_hv_matrix.csv"
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import friedmanchisquare, wilcoxon
from itertools import combinations

# =============================
# CONFIG
# =============================
CSV_PATH = "resources/hv_matrices/instancia3_hv_matrix.csv"   # ajustá si el nombre es otro
ALPHA = 0.05

# =============================
# LOAD DATA
# =============================
df = pd.read_csv(CSV_PATH)

configs = df.columns.tolist()
data = [df[c].values for c in configs]

# =============================
# FRIEDMAN
# =============================
stat, p_friedman = friedmanchisquare(*data)
print(f"p-value Friedman: {p_friedman:.4e}")

# =============================
# POST-HOC: WILCOXON + BONFERRONI
# =============================
m = len(configs)
comparisons = list(combinations(range(m), 2))
pvals = np.ones((m, m))

for i, j in comparisons:
    _, p = wilcoxon(df.iloc[:, i], df.iloc[:, j])
    p_adj = min(p * len(comparisons), 1.0)  # Bonferroni
    pvals[i, j] = p_adj
    pvals[j, i] = p_adj

# =============================
# RANKINGS (HV mayor = mejor)
# =============================
ranks = df.rank(axis=1, ascending=False)
mean_ranks = ranks.mean()

order = np.argsort(mean_ranks.values)
mean_ranks = mean_ranks.iloc[order]
configs = [configs[i] for i in order]
pvals = pvals[np.ix_(order, order)]

# =============================
# PLOT (LINE GRAPH)
# =============================
x = np.arange(len(configs))
y = mean_ranks.values

plt.figure(figsize=(10, 4))
plt.plot(x, y, marker="o")

# etiquetas
plt.xticks(x, configs, rotation=45, ha="right")
plt.ylabel("Ranking promedio (menor es mejor)")
plt.title("Friedman + Wilcoxon (Bonferroni)")

# líneas entre configuraciones SIN diferencia significativa
for i in range(len(configs)):
    for j in range(i + 1, len(configs)):
        if pvals[i, j] >= ALPHA:
            plt.plot(
                [x[i], x[j]],
                [y[i], y[j]],
                linewidth=2,
                alpha=0.4
            )

plt.grid(axis="y", linestyle="--", alpha=0.5)
plt.tight_layout()
plt.show()
