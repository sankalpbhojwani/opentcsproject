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
public class Path {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "path_generator")
	@SequenceGenerator(name = "path_generator", sequenceName = "path_id_seq", allocationSize = 1)
	Integer id;

	String name;

	String sourcePoint;

	String destinationPoint;

	String length;

	String routingCost;

	String maxVelocity;

	String maxReverseVelocity;

	Boolean locked;

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

	public String getSourcePoint() {
		return sourcePoint;
	}

	public void setSourcePoint(final String sourcePoint) {
		this.sourcePoint = sourcePoint;
	}

	public String getDestinationPoint() {
		return destinationPoint;
	}

	public void setDestinationPoint(final String destinationPoint) {
		this.destinationPoint = destinationPoint;
	}

	public String getLength() {
		return length;
	}

	public void setLength(final String length) {
		this.length = length;
	}

	public String getRoutingCost() {
		return routingCost;
	}

	public void setRoutingCost(final String routingCost) {
		this.routingCost = routingCost;
	}

	public String getMaxVelocity() {
		return maxVelocity;
	}

	public void setMaxVelocity(final String maxVelocity) {
		this.maxVelocity = maxVelocity;
	}

	public String getMaxReverseVelocity() {
		return maxReverseVelocity;
	}

	public void setMaxReverseVelocity(final String maxReverseVelocity) {
		this.maxReverseVelocity = maxReverseVelocity;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(final Boolean locked) {
		this.locked = locked;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(final Model model) {
		this.model = model;
	}

}
