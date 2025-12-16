import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("resources/out/generaciones/instancia3.csv")

plt.figure(figsize=(8, 5))

plt.plot(df["generations"], df["best_min_cost"],
         marker="o", label="Min costo")

plt.plot(df["generations"], df["best_min_time"],
         marker="s", label="Min tiempo")

plt.xlabel("Cantidad de generaciones")
plt.ylabel("Valor del objetivo")
plt.title("Evolución de los extremos del frente de Pareto")
plt.legend()
plt.grid(True)

plt.tight_layout()
plt.show()
