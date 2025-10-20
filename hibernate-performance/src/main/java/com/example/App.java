package com.example;

import com.example.service.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class App {
    public static void main(String[] args) {
        // Création de l'EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-performance");

        try {
            // Initialisation des données
            DataInitService dataInitService = new DataInitService(emf);
            dataInitService.initData();

            // Service de test de performance
            PerformanceTestService performanceTestService = new PerformanceTestService(emf);

            // Test 1: Problème N+1 sans optimisation
            System.out.println("\n\n=== TEST 1: PROBLÈME N+1 SANS OPTIMISATION ===");
            performanceTestService.testN1Problem();

            // Test 2: Résolution du problème N+1 avec JOIN FETCH
            System.out.println("\n\n=== TEST 2: RÉSOLUTION AVEC JOIN FETCH ===");
            performanceTestService.testJoinFetch();

            // Test 3: Résolution du problème N+1 avec Entity Graphs
            System.out.println("\n\n=== TEST 3: RÉSOLUTION AVEC ENTITY GRAPHS ===");
            performanceTestService.testEntityGraph();

            // Test 4: Test du cache de second niveau
            System.out.println("\n\n=== TEST 4: CACHE DE SECOND NIVEAU ===");
            performanceTestService.testSecondLevelCache();

            // Test 5: Comparaison des performances avec et sans cache
            System.out.println("\n\n=== TEST 5: COMPARAISON DES PERFORMANCES ===");
            performanceTestService.testPerformanceComparison();

            System.out.println("\n\n=== TEST 6: Cache strategy TEST ===");
            CacheStrategyTestService testService = new CacheStrategyTestService(emf);
            testService.testCachePerformance();


            System.out.println("\n\n=== TEST 6: Service de préchargement TEST ===");
            PreloadCacheService preload = new PreloadCacheService(emf);
            preload.preloadFrequentData();

            System.out.println("\n\n=== TEST 7: Mesurer l'impact des différentes optimisations sur un jeu de données ===");
            DataInitService dataInit = new DataInitService(emf);
            dataInit.initialiserDonneesMassives(500, 10, 50);

            BenchmarkService benchmark = new BenchmarkService(emf);
            benchmark.lancerBenchmarkComplet();

        } finally {
            // Fermeture de l'EntityManagerFactory
            emf.close();
        }
    }
}