/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpacontroller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Equipments;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import jpacontroller.exceptions.NonexistentEntityException;
import jpacontroller.exceptions.PreexistingEntityException;
import jpacontroller.exceptions.RollbackFailureException;
import model.Students;

/**
 *
 * @author Jn
 */
public class StudentsJpaController implements Serializable {

    public StudentsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Students students) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (students.getEquipmentsList() == null) {
            students.setEquipmentsList(new ArrayList<Equipments>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Equipments> attachedEquipmentsList = new ArrayList<Equipments>();
            for (Equipments equipmentsListEquipmentsToAttach : students.getEquipmentsList()) {
                equipmentsListEquipmentsToAttach = em.getReference(equipmentsListEquipmentsToAttach.getClass(), equipmentsListEquipmentsToAttach.getId());
                attachedEquipmentsList.add(equipmentsListEquipmentsToAttach);
            }
            students.setEquipmentsList(attachedEquipmentsList);
            em.persist(students);
            for (Equipments equipmentsListEquipments : students.getEquipmentsList()) {
                Students oldBorrowerOfEquipmentsListEquipments = equipmentsListEquipments.getBorrower();
                equipmentsListEquipments.setBorrower(students);
                equipmentsListEquipments = em.merge(equipmentsListEquipments);
                if (oldBorrowerOfEquipmentsListEquipments != null) {
                    oldBorrowerOfEquipmentsListEquipments.getEquipmentsList().remove(equipmentsListEquipments);
                    oldBorrowerOfEquipmentsListEquipments = em.merge(oldBorrowerOfEquipmentsListEquipments);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findStudents(students.getId()) != null) {
                throw new PreexistingEntityException("Students " + students + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Students students) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Students persistentStudents = em.find(Students.class, students.getId());
            List<Equipments> equipmentsListOld = persistentStudents.getEquipmentsList();
            List<Equipments> equipmentsListNew = students.getEquipmentsList();
            List<Equipments> attachedEquipmentsListNew = new ArrayList<Equipments>();
            for (Equipments equipmentsListNewEquipmentsToAttach : equipmentsListNew) {
                equipmentsListNewEquipmentsToAttach = em.getReference(equipmentsListNewEquipmentsToAttach.getClass(), equipmentsListNewEquipmentsToAttach.getId());
                attachedEquipmentsListNew.add(equipmentsListNewEquipmentsToAttach);
            }
            equipmentsListNew = attachedEquipmentsListNew;
            students.setEquipmentsList(equipmentsListNew);
            students = em.merge(students);
            for (Equipments equipmentsListOldEquipments : equipmentsListOld) {
                if (!equipmentsListNew.contains(equipmentsListOldEquipments)) {
                    equipmentsListOldEquipments.setBorrower(null);
                    equipmentsListOldEquipments = em.merge(equipmentsListOldEquipments);
                }
            }
            for (Equipments equipmentsListNewEquipments : equipmentsListNew) {
                if (!equipmentsListOld.contains(equipmentsListNewEquipments)) {
                    Students oldBorrowerOfEquipmentsListNewEquipments = equipmentsListNewEquipments.getBorrower();
                    equipmentsListNewEquipments.setBorrower(students);
                    equipmentsListNewEquipments = em.merge(equipmentsListNewEquipments);
                    if (oldBorrowerOfEquipmentsListNewEquipments != null && !oldBorrowerOfEquipmentsListNewEquipments.equals(students)) {
                        oldBorrowerOfEquipmentsListNewEquipments.getEquipmentsList().remove(equipmentsListNewEquipments);
                        oldBorrowerOfEquipmentsListNewEquipments = em.merge(oldBorrowerOfEquipmentsListNewEquipments);
                    }
                }
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
                Integer id = students.getId();
                if (findStudents(id) == null) {
                    throw new NonexistentEntityException("The students with id " + id + " no longer exists.");
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
            Students students;
            try {
                students = em.getReference(Students.class, id);
                students.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The students with id " + id + " no longer exists.", enfe);
            }
            List<Equipments> equipmentsList = students.getEquipmentsList();
            for (Equipments equipmentsListEquipments : equipmentsList) {
                equipmentsListEquipments.setBorrower(null);
                equipmentsListEquipments = em.merge(equipmentsListEquipments);
            }
            em.remove(students);
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

    public List<Students> findStudentsEntities() {
        return findStudentsEntities(true, -1, -1);
    }

    public List<Students> findStudentsEntities(int maxResults, int firstResult) {
        return findStudentsEntities(false, maxResults, firstResult);
    }

    private List<Students> findStudentsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Students.class));
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

    public Students findStudents(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Students.class, id);
        } finally {
            em.close();
        }
    }

    public int getStudentsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Students> rt = cq.from(Students.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
