package com.example.service;

import com.example.model.Auteur;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class BenchmarkService {

    private final EntityManagerFactory emf;

    public BenchmarkService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private long mesurer(Runnable operation) {
        long start = System.currentTimeMillis();
        operation.run();
        return System.currentTimeMillis() - start;
    }

    public void lancerBenchmarkComplet() {
        System.out.println("\n=== BENCHMARK OPTIMISATIONS JPA/HIBERNATE ===");

        System.out.println("1 Sans optimisation (lazy, N+1)");
        long t1 = mesurer(this::testSansOptimisation);
        System.out.println("Durée : " + t1 + " ms\n");

        System.out.println("2 Avec JOIN FETCH");
        long t2 = mesurer(this::testJoinFetch);
        System.out.println("Durée : " + t2 + " ms\n");

        System.out.println("3 Avec EntityGraph");
        long t3 = mesurer(this::testEntityGraph);
        System.out.println("Durée : " + t3 + " ms\n");

        System.out.println("4 Avec cache de second niveau");
        long t4 = mesurer(this::testCacheSecondNiveau);
        System.out.println("Durée : " + t4 + " ms\n");

        System.out.println("5 Après préchargement des données");
        long t5 = mesurer(this::testApresPrechargement);
        System.out.println("Durée : " + t5 + " ms\n");

        System.out.println("=== Résumé ===");
        System.out.printf("""
            Sans optimisation : %d ms
            JOIN FETCH : %d ms
            EntityGraph : %d ms
            Cache 2e niveau : %d ms
            Préchargement : %d ms
            """, t1, t2, t3, t4, t5);
    }

    private void testSansOptimisation() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<Auteur> auteurs = em.createQuery("SELECT a FROM Auteur a", Auteur.class).getResultList();
        for (Auteur a : auteurs) {
            a.getLivres().size(); // forcer le chargement lazy
        }
        em.getTransaction().commit();
        em.close();
    }

    private void testJoinFetch() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("SELECT DISTINCT a FROM Auteur a JOIN FETCH a.livres", Auteur.class).getResultList();
        em.getTransaction().commit();
        em.close();
    }

    private void testEntityGraph() {
        EntityManager em = emf.createEntityManager();
        EntityGraph<Auteur> graph = em.createEntityGraph(Auteur.class);
        graph.addSubgraph("livres");
        em.getTransaction().begin();
        em.createQuery("SELECT a FROM Auteur a", Auteur.class)
                .setHint("javax.persistence.loadgraph", graph)
                .getResultList();
        em.getTransaction().commit();
        em.close();
    }

    private void testCacheSecondNiveau() {
        // 1er accès = DB
        testJoinFetch();
        // 2e accès = cache
        testJoinFetch();
    }

    private void testApresPrechargement() {
        PreloadCacheService preload = new PreloadCacheService(emf);
        preload.preloadFrequentData();
        testCacheSecondNiveau();
    }
}

