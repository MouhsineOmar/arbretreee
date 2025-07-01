# Application d'Arbres de Décision - Migration JavaFX vers Spring Boot + React

## Vue d'ensemble

Cette application a été migrée d'une architecture JavaFX vers une architecture moderne Spring Boot + React. Elle permet d'entraîner et d'évaluer des modèles d'arbres de décision pour la classification de données.

## Architecture

### Backend (Spring Boot)
- **Port**: 8080
- **Technologies**: Spring Boot 3.2.0, Java 17, Maven
- **Endpoints REST**:
  - `POST /api/data/upload` - Upload de fichiers ARFF
  - `GET /api/data/status` - Statut des données chargées
  - `POST /api/model/train` - Entraînement de modèles
  - `GET /api/model/status` - Statut du modèle entraîné
  - `GET /api/model/algorithms` - Liste des algorithmes supportés

### Frontend (React)
- **Port**: 5173 (développement)
- **Technologies**: React 18, Vite, Tailwind CSS, shadcn/ui
- **Fonctionnalités**:
  - Interface d'upload de fichiers ARFF
  - Sélection d'algorithmes d'apprentissage
  - Configuration des paramètres
  - Visualisation des résultats et métriques

## Algorithmes Supportés

1. **ID3** (Iterative Dichotomiser 3)
   - Algorithme classique basé sur l'entropie
   - Adapté pour les attributs catégoriels

2. **CART** (Classification and Regression Trees)
   - Utilise l'indice de Gini
   - Supporte les attributs numériques et catégoriels

3. **Random Forest**
   - Ensemble d'arbres de décision
   - Paramètre configurable: nombre d'arbres
   - Meilleure précision grâce à l'agrégation

## Format de Données

L'application accepte les fichiers au format ARFF (Attribute-Relation File Format):

```arff
@relation weather

@attribute outlook {sunny, overcast, rainy}
@attribute temperature {hot, mild, cool}
@attribute humidity {high, normal}
@attribute windy {TRUE, FALSE}
@attribute play {yes, no}

@data
sunny,hot,high,FALSE,no
sunny,hot,high,TRUE,no
overcast,hot,high,FALSE,yes
...
```

## Installation et Démarrage

### Prérequis
- Java 17+
- Maven 3.6+
- Node.js 18+
- pnpm

### Backend
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
pnpm install
pnpm run dev --host
```

## Utilisation

1. **Chargement des données**
   - Accédez à l'onglet "Données"
   - Glissez-déposez ou sélectionnez un fichier ARFF
   - Vérifiez l'aperçu des données

2. **Entraînement du modèle**
   - Passez à l'onglet "Entraînement"
   - Sélectionnez un algorithme (ID3, CART, ou Random Forest)
   - Configurez les paramètres si nécessaire
   - Lancez l'entraînement

3. **Analyse des résultats**
   - Consultez l'onglet "Résultats"
   - Examinez les métriques de performance
   - Analysez la matrice de confusion
   - Téléchargez le rapport détaillé

## Métriques d'Évaluation

L'application fournit les métriques suivantes:

- **Précision globale**: Pourcentage d'instances correctement classifiées
- **Taux d'erreur**: Pourcentage d'instances mal classifiées
- **Précision par classe**: Précision pour chaque classe
- **Rappel par classe**: Sensibilité pour chaque classe
- **F-mesure**: Moyenne harmonique de la précision et du rappel
- **Matrice de confusion**: Tableau des prédictions vs réalité
- **Statistique Kappa**: Mesure de l'accord inter-observateurs

## Exemples de Fichiers

Des fichiers d'exemple sont fournis dans `backend/src/main/resources/samples/`:
- `weather.nominal.arff` - Données météorologiques
- `contact-lenses.arff` - Prescription de lentilles de contact
- `breast-cancer.arff` - Diagnostic de cancer du sein
- `cars.arff` - Évaluation de voitures

## Améliorations par rapport à l'Original

1. **Architecture moderne**: Séparation claire frontend/backend
2. **Interface responsive**: Compatible mobile et desktop
3. **API REST**: Facilite l'intégration avec d'autres systèmes
4. **Visualisations améliorées**: Graphiques interactifs avec Recharts
5. **Expérience utilisateur**: Interface moderne avec Tailwind CSS
6. **Déploiement facilité**: Applications web déployables séparément

## Développement

### Structure du Projet
```
decision-tree-app/
├── backend/                 # Application Spring Boot
│   ├── src/main/java/      # Code source Java
│   ├── src/main/resources/ # Ressources et fichiers d'exemple
│   └── pom.xml            # Configuration Maven
└── frontend/               # Application React
    ├── src/               # Code source React
    ├── public/            # Fichiers statiques
    └── package.json       # Configuration npm
```

### Tests

Les tests peuvent être effectués via:
- Backend: `mvn test`
- Frontend: `pnpm test`
- Tests d'intégration via l'interface web

## Déploiement

L'application peut être déployée sur différentes plateformes:
- Backend: JAR exécutable Spring Boot
- Frontend: Build statique déployable sur CDN/serveur web
- Conteneurisation possible avec Docker

## Support

Pour toute question ou problème, consultez:
- Les logs du backend dans la console Spring Boot
- Les logs du frontend dans la console du navigateur
- La documentation des algorithmes dans le code source

