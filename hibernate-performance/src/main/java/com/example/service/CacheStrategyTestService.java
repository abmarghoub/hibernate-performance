package com.example.service;

import com.example.model.Livre;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class CacheStrategyTestService {

    private final EntityManagerFactory emf;

    public CacheStrategyTestService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void testCachePerformance() {
        System.out.println("=== TEST STRATÉGIES DE CACHE ===");

        testStrategy("READ_ONLY");
        testStrategy("READ_WRITE");
        testStrategy("NONSTRICT_READ_WRITE");
    }

    private void testStrategy(String strategy) {
        long start = System.currentTimeMillis();

        EntityManager em1 = emf.createEntityManager();
        em1.getTransaction().begin();
        List<Livre> livres = em1.createQuery("SELECT l FROM Livre l", Livre.class).getResultList();
        em1.getTransaction().commit();
        em1.close();

        // 2e accès : doit venir du cache
        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        List<Livre> livres2 = em2.createQuery("SELECT l FROM Livre l", Livre.class).getResultList();
        em2.getTransaction().commit();
        em2.close();

        long duration = System.currentTimeMillis() - start;
        System.out.println(strategy + " → Durée totale : " + duration + " ms");
    }
}

