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
public class OutgoingPath {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "outgoingpath_generator")
	@SequenceGenerator(name = "outgoingpath_generator", sequenceName = "outgoingpath_id_seq", allocationSize = 1)
	Integer id;

	String name;

	@ManyToOne
	@JoinColumn(name = "point_id", referencedColumnName = "id")
	Point point;

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

	public Point getPoint() {
		return point;
	}

	public void setPoint(final Point point) {
		this.point = point;
	}

}
