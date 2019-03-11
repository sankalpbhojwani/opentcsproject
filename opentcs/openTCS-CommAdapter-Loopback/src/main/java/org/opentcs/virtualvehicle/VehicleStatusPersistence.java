/*
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.virtualvehicle;

import com.sun.jersey.api.client.ClientHandlerException;
import org.opentcs.util.persistence.models.VehicleStatus;
import org.opentcs.util.persistence.models.VehicleStatusCheck;

/**
 *
 * @author a2z computer
 */
public class VehicleStatusPersistence {
 
  public final static VehicleStatus vehiclePersistence(VehicleStatus status,String port,String ip)
  { 
    try{
      //Gets telemetry data associated with specified ip address and port number
      final VehicleStatus vehicleStatus=VehicleStatusCheck.dataFromServer(port,ip);
    
    if(isNotEmpty(vehicleStatus.getAmbientConditions())){
    status.setAmbientConditions(vehicleStatus.getAmbientConditions());}
    
    if(isNotEmpty(vehicleStatus.getCruiseControl())){
    status.setCruiseControl(vehicleStatus.getCruiseControl());}
    
    if(isNotEmpty(vehicleStatus.getElectricalPower())){
    status.setElectricalPower(vehicleStatus.getElectricalPower());}
    
    if(isNotEmpty(vehicleStatus.getEngineFluid())){
    status.setEngineFluid(vehicleStatus.getEngineFluid());}
    
    if(isNotEmpty(vehicleStatus.getEngineFluid2())){
    status.setEngineFluid2(vehicleStatus.getEngineFluid2());}
    
    if(isNotEmpty(vehicleStatus.getEngineHours())){
    status.setEngineHours(vehicleStatus.getEngineHours());}
    
    if(isNotEmpty(vehicleStatus.getEngineTemp())){
    status.setEngineTemp(vehicleStatus.getEngineTemp());}
    
    if(isNotEmpty(vehicleStatus.getFanDrive())){
    status.setFanDrive(vehicleStatus.getFanDrive());}
    
    if(isNotEmpty(vehicleStatus.getFuelConsumption())){
    status.setFuelConsumption(vehicleStatus.getFuelConsumption());}
    
    if(isNotEmpty(vehicleStatus.getFuelEco())){
    status.setFuelEco(vehicleStatus.getFuelEco());}
    
    if(isNotEmpty(vehicleStatus.getIdleOperation())){
    status.setIdleOperation(vehicleStatus.getIdleOperation());}
    
    if(isNotEmpty(vehicleStatus.getIdleShutdown())){
    status.setIdleShutdown(vehicleStatus.getIdleShutdown());}
    
    if(isNotEmpty(vehicleStatus.getInletExhaust())){
    status.setInletExhaust(vehicleStatus.getInletExhaust());}
    
    if(isNotEmpty(vehicleStatus.getTimeDate())){
    status.setTimeDate(vehicleStatus.getTimeDate());}
    
    if(isNotEmpty(vehicleStatus.getTruckName())){
    status.setTruckName(vehicleStatus.getTruckName());}
    
    if(isNotEmpty(vehicleStatus.getVehicleDistance())){
    status.setVehicleDistance(vehicleStatus.getVehicleDistance());}
    
    if(isNotEmpty(vehicleStatus.getVehicleHours())){
    status.setVehicleHours(vehicleStatus.getVehicleHours());}
    
    if(isNotEmpty(vehicleStatus.getVehicleID())){
    status.setVehicleID(vehicleStatus.getVehicleID());}
    
    if(isNotEmpty(vehicleStatus.getWaterInFuelInd())){
    status.setWaterInFuelInd(vehicleStatus.getWaterInFuelInd());}
    return status;
    }
    catch(ClientHandlerException e){
    return status;
    }
    
  } 
   private static Boolean isNotEmpty(final String str) {
		return !(str == null || str.isEmpty());
	}
   
}
