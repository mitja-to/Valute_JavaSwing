/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.valuta;

import com.mycompany.valuta.exceptions.NonexistentEntityException;
import com.mycompany.valuta.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Mitja
 */
public class ValutaJpaController implements Serializable {

    public ValutaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Valuta valuta) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(valuta);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findValuta(valuta.getId()) != null) {
                throw new PreexistingEntityException("Valuta " + valuta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Valuta valuta) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            valuta = em.merge(valuta);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = valuta.getId();
                if (findValuta(id) == null) {
                    throw new NonexistentEntityException("The valuta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Valuta valuta;
            try {
                valuta = em.getReference(Valuta.class, id);
                valuta.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The valuta with id " + id + " no longer exists.", enfe);
            }
            em.remove(valuta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Valuta> findValutaEntities() {
        return findValutaEntities(true, -1, -1);
    }

    public List<Valuta> findValutaEntities(int maxResults, int firstResult) {
        return findValutaEntities(false, maxResults, firstResult);
    }

    private List<Valuta> findValutaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Valuta.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Valuta findValuta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Valuta.class, id);
        } finally {
            em.close();
        }
    }

    public int getValutaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Valuta> rt = cq.from(Valuta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
