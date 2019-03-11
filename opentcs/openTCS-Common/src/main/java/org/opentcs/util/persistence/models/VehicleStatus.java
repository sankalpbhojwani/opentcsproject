/*
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.util.persistence.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author a2z computer
 */
@Entity
@Table
public class VehicleStatus {
  @Id@GeneratedValue(strategy = GenerationType.AUTO, generator = "server_property_generator")
  @SequenceGenerator(name = "server_property_generator", sequenceName = "serverproperty_id_seq", allocationSize = 1)
   Integer id;
  
  
 @OneToOne
 @JoinColumn(name="vehicle_id",referencedColumnName = "id")
 private Vehicle vehicle; 
 
 private String TruckName;
 private String EngineTemp;
 private String EngineFluid;
 private String InletExhaust;
 private String FuelEco;
 private String CruiseControl;
 private String VehicleID;
 private String ElectricalPower;
 private String AmbientConditions;
 private String EngineHours;
 private String FanDrive;
 private String EngineFluid2;
 private String IdleOperation;
 private String VehicleDistance;
 private String IdleShutdown;
 private String TimeDate;
 private String VehicleHours;
 private String FuelConsumption;
 private String WaterInFuelInd;

     
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }
  public String getTruckName() {
    return TruckName;
  }

  public void setTruckName(String TruckName) {
    this.TruckName = TruckName;
  }

  public String getEngineTemp() {
    return EngineTemp;
  }

  public void setEngineTemp(String EngineTemp) {
    this.EngineTemp = EngineTemp;
  }

  public String getEngineFluid() {
    return EngineFluid;
  }

  public void setEngineFluid(String EngineFluid) {
    this.EngineFluid = EngineFluid;
  }

  public String getInletExhaust() {
    return InletExhaust;
  }

  public void setInletExhaust(String InletExhaust) {
    this.InletExhaust = InletExhaust;
  }

  public String getFuelEco() {
    return FuelEco;
  }

  public void setFuelEco(String FuelEco) {
    this.FuelEco = FuelEco;
  }

  public String getCruiseControl() {
    return CruiseControl;
  }

  public void setCruiseControl(String CruiseControl) {
    this.CruiseControl = CruiseControl;
  }

  public String getVehicleID() {
    return VehicleID;
  }

  public void setVehicleID(String VehicleID) {
    this.VehicleID = VehicleID;
  }

  public String getElectricalPower() {
    return ElectricalPower;
  }

  public void setElectricalPower(String ElectricalPower) {
    this.ElectricalPower = ElectricalPower;
  }

  public String getAmbientConditions() {
    return AmbientConditions;
  }

  public void setAmbientConditions(String AmbientConditions) {
    this.AmbientConditions = AmbientConditions;
  }

  public String getEngineHours() {
    return EngineHours;
  }

  public void setEngineHours(String EngineHours) {
    this.EngineHours = EngineHours;
  }

  public String getFanDrive() {
    return FanDrive;
  }

  public void setFanDrive(String FanDrive) {
    this.FanDrive = FanDrive;
  }

  public String getEngineFluid2() {
    return EngineFluid2;
  }

  public void setEngineFluid2(String EngineFluid2) {
    this.EngineFluid2 = EngineFluid2;
  }

  public String getIdleOperation() {
    return IdleOperation;
  }

  public void setIdleOperation(String IdleOperation) {
    this.IdleOperation = IdleOperation;
  }

  public String getVehicleDistance() {
    return VehicleDistance;
  }

  public void setVehicleDistance(String VehicleDistance) {
    this.VehicleDistance = VehicleDistance;
  }

  public String getIdleShutdown() {
    return IdleShutdown;
  }

  public void setIdleShutdown(String IdleShutdown) {
    this.IdleShutdown = IdleShutdown;
  }

  public String getTimeDate() {
    return TimeDate;
  }

  public void setTimeDate(String TimeDate) {
    this.TimeDate = TimeDate;
  }

  public String getVehicleHours() {
    return VehicleHours;
  }

  public void setVehicleHours(String VehicleHours) {
    this.VehicleHours = VehicleHours;
  }

  public String getFuelConsumption() {
    return FuelConsumption;
  }

  public void setFuelConsumption(String FuelConsumption) {
    this.FuelConsumption = FuelConsumption;
  }

  public String getWaterInFuelInd() {
    return WaterInFuelInd;
  }

  public void setWaterInFuelInd(String WaterInFuelInd) {
    this.WaterInFuelInd = WaterInFuelInd;
  }

  public Vehicle getVehicle() {
    return vehicle;
  }

  public void setVehicle(Vehicle vehicle) {
    this.vehicle = vehicle;
  }

}
