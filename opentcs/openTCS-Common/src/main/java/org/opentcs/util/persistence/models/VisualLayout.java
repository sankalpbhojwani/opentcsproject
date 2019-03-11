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
public class VisualLayout {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "visual_layout_generator")
	@SequenceGenerator(name = "visual_layout_generator", sequenceName = "visuallayout_id_seq", allocationSize = 1)
	Integer id;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "visualLayout", targetEntity = ModelLayoutElement.class)
	List<ModelLayoutElement> modelLayoutElements;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "visualLayout", targetEntity = Property.class)
	List<Property> properties;

	String name;

	String scaleX;

	String scaleY;

	@ManyToOne
	@JoinColumn(name = "model_id", referencedColumnName = "id")
	Model model;

	public List<ModelLayoutElement> getModelLayoutElements() {
		return modelLayoutElements;
	}

	public void setModelLayoutElements(final List<ModelLayoutElement> modelLayoutElements) {
		this.modelLayoutElements = modelLayoutElements;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getScaleX() {
		return scaleX;
	}

	public void setScaleX(final String scaleX) {
		this.scaleX = scaleX;
	}

	public String getScaleY() {
		return scaleY;
	}

	public void setScaleY(final String scaleY) {
		this.scaleY = scaleY;
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
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
