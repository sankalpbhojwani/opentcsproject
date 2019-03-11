package org.opentcs.util.persistence.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class LocationType {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "locationtype_generator")
	@SequenceGenerator(name = "locationtype_generator", sequenceName = "locationtype_id_seq", allocationSize = 1)
	Integer id;

	String name;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "locationType", targetEntity = AllowedOperation.class)
	List<AllowedOperation> allowedOperations;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "locationType", targetEntity = Property.class)
	List<Property> properties;

	@ManyToOne
	@JoinColumn(name = "model_id", referencedColumnName = "id")
	Model model;
	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public List<AllowedOperation> getAllowedOperations() {
		return allowedOperations;
	}

	public void setAllowedOperations(final List<AllowedOperation> allowedOperations) {
		this.allowedOperations = allowedOperations;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(final List<Property> properties) {
		this.properties = properties;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(final Model model) {
		this.model = model;
	}

}
