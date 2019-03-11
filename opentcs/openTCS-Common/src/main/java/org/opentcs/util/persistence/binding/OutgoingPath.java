package org.opentcs.util.persistence.binding;

import static java.util.Objects.requireNonNull;
import static javax.persistence.GenerationType.IDENTITY;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
@Entity
@Table
public class OutgoingPath {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private String id;
	private String name = "";

	public String getId() {
		return id;
	}

	@XmlAttribute(required = true)
	public String getName() {
		return name;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public OutgoingPath setName(@Nonnull final String name) {
		requireNonNull(name, "name");
		this.name = name;
		return this;
	}
}