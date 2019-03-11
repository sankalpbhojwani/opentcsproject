package org.opentcs.util.persistence.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "Property")
public class Property {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "property_generator")
	@SequenceGenerator(name = "property_generator", sequenceName = "property_id_seq", allocationSize = 1)
	Integer id;

	String name;

	String value;

	@ManyToOne
	@JoinColumn(name = "modelLayoutElement_id", referencedColumnName = "id")
	ModelLayoutElement modelLayoutElement;

	@ManyToOne
	@JoinColumn(name = "locationType_id", referencedColumnName = "id")
	LocationType locationType;

	@ManyToOne
	@JoinColumn(name = "visualLayout_id", referencedColumnName = "id")
	VisualLayout visualLayout;

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

	public String getValue() {
		return value;
	}

	public ModelLayoutElement getModelLayoutElement() {
		return modelLayoutElement;
	}

	public void setModelLayoutElement(final ModelLayoutElement modelLayoutElement) {
		this.modelLayoutElement = modelLayoutElement;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(final LocationType locationType) {
		this.locationType = locationType;
	}

	public VisualLayout getVisualLayout() {
		return visualLayout;
	}

	public void setVisualLayout(final VisualLayout visualLayout) {
		this.visualLayout = visualLayout;
	}
}
