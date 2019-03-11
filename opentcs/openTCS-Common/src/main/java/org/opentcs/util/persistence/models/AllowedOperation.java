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
@Table
public class AllowedOperation {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "allowed_operation_generator")
	@SequenceGenerator(name = "allowed_operation_generator", sequenceName = "allowed_operation_seq", allocationSize = 1)
	Integer id;


	String name;

	@ManyToOne
	@JoinColumn(name = "locationType_id", referencedColumnName = "id")
	LocationType locationType;

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

  public LocationType getLocationType() {
    return locationType;
  }

  public void setLocationType(LocationType locationType) {
    this.locationType = locationType;
  }

}
