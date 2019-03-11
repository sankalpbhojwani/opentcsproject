package org.opentcs.util.persistence.models;

import java.util.ArrayList;
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
public class Point {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "point_generator")
	@SequenceGenerator(name = "point_generator", sequenceName = "point_id_seq", allocationSize = 1)
	Integer id;

	String name;

	String xPosition;

	String yPosition;

	String zPosition;

	String vehicleOrientationAngle;

	String type;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "point", targetEntity = OutgoingPath.class)
	List<OutgoingPath> outGoingPaths = new ArrayList<OutgoingPath>();

	@ManyToOne
	@JoinColumn(name = "model_id", referencedColumnName = "id")
	Model model;

	public String getxPosition() {
		return xPosition;
	}

	public void setxPosition(final String xPosition) {
		this.xPosition = xPosition;
	}

	public String getyPosition() {
		return yPosition;
	}

	public void setyPosition(final String yPosition) {
		this.yPosition = yPosition;
	}

	public String getzPosition() {
		return zPosition;
	}

	public void setzPosition(final String zPosition) {
		this.zPosition = zPosition;
	}

	public String getVehicleOrientationAngle() {
		return vehicleOrientationAngle;
	}

	public void setVehicleOrientationAngle(final String vehicleOrientationAngle) {
		this.vehicleOrientationAngle = vehicleOrientationAngle;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public List<OutgoingPath> getOutGoingPaths() {
		return outGoingPaths;
	}

	public void setOutGoingPaths(final List<OutgoingPath> outGoingPaths) {
		this.outGoingPaths = outGoingPaths;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(final Model model) {
		this.model = model;
	}

}
