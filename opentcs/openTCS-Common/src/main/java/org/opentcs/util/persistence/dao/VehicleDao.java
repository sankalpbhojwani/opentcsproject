/*
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.util.persistence.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.opentcs.util.persistence.models.Vehicle;

/**
 *
 * @author a2z computer
 */
public class VehicleDao {
  
  protected EntityManager entityManager;
  @Inject
  	public VehicleDao(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}
  	public void saveInNewTransaction(final Vehicle object) {
		entityManager.getTransaction().begin();
		save(object);
		entityManager.getTransaction().commit();
	}
    	public void save(final Vehicle object) {
		entityManager.persist(object);
     
	}
       public Vehicle getVehicle(String name) {
         
        TypedQuery<Vehicle> getVehicleByNameQuery=entityManager.createQuery("select v from Vehicle v where v.name=:name", Vehicle.class);
        getVehicleByNameQuery.setParameter("name", name);
        return getVehicleByNameQuery.setParameter("name", name).getSingleResult();
    
	}
}
