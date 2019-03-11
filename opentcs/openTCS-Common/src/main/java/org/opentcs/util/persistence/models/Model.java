package org.opentcs.util.persistence.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
public class Model {

	@Id
	Integer id;

	String version;

	String name;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "model", targetEntity = Point.class)
	List<Point> points = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "model", targetEntity = Path.class)
	List<Path> paths = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "model", targetEntity = Vehicle.class)
	List<Vehicle> vehicles = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "model", targetEntity = LocationType.class)
	List<LocationType> locationTypes = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "model", targetEntity = Location.class)
	List<Location> locations = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "model", targetEntity = Block.class)
	List<Block> blocks = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "model", targetEntity = VisualLayout.class)
	List<VisualLayout> visualLayouts = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public List<Path> getPaths() {
		return paths;
	}

	public void setPaths(final List<Path> paths) {
		this.paths = paths;
	}

	public List<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(final List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

	public List<LocationType> getLocationTypes() {
		return locationTypes;
	}

	public void setLocationTypes(final List<LocationType> locationTypes) {
		this.locationTypes = locationTypes;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(final List<Location> locations) {
		this.locations = locations;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(final List<Block> blocks) {
		this.blocks = blocks;
	}

	public List<VisualLayout> getVisualLayouts() {
		return visualLayouts;
	}

	public void setVisualLayouts(final List<VisualLayout> visualLayouts) {
		this.visualLayouts = visualLayouts;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(final List<Point> points) {
		this.points = points;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

  @Override
  public String toString() {
    return "Model{" + "id=" + id + ", version=" + version + ", name=" + name + ", points=" + points + ", paths=" + paths + ", vehicles=" + vehicles + ", locationTypes=" + locationTypes + ", locations=" + locations + ", blocks=" + blocks + ", visualLayouts=" + visualLayouts + '}';
  }

}
