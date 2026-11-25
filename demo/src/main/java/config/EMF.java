package config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EMF {

    private static final EntityManagerFactory factory =
            Persistence.createEntityManagerFactory("MiniShopPU");

    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    public static void close() {
        if (factory.isOpen()) {
            factory.close();
        }
    }
}