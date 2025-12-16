import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import networkx as nx
import itertools
from scipy.stats import wilcoxon

# =========================
# CARGAR MATRIZ HV
# =========================
df = pd.read_csv("resources/hv_matrices/instancia3_hv_matrix.csv")

configs = df.columns.tolist()
n_configs = len(configs)

# =========================
# RANKING PROMEDIO (Friedman)
# =========================
ranks = df.rank(axis=1, ascending=False)
avg_ranks = ranks.mean()

# =========================
# POST-HOC: WILCOXON + BONFERRONI
# =========================
pvals = pd.DataFrame(
    np.ones((n_configs, n_configs)),
    index=configs,
    columns=configs
)

m = n_configs * (n_configs - 1) / 2  # cantidad de comparaciones

for c1, c2 in itertools.combinations(configs, 2):
    stat, p = wilcoxon(df[c1], df[c2])
    p_adj = min(p * m, 1.0)  # Bonferroni
    pvals.loc[c1, c2] = p_adj
    pvals.loc[c2, c1] = p_adj

# =========================
# GRAFO DE COMPARACIONES
# =========================
G = nx.Graph()

for c in configs:
    G.add_node(c, rank=avg_ranks[c])

alpha = 0.05
for i in range(n_configs):
    for j in range(i + 1, n_configs):
        c1, c2 = configs[i], configs[j]
        significant = pvals.loc[c1, c2] < alpha
        G.add_edge(c1, c2, significant=significant)

# =========================
# POSICIONES CIRCULARES
# =========================
pos = nx.circular_layout(G)

# =========================
# DIBUJO
# =========================
plt.figure(figsize=(8, 8))

# Nodos
nx.draw_networkx_nodes(
    G, pos,
    node_size=900,
    node_color="white",
    edgecolors="black"
)

# Etiquetas (config + ranking)
labels = {c: f"{c}\n{avg_ranks[c]:.2f}" for c in configs}
nx.draw_networkx_labels(G, pos, labels, font_size=9)

# Aristas (rojo / azul)
for u, v, d in G.edges(data=True):
    if d["significant"]:
        color = "blue"
        width = 1.5
    else:
        color = "red"
        width = 3.0
    nx.draw_networkx_edges(G, pos, edgelist=[(u, v)], edge_color=color, width=width)

plt.title("Pairwise Comparisons (Wilcoxon + Bonferroni)")
plt.axis("off")
plt.tight_layout()
plt.show()
