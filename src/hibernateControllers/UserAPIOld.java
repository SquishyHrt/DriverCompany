package hibernateControllers;

import com.google.gson.GsonBuilder;
import model.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import utils.*;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;

public class UserAPIOld {
    private final EntityManagerFactory emf;

    public UserAPIOld(EntityManagerFactory entityManagerFactory) {
        this.emf = entityManagerFactory;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    private static void executePost(String url, String json) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);
            StringEntity postingString = new StringEntity(json);
            post.setEntity(postingString);
            post.setHeader("Content-type", "application/json");
            HttpResponse response = httpClient.execute(post);
//            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> T executeGet(String url, GsonBuilder gsonBuilder, Class<T> tClass) {
        T res = null;
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(url);
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            res = gsonBuilder.create().fromJson(responseString, tClass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public <T> void createObject(T entity, Class<T> entityClass) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            String url = "";
            if (entityClass.isAssignableFrom(Driver.class)) {
                url = "http://localhost:8080/user/createDriver";
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Driver.class, new DriverGsonSerializer());

            } else if (entityClass.isAssignableFrom(Manager.class)) {
                url = "http://localhost:8080/user/createManager";
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Manager.class, new ManagerGsonSerializer());

            } else if (entityClass.isAssignableFrom(Cargo.class)) {
                url = "http://localhost:8080/cargo/createCargo";
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Cargo.class, new CargoGsonSerializer());

            } else if (entityClass.isAssignableFrom(Destination.class)) {
                url = "http://localhost:8080/destination/createDestination";
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Destination.class, new DestinationGsonSerializer())
                        .registerTypeAdapter(Driver.class, new DriverGsonSerializer())
                        .registerTypeAdapter(Truck.class, new TruckGsonSerializer())
                        .registerTypeAdapter(Manager.class, new ManagerGsonSerializer());

            } else if (entityClass.isAssignableFrom(Comment.class)) {
                url = "http://localhost:8080/comment/createComment";
                gsonBuilder.registerTypeAdapter(Comment.class, new CommentGsonSerializer());

            } else if (entityClass.isAssignableFrom(Checkpoint.class)) {
                url = "http://localhost:8080/checkpoint/createCheckpoint";
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Checkpoint.class, new CheckpointGsonSerializer());

            } else if (entityClass.isAssignableFrom(Forum.class)) {
                url = "http://localhost:8080/forum/createForum";
                gsonBuilder.registerTypeAdapter(Forum.class, new ForumGsonSerializer());

            } else if (entityClass.isAssignableFrom(Truck.class)) {
                url = "http://localhost:8080/truck/createTruck";
                gsonBuilder.registerTypeAdapter(Truck.class, new TruckGsonSerializer());

            } else {
                url = "http://localhost:8080/error";
            }
            String json = gsonBuilder.create().toJson(entity);
            executePost(url, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void updateObject(T entity) {
        Class<T> entityClass = (Class<T>) entity.getClass();
        try {
            createObject(entity, entityClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void deleteObject(T entity) {
        Class<T> entityClass = (Class<T>) entity.getClass();
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            String url = "";
            if (entityClass.isAssignableFrom(Driver.class)) {
                url = "http://localhost:8080/user/deleteDriver";
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Driver.class, new DriverGsonSerializer());

            } else if (entityClass.isAssignableFrom(Manager.class)) {
                url = "http://localhost:8080/user/deleteManager";
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Manager.class, new ManagerGsonSerializer());

            } else if (entityClass.isAssignableFrom(Cargo.class)) {
                url = "http://localhost:8080/cargo/deleteCargo";
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Cargo.class, new CargoGsonSerializer());

            } else if (entityClass.isAssignableFrom(Destination.class)) {
                url = "http://localhost:8080/destination/deleteDestination";
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Destination.class, new DestinationGsonSerializer());

            } else if (entityClass.isAssignableFrom(Comment.class)) {
                url = "http://localhost:8080/comment/deleteComment";
                gsonBuilder.registerTypeAdapter(Comment.class, new CommentGsonSerializer());

            } else if (entityClass.isAssignableFrom(Forum.class)) {
                url = "http://localhost:8080/forum/deleteForum";
                gsonBuilder.registerTypeAdapter(Forum.class, new ForumGsonSerializer());

            } else if (entityClass.isAssignableFrom(Truck.class)) {
                url = "http://localhost:8080/truck/deleteTruck";
                gsonBuilder.registerTypeAdapter(Truck.class, new TruckGsonSerializer());

            } else {
                url = "http://localhost:8080/error";
            }
            String json = gsonBuilder.create().toJson(entity);
            executePost(url, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T getObjectById(Class<T> entity, int id) {
        T res = null;
        try {
            String url = "";
            GsonBuilder gsonBuilder = new GsonBuilder();
            if (entity.isAssignableFrom(Driver.class)) {
                url = "http://localhost:8080/user/getDriverById?id=" + id;
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Driver.class, new DriverGsonSerializer());

            } else if (entity.isAssignableFrom(Manager.class)) {
                url = "http://localhost:8080/user/getManagerById?id=" + id;
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Manager.class, new ManagerGsonSerializer());

            } else if (entity.isAssignableFrom(Cargo.class)) {
                url = "http://localhost:8080/cargo/getCargoById?id=" + id;
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Cargo.class, new CargoGsonSerializer());

            } else if (entity.isAssignableFrom(Destination.class)) {
                url = "http://localhost:8080/destination/getDestinationById?id=" + id;
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Destination.class, new DestinationGsonSerializer());

            } else if (entity.isAssignableFrom(Comment.class)) {
                url = "http://localhost:8080/comment/getCommentById?id=" + id;
                gsonBuilder.registerTypeAdapter(Comment.class, new CommentGsonSerializer());

            } else if (entity.isAssignableFrom(Checkpoint.class)) {
                url = "http://localhost:8080/checkpoint/getCheckpointById?id=" + id;
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Checkpoint.class, new CheckpointGsonSerializer());

            } else if (entity.isAssignableFrom(Forum.class)) {
                url = "http://localhost:8080/forum/getForumById?id=" + id;
                gsonBuilder.registerTypeAdapter(Forum.class, new ForumGsonSerializer());

            } else if (entity.isAssignableFrom(Truck.class)) {
                url = "http://localhost:8080/truck/getTruckById?id=" + id;
                gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                        .registerTypeAdapter(Truck.class, new TruckGsonSerializer());

            }
            res = executeGet(url, gsonBuilder, entity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
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
        EntityManager em = getEntityManager();
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
        EntityManager em = getEntityManager();
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
