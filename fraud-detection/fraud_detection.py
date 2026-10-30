"""Détection de fraude bancaire avec une régression logistique.

Lancement : python fraud_detection.py
Résultats : dossier results/
"""
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from sklearn.compose import ColumnTransformer
from sklearn.impute import SimpleImputer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import (
    ConfusionMatrixDisplay,
    PrecisionRecallDisplay,
    average_precision_score,
    confusion_matrix,
    f1_score,
    precision_recall_curve,
    precision_score,
    recall_score,
)
from sklearn.model_selection import StratifiedKFold, cross_val_predict, train_test_split
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler

BASE_DIR = Path(__file__).resolve().parent
RESULTS_DIR = BASE_DIR / "results"
RANDOM_STATE = 42

# 1. Chargement des données
data = pd.read_csv(BASE_DIR / "data" / "bank_transactions.csv")
print(f"Transactions : {len(data)}")

# 2. Proportion de fraudes et valeurs manquantes
print(f"Fraudes : {data['is_fraud'].sum()} ({data['is_fraud'].mean():.2%})")
print("Valeurs manquantes :")
print(data.isna().sum().to_string())

# 3 et 4. Séparation stratifiée, sans les identifiants
x = data.drop(columns=["is_fraud", "transaction_id", "account_id"])
y = data["is_fraud"]
x_train, x_test, y_train, y_test = train_test_split(
    x, y, test_size=0.2, stratify=y, random_state=RANDOM_STATE
)
print(f"Entraînement : {len(x_train)} lignes, test : {len(x_test)} lignes dont {y_test.sum()} fraudes")

# 5. Pipeline : tout est ajusté sur l'entraînement uniquement
numeric = Pipeline([
    ("imputation", SimpleImputer(strategy="median")),
    ("normalisation", StandardScaler()),
])
categorical = Pipeline([
    ("imputation", SimpleImputer(strategy="constant", fill_value="INCONNU")),
    ("encodage", OneHotEncoder(handle_unknown="ignore")),
])
preprocessing = ColumnTransformer([
    ("num", numeric, ["amount", "hour", "transactions_last_24h", "average_amount_last_7d"]),
    ("cat", categorical, ["country", "merchant_category"]),
])

# 6. Modèle simple, avec un poids plus fort pour la classe rare
model = Pipeline([
    ("pretraitement", preprocessing),
    ("modele", LogisticRegression(class_weight="balanced", max_iter=2000)),
])

# Le seuil est choisi par validation croisée sur l'entraînement, jamais sur le test
folds = StratifiedKFold(n_splits=5, shuffle=True, random_state=RANDOM_STATE)
train_proba = cross_val_predict(model, x_train, y_train, cv=folds, method="predict_proba")[:, 1]
precision, recall, thresholds = precision_recall_curve(y_train, train_proba)
f1_values = 2 * precision[:-1] * recall[:-1] / np.maximum(precision[:-1] + recall[:-1], 1e-12)
threshold = thresholds[np.argmax(f1_values)]
print(f"Average precision en validation croisée : {average_precision_score(y_train, train_proba):.3f}")
print(f"Seuil choisi sur l'entraînement : {threshold:.3f}")

print("\nEffet du seuil (entraînement) :")
for value in [0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8]:
    predicted = train_proba >= value
    print(
        f"  seuil {value:.1f} : précision {precision_score(y_train, predicted, zero_division=0):.3f}, "
        f"rappel {recall_score(y_train, predicted):.3f}, alertes {predicted.sum()}"
    )

# 7. Évaluation finale sur le test
model.fit(x_train, y_train)
test_proba = model.predict_proba(x_test)[:, 1]
test_predicted = test_proba >= threshold
matrix = confusion_matrix(y_test, test_predicted)

print("\nÉvaluation sur le test, classe fraude :")
print(f"  Précision : {precision_score(y_test, test_predicted):.3f}")
print(f"  Rappel : {recall_score(y_test, test_predicted):.3f}")
print(f"  F1-score : {f1_score(y_test, test_predicted):.3f}")
print(f"  Average precision : {average_precision_score(y_test, test_proba):.3f} (hasard : {y_test.mean():.3f})")
print("Matrice de confusion [[normales OK, fausses alertes], [fraudes manquées, fraudes détectées]] :")
print(matrix)
print(f"Accuracy : {(test_predicted == y_test).mean():.2%}")
print(f"Accuracy d'un modèle qui ne détecte jamais rien : {1 - y_test.mean():.2%}")

# Graphiques pour le rapport
RESULTS_DIR.mkdir(exist_ok=True)
ConfusionMatrixDisplay(matrix, display_labels=["Normale", "Fraude"]).plot(cmap="Blues", colorbar=False)
plt.title(f"Matrice de confusion (seuil {threshold:.2f})")
plt.xlabel("Prédiction")
plt.ylabel("Réalité")
plt.tight_layout()
plt.savefig(RESULTS_DIR / "matrice_confusion.png", dpi=150)

PrecisionRecallDisplay.from_predictions(y_test, test_proba, name="Régression logistique", plot_chance_level=True)
plt.title("Courbe précision-rappel (test)")
plt.xlabel("Rappel")
plt.ylabel("Précision")
plt.legend(["Régression logistique", "Hasard"], loc="upper right")
plt.tight_layout()
plt.savefig(RESULTS_DIR / "courbe_precision_rappel.png", dpi=150)
