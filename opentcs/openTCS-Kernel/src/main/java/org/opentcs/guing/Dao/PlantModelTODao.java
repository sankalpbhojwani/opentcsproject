package org.opentcs.guing.Dao;

import javax.persistence.EntityManager;

import org.opentcs.util.persistence.models.XmlModel;

import com.google.inject.Inject;

public class PlantModelTODao {

	protected EntityManager entityManager;

	@Inject
	public PlantModelTODao(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void saveInNewTransaction(final XmlModel object) {
		entityManager.getTransaction().begin();
		save(object);
		entityManager.getTransaction().commit();
	}

	public void save(final XmlModel object) {
		entityManager.persist(object);
	}

	public XmlModel getObject() {
		return entityManager.createQuery("select e from XmlModel e ", XmlModel.class).getSingleResult();
	}

}
