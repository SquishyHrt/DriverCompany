package hibernateControllers;

import model.Driver;
import model.Manager;
import model.User;
import utils.GlobalUtils;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class UserHib {
    private final EntityManagerFactory emf;
    private EntityManager em;

    public UserHib(EntityManagerFactory entityManagerFactory) {
        this.emf = entityManagerFactory;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public <T> void createObject(T entity, Class<T> entityClass) {
        EntityManager em = getEntityManager();
        try {
            // check if the entity (user) already exists
            if (entityClass.isAssignableFrom(Driver.class) || entityClass.isAssignableFrom(Manager.class) || entityClass.isAssignableFrom(User.class)) {
                if (checkUserDuplicatesDB((User) entity, entityClass)) {
                    return;
                }
            }

            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public <T> void updateObject(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public <T> void deleteObject(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.merge(entity));
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null)
                em.close();
        }
    }

    public <T> T getObjectById(Class<T> entity, int id) {
        EntityManager em = getEntityManager();
        T object = null;
        try {
            em.getTransaction().begin();
            object = em.find(entity, id);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null)
                em.close();
        }
        return object;
    }

    public <T> List<T> getAllRecords(Class<T> entityClass) {
        EntityManager em = getEntityManager();
        List<T> res = null;
        try {
            em.getTransaction().begin();
            CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(entityClass);
            criteriaQuery.select(criteriaQuery.from(entityClass));
            res = em.createQuery(criteriaQuery).getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return res;
    }

    public User findUserByCredentials(String login, String password) {
        em = getEntityManager();

        // First try to find the user in the Driver table
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Driver> cqDriver = cb.createQuery(Driver.class);
        Root<Driver> rootDriver = cqDriver.from(Driver.class);
        cqDriver.select(rootDriver).where(cb.and(cb.equal(rootDriver.get("login"), login), cb.equal(rootDriver.get("password"), password)));
        TypedQuery<Driver> qDriver = em.createQuery(cqDriver);

        try {
            return qDriver.getSingleResult();
        } catch (NoResultException e) {
            // User not found in Driver table, try to find it in Manager table
            CriteriaQuery<Manager> cqManager = cb.createQuery(Manager.class);
            Root<Manager> rootManager = cqManager.from(Manager.class);
            cqManager.select(rootManager).where(cb.and(cb.equal(rootManager.get("login"), login), cb.equal(rootManager.get("password"), password)));
            TypedQuery<Manager> qManager = em.createQuery(cqManager);

            try {
                return qManager.getSingleResult();
            } catch (NoResultException ex) {
                // User not found in Manager table, return null
                return null;
            }
        } finally {
            em.close();
        }
    }

    // Return false if there is no duplicates. Else return true;
    public <T> boolean checkUserDuplicatesDB(User user, Class<T> entityClass) {
        em = getEntityManager();
        try {
            String className = entityClass.getSimpleName();
            String queryStr = "SELECT e FROM " + className + " e WHERE e.login = :login";
            Query query = em.createQuery(queryStr);
            query.setParameter("login", user.getLogin());
            List<T> entities = query.getResultList();
            if (!entities.isEmpty()) {
                GlobalUtils.createError("Error", "This " + className + " login already exists");
                return true;
            }
        } catch (NoResultException nr) {
            nr.printStackTrace();
        }

        em.close();
        return false;
    }
}
