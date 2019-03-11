/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.util.persistence.binding;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.PROPERTY)
@MappedSuperclass
public class PlantModelElementTO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name = "";
	@OneToMany
	private List<PropertyTO> properties = new ArrayList<>();

	@XmlAttribute
	@XmlSchemaType(name = "unsignedInt")
	public Long getId() {
		return id;
	}

	public PlantModelElementTO setId(final Long id) {
		this.id = id;
		return this;
	}

	@XmlAttribute(required = true)
	public String getName() {
		return name;
	}

	public PlantModelElementTO setName(@Nonnull final String name) {
		requireNonNull(name, "name");
		this.name = name;
		return this;
	}

	@XmlElement(name = "property")
	public List<PropertyTO> getProperties() {
		return properties;
	}

	public PlantModelElementTO setProperties(@Nonnull final List<PropertyTO> properties) {
		requireNonNull(properties, "properties");
		this.properties = properties;
		return this;
	}
}
