import pandas as pd
import matplotlib.pyplot as plt

# Cargar CSVs
df1 = pd.read_csv("resources/out/ae.csv")
df2 = pd.read_csv("resources/out/greedys.csv")

# Graficar
plt.figure()
plt.scatter(df1["tiempo"], df1["costo"], label="Algoritmo Evolutivo", color="blue")
plt.scatter(df2["tiempo"], df2["costo"], label="Greedys", color="orange")

plt.xlabel("Tiempo")
plt.ylabel("Costo")
plt.title("Costo vs Tiempo")
plt.legend()
plt.show()
