/*
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.util.persistence.models;

//import javax.ws.rs.client.Client;

import javax.ws.rs.core.UriBuilder;

import javax.ws.rs.core.MediaType;
//import org.glassfish.jersey.client.ClientConfig;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.google.gson.Gson;
import javax.swing.Timer;
import org.opentcs.util.persistence.models.VehicleStatus;
/**
 *
 * @author a2z computer
 */
public class VehicleStatusCheck {
//  private static Timer timer;
  public static VehicleStatus dataFromServer(String port,String ip){
  String url="http://"+ip+":"+port;
  ClientConfig config = new DefaultClientConfig();
		  Client client = Client.create(config);
		  WebResource service = client.resource(UriBuilder.fromUri(url).build());
		  // getting XML data
		 // System.out.println(service. path("AITVSystemStatus").accept(MediaType.APPLICATION_JSON).get(String.class));
		  String jsonstring=service. path("AITVSystemStatus").accept(MediaType.APPLICATION_JSON).get(String.class);
		  // getting JSON data
		//  System.out.println(service. path("restPath").path("resourcePath").accept(MediaType.APPLICATION_XML).get(String.class));
		 Gson g=new Gson();
     VehicleStatus vehicleStatus=g.fromJson(jsonstring,VehicleStatus.class);
		// System.out.println(serverProperty.getElectricalPower());
    return vehicleStatus;
  }
   
  

}

 