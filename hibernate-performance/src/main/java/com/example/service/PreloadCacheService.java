package com.example.service;

import javax.persistence.*;
import java.util.List;

public class PreloadCacheService {

    private final EntityManagerFactory emf;

    public PreloadCacheService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void preloadFrequentData() {
        System.out.println("=== PRÉCHARGEMENT DU CACHE ===");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Charger toutes les catégories (READ_ONLY → parfait pour préchargement)
        List<?> categories = em.createQuery("SELECT c FROM Categorie c").getResultList();
        System.out.println("Catégories préchargées : " + categories.size());

        // Charger les auteurs avec leurs livres (JOIN FETCH pour pré-remplir le cache)
        List<?> auteurs = em.createQuery(
                "SELECT DISTINCT a FROM Auteur a JOIN FETCH a.livres"
        ).getResultList();
        System.out.println("Auteurs + livres préchargés : " + auteurs.size());

        //  Charger les livres récents ou les plus consultés
        List<?> livres = em.createQuery(
                        "SELECT l FROM Livre l ORDER BY l.id DESC"
                ).setMaxResults(20)
                .getResultList();
        System.out.println("Livres récents préchargés : " + livres.size());

        em.getTransaction().commit();
        em.close();

        System.out.println("=== CACHE PRÉCHARGÉ AVEC SUCCÈS ===");
    }
}
