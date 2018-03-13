package com.vgaw.hibernate.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
	private static final SessionFactory sessionFactory = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {
		try {
			// ╪сть/hibernate.properties
			Configuration cfg = new Configuration();
			// ╪сть/hibernate.cfg.xml
			cfg.configure();
			//factory = cfg.buildSessionFactory();
			ServiceRegistry serviceRegistryBuilder = new StandardServiceRegistryBuilder()
					.applySettings(cfg.getProperties()).build();
			return cfg.buildSessionFactory(serviceRegistryBuilder);
		}
		catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
