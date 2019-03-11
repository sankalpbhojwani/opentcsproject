package org.opentcs.guing.application;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.opentcs.util.persistence.dao.PlantModelTODao;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class DbModule extends AbstractModule {

	private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE = new ThreadLocal<EntityManager>();

	@Override
	public void configure() {
		bind(PlantModelTODao.class);
	}

	@Provides
	@Singleton
	public EntityManagerFactory provideEntityManagerFactory() {
		final Map<String, String> properties = new HashMap<String, String>();
		properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
		properties.put("hibernate.connection.url", "jdbc:postgresql:fmstest");
		properties.put("hibernate.connection.username", "postgres");
		properties.put("hibernate.connection.password", "trust");
		properties.put("hibernate.connection.pool_size", "1");
		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		properties.put("hibernate.hbm2ddl.auto", "update");
		return Persistence.createEntityManagerFactory("db-manager", properties);
	}

	@Provides
	public EntityManager provideEntityManager(final EntityManagerFactory entityManagerFactory) {
		EntityManager entityManager = ENTITY_MANAGER_CACHE.get();
		if (entityManager == null) {
			ENTITY_MANAGER_CACHE.set(entityManager = entityManagerFactory.createEntityManager());
		}
		return entityManager;
	}

}
