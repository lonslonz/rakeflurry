package com.skplanet.rakeflurry.db;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.rakeflurry.collector.AppMetrics;

public class HiberUtil
{
    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;

    static
    {
        try 
        {

            Configuration configuration = new Configuration();
            configuration.configure();
            //configuration.addAnnotatedClass(HiveTable.class);
            //configuration.addAnnotatedClass(T2.class);
            serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        catch (HibernateException he)
        {
            System.err.println("Error creating Session: " + he);
            throw new ExceptionInInitializerError(he);
        }
    }

    public static SessionFactory getSessionFactory()
    {
        return sessionFactory;
    } 
    public static Session openSession() {
        return sessionFactory.openSession();
    }
    public static void update(Object obj, String msg) throws Exception {
        Logger logger = LoggerFactory.getLogger(HiberUtil.class);
        
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(obj);
            tx.commit();
            logger.debug(msg);
        } catch (Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
}