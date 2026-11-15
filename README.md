# Réservation de matériel informatique

## Présentation

Ce projet consiste à développer une application Java permettant de gérer la réservation de matériel informatique par les étudiants.

L'application permet aux étudiants de réserver différents équipements informatiques, tels que des ordinateurs portables, des vidéoprojecteurs et des kits électroniques.

Les données sont stockées en mémoire à l'aide de listes Java, sans utilisation d'une base de données.

## Fonctionnalités

- Gestion des étudiants.
- Gestion des produits informatiques.
- Création des réservations.
- Vérification de la disponibilité du matériel.
- Validation des dates de réservation.
- Limitation de la durée de réservation à 7 jours.
- Limitation à 2 réservations par étudiant.
- Calcul automatique du montant de location et de la caution.
- Gestion des erreurs à l'aide d'exceptions.

## Technologies utilisées

- **Java** : langage de programmation.
- **JUnit 5** : tests unitaires.
- **Mockito** : simulation des dépendances dans les tests.
- **Maven** : gestion du projet et des dépendances.
- **JaCoCo** : mesure de la couverture des tests.

## Méthodologie

Le développement est réalisé selon l'approche **TDD (Test Driven Development)**, qui repose sur trois étapes :

1. **RED** : écrire un test qui échoue.
2. **GREEN** : développer le code nécessaire pour réussir le test.
3. **REFACTOR** : améliorer le code tout en conservant le comportement attendu.

## Exécution du projet

Pour exécuter les tests :

```bash
mvn test
```

Pour générer le rapport de couverture avec JaCoCo :

```bash
mvn verify
```

Le rapport de couverture est disponible dans :

```text
target/site/jacoco/index.html
```

## Règles de gestion

Le système applique plusieurs règles de gestion, notamment :

- Les informations obligatoires doivent être renseignées.
- La date de début doit être supérieure ou égale à la date actuelle.
- La date de fin doit être supérieure ou égale à la date de début.
- La durée maximale d'une réservation est de 7 jours.
- Un étudiant ne peut pas avoir plus de 2 réservations en cours ou futures.
- Un produit ne peut pas être réservé sur une période déjà occupée.
- Le montant total correspond au montant de location augmenté de la caution.

## Auteur

Projet académique réalisé dans le cadre d'un exercice de développement logiciel et de tests unitaires.
