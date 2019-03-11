/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.access.to.model;

import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nonnull;
import org.opentcs.access.to.CreationTO;
import static org.opentcs.util.Assertions.checkArgument;
import static org.opentcs.util.Assertions.checkInRange;
import org.opentcs.util.annotations.ScheduledApiChange;

/**
 * A transfer object describing a block in the plant model.
 *
 * 
 */
public class VehicleCreationTO
    extends CreationTO
    implements Serializable {

  /**
   * The vehicle's length (in mm).
   */
  private int length = 1000;
  /**
   * The energy level value at/below which the vehicle should be recharged.
   */
  private int energyLevelCritical = 30;
  /**
   * The energy level value at/above which the vehicle can be dispatched again when charging.
   */
  private int energyLevelGood = 90;
  /**
   * The vehicle's maximum velocity (in mm/s).
   */
  private int maxVelocity = 1000;
  /**
   * The vehicle's maximum reverse velocity (in mm/s).
   */
  private int maxReverseVelocity = 1000;
  
  
  
  private String vehicleIp = "127.0.0.1";

  /**
   * Creates a new instance.
   *
   * @param name The name of this vehicle.
   */
  public VehicleCreationTO(@Nonnull String name) {
    super(name);
  }

  private VehicleCreationTO(@Nonnull String name,
                            @Nonnull Map<String, String> properties,
                            int length,
                            int energyLevelCritical,
                            int energyLevelGood,
                            int maxVelocity,
                            int maxReverseVelocity,
                            String vehicleIp ) {
    super(name, properties);
    this.length = length;
    this.energyLevelCritical = energyLevelCritical;
    this.energyLevelGood = energyLevelGood;
    this.maxVelocity = maxVelocity;
    this.maxReverseVelocity = maxReverseVelocity;
    this.vehicleIp = vehicleIp;
  }

  /**
   * Sets the name of this vehicle.
   *
   * @param name The new name.
   * @return The modified vehicle.
   */
  @Deprecated
  @ScheduledApiChange(when = "5.0")
  @Nonnull
  @Override
  public VehicleCreationTO setName(@Nonnull String name) {
    return (VehicleCreationTO) super.setName(name);
  }

  /**
   * Creates a copy of this object with the given name.
   *
   * @param name The new instance.
   * @return A copy of this object, differing in the given name.
   */
  @Override
  public VehicleCreationTO withName(@Nonnull String name) {
    return new VehicleCreationTO(name,
                                 getModifiableProperties(),
                                 length,
                                 energyLevelCritical,
                                 energyLevelGood,
                                 maxVelocity,
                                 maxReverseVelocity,
                                 vehicleIp);
  }

  /**
   * Sets the properties of this vehicle.
   *
   * @param properties The new properties.
   * @return The modified vehicle.
   */
  @Deprecated
  @ScheduledApiChange(when = "5.0")
  @Nonnull
  @Override
  public VehicleCreationTO setProperties(@Nonnull Map<String, String> properties) {
    return (VehicleCreationTO) super.setProperties(properties);
  }

  /**
   * Creates a copy of this object with the given properties.
   *
   * @param properties The new properties.
   * @return A copy of this object, differing in the given properties.
   */
  @Override
  public VehicleCreationTO withProperties(@Nonnull Map<String, String> properties) {
    return new VehicleCreationTO(getName(),
                                 properties,
                                 length,
                                 energyLevelCritical,
                                 energyLevelGood,
                                 maxVelocity,
                                 maxReverseVelocity,
                                 vehicleIp    );
  }

  /**
   * Sets a single property of this vehicle.
   *
   * @param key The property key.
   * @param value The property value.
   * @return The modified vehicle.
   */
  @Deprecated
  @ScheduledApiChange(when = "5.0")
  @Nonnull
  @Override
  public VehicleCreationTO setProperty(@Nonnull String key, @Nonnull String value) {
    return (VehicleCreationTO) super.setProperty(key, value);
  }

  /**
   * Creates a copy of this object and adds the given property.
   * If value == null, then the key-value pair is removed from the properties.
   *
   * @param key the key.
   * @param value the value
   * @return A copy of this object that either
   * includes the given entry in it's current properties, if value != null or
   * excludes the entry otherwise.
   */
  @Override
  public VehicleCreationTO withProperty(@Nonnull String key, @Nonnull String value) {
    return new VehicleCreationTO(getName(),
                                 propertiesWith(key, value),
                                 length,
                                 energyLevelCritical,
                                 energyLevelGood,
                                 maxVelocity,
                                 maxReverseVelocity,
                                 vehicleIp);
  }

  /**
   * Returns the vehicle's length (in mm).
   *
   * @return The vehicle's length (in mm).
   */
  public int getLength() {
    return length;
  }

  
   public String getVehicleIpAddress() {
    return vehicleIp ;
  }
  
  /**
   * Sets the vehicle's length (in mm).
   *
   * @param length The new length. Must be at least 1.
   * @throws IllegalArgumentException If {@code newLength} is less than 1.
   * @return The modified vehicle.
   */
  @Deprecated
  @ScheduledApiChange(when = "5.0")
  @Nonnull
  public VehicleCreationTO setLength(int length) {
    checkArgument(length >= 1, "length must be at least 1: " + length);
    this.length = length;
    return this;
  }

  /**
   * Creates a copy of this object with the vehicle's given length (in mm).
   *
   * @param length The new length. Must be at least 1.
   * @return A copy of this object, differing in the given vehicle length.
   */
  public VehicleCreationTO withLength(int length) {
    checkArgument(length >= 1, "length must be at least 1: " + length);
    return new VehicleCreationTO(getName(),
                                 getModifiableProperties(),
                                 length,
                                 energyLevelCritical,
                                 energyLevelGood,
                                 maxVelocity,
                                 maxReverseVelocity,
                                 vehicleIp );
  }

  /**
   * Returns this vehicle's critical energy level (in percent of the maximum).
   * The critical energy level is the one at/below which the vehicle should be recharged.
   *
   * @return This vehicle's critical energy level.
   */
  public int getEnergyLevelCritical() {
    return energyLevelCritical;
  }

  /**
   * Sets this vehicle's critical energy level (in percent of the maximum).
   * The critical energy level is the one at/below which the vehicle should be recharged.
   *
   * @param energyLevelCritical The new critical energy level. Must not be smaller than 0 or
   * greater than 100.
   * @return The modified vehicle.
   */
  @Deprecated
  @ScheduledApiChange(when = "5.0")
  @Nonnull
  public VehicleCreationTO setEnergyLevelCritical(int energyLevelCritical) {
    checkInRange(energyLevelCritical, 0, 100);
    this.energyLevelCritical = energyLevelCritical;
    return this;
  }

  /**
   * Creates a copy of this object with the given critical energy level.
   * The critical energy level is the one at/below which the vehicle should be recharged.
   *
   * @param energyLevelCritical The new critical energy level. Must not be smaller than 0 or
   * greater than 100.
   * @return A copy of this object, differing in the given value.
   */
  public VehicleCreationTO withEnergyLevelCritical(int energyLevelCritical) {
    checkInRange(energyLevelCritical, 0, 100);
    return new VehicleCreationTO(getName(),
                                 getModifiableProperties(),
                                 length,
                                 energyLevelCritical,
                                 energyLevelGood,
                                 maxVelocity,
                                 maxReverseVelocity,
                                 vehicleIp);
  }

  /**
   * Returns this vehicle's good energy level (in percent of the maximum).
   * The good energy level is the one at/above which the vehicle can be dispatched again when
   * charging.
   *
   * @return This vehicle's good energy level.
   */
  public int getEnergyLevelGood() {
    return energyLevelGood;
  }

  public int getMaxVelocity() {
    return maxVelocity;
  }

  @Deprecated
  @ScheduledApiChange(when = "5.0")
  public VehicleCreationTO setMaxVelocity(int maxVelocity) {
    this.maxVelocity = checkInRange(maxVelocity, 0, Integer.MAX_VALUE);
    return this;
  }

  /**
   * Creates a copy of this object with the given maximum velocity (in mm/s).
   *
   * @param maxVelocity the new max velocity.
   * @return A copy of this object, differing in the given value.
   */
  public VehicleCreationTO withMaxVelocity(int maxVelocity) {
    this.maxVelocity = checkInRange(maxVelocity, 0, Integer.MAX_VALUE);
    return new VehicleCreationTO(getName(),
                                 getModifiableProperties(),
                                 length,
                                 energyLevelCritical,
                                 energyLevelGood,
                                 maxVelocity,
                                 maxReverseVelocity,
                                  vehicleIp);
  }

  public int getMaxReverseVelocity() {
    return maxReverseVelocity;
  }

  @Deprecated
  @ScheduledApiChange(when = "5.0")
  public VehicleCreationTO setMaxReverseVelocity(int maxReverseVelocity) {
    this.maxReverseVelocity = checkInRange(maxReverseVelocity, 0, Integer.MAX_VALUE);
    return this;
  }

  /**
   * Creates a copy of this object with the given maximum reverse velocity (in mm/s).
   *
   * @param maxReverseVelocity the new maximum reverse velocity.
   * @return A copy of this object, differing in the given value.
   */
  public VehicleCreationTO withMaxReverseVelocity(int maxReverseVelocity) {
    checkInRange(maxReverseVelocity, 0, Integer.MAX_VALUE);
    return new VehicleCreationTO(getName(),
                                 getModifiableProperties(),
                                 length,
                                 energyLevelCritical,
                                 energyLevelGood,
                                 maxVelocity,
                                 maxReverseVelocity,
                                 vehicleIp);
  }

  /**
   * Sets this vehicle's good energy level (in percent of the maximum).
   * The good energy level is the one at/above which the vehicle can be dispatched again when
   * charging.
   *
   * @param energyLevelGood The new good energy level. Must not be smaller than 0 or greater than
   * 100.
   * @return The modified vehicle.
   */
  @Deprecated
  @ScheduledApiChange(when = "5.0")
  @Nonnull
  public VehicleCreationTO setEnergyLevelGood(int energyLevelGood) {
    checkInRange(energyLevelGood, 0, 100);
    this.energyLevelGood = energyLevelGood;
    return this;
  }

  /**
   * Creates a copy of this object with the vehicle's good energy level (in percent of the maximum).
   * The good energy level is the one at/above which the vehicle can be dispatched again when
   * charging.
   *
   * @param energyLevelGood The new good energy level. Must not be smaller than 0 or greater than
   * 100.
   * @return A copy of this object, differing in the given value.
   */
  public VehicleCreationTO withEnergyLevelGood(int energyLevelGood) {
    checkInRange(energyLevelGood, 0, 100);
    return new VehicleCreationTO(getName(),
                                 getModifiableProperties(),
                                 length,
                                 energyLevelCritical,
                                 energyLevelGood,
                                 maxVelocity,
                                 maxReverseVelocity,
                                 vehicleIp );
  }
  
  public VehicleCreationTO withVehicleIp(String vehicleIp) {
    
    return new VehicleCreationTO(getName(),
                                 getModifiableProperties(),
                                 length,
                                 energyLevelCritical,
                                 energyLevelGood,
                                 maxVelocity,
                                 maxReverseVelocity,
                                 vehicleIp );
  }
  
  
}
