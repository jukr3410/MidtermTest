/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpacontroller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import jpacontroller.exceptions.NonexistentEntityException;
import jpacontroller.exceptions.PreexistingEntityException;
import jpacontroller.exceptions.RollbackFailureException;
import model.Equipments;
import model.Students;

/**
 *
 * @author Jn
 */
public class EquipmentsJpaController implements Serializable {

    public EquipmentsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Equipments equipments) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Students borrower = equipments.getBorrower();
            if (borrower != null) {
                borrower = em.getReference(borrower.getClass(), borrower.getId());
                equipments.setBorrower(borrower);
            }
            em.persist(equipments);
            if (borrower != null) {
                borrower.getEquipmentsList().add(equipments);
                borrower = em.merge(borrower);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findEquipments(equipments.getId()) != null) {
                throw new PreexistingEntityException("Equipments " + equipments + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Equipments equipments) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Equipments persistentEquipments = em.find(Equipments.class, equipments.getId());
            Students borrowerOld = persistentEquipments.getBorrower();
            Students borrowerNew = equipments.getBorrower();
            if (borrowerNew != null) {
                borrowerNew = em.getReference(borrowerNew.getClass(), borrowerNew.getId());
                equipments.setBorrower(borrowerNew);
            }
            equipments = em.merge(equipments);
            if (borrowerOld != null && !borrowerOld.equals(borrowerNew)) {
                borrowerOld.getEquipmentsList().remove(equipments);
                borrowerOld = em.merge(borrowerOld);
            }
            if (borrowerNew != null && !borrowerNew.equals(borrowerOld)) {
                borrowerNew.getEquipmentsList().add(equipments);
                borrowerNew = em.merge(borrowerNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = equipments.getId();
                if (findEquipments(id) == null) {
                    throw new NonexistentEntityException("The equipments with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Equipments equipments;
            try {
                equipments = em.getReference(Equipments.class, id);
                equipments.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The equipments with id " + id + " no longer exists.", enfe);
            }
            Students borrower = equipments.getBorrower();
            if (borrower != null) {
                borrower.getEquipmentsList().remove(equipments);
                borrower = em.merge(borrower);
            }
            em.remove(equipments);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Equipments> findEquipmentsEntities() {
        return findEquipmentsEntities(true, -1, -1);
    }

    public List<Equipments> findEquipmentsEntities(int maxResults, int firstResult) {
        return findEquipmentsEntities(false, maxResults, firstResult);
    }

    private List<Equipments> findEquipmentsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Equipments.class));
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

    public Equipments findEquipments(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Equipments.class, id);
        } finally {
            em.close();
        }
    }

    public int getEquipmentsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Equipments> rt = cq.from(Equipments.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
