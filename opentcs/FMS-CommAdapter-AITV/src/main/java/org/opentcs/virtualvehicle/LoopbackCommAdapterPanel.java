/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.virtualvehicle;

import com.google.inject.assistedinject.Assisted;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.ResourceBundle;
import java.util.Set;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.customizations.ServiceCallWrapper;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Triple;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.LoadHandlingDevice;
import org.opentcs.drivers.vehicle.VehicleCommAdapterEvent;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import org.opentcs.drivers.vehicle.commands.PublishEventCommand;
import org.opentcs.drivers.vehicle.commands.SetEnergyLevelCommand;
import org.opentcs.drivers.vehicle.commands.SetLoadHandlingDevicesCommand;
import org.opentcs.drivers.vehicle.commands.SetOrienatationAngleCommand;
import org.opentcs.drivers.vehicle.commands.SetPercisePositionCommand;
import org.opentcs.drivers.vehicle.commands.SetPositionCommand;
import org.opentcs.drivers.vehicle.commands.SetStateCommand;
import org.opentcs.drivers.vehicle.commands.SetVehiclePropertyCommand;
import org.opentcs.drivers.vehicle.management.VehicleCommAdapterPanel;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.util.CallWrapper;
import org.opentcs.util.Comparators;
import org.opentcs.util.gui.StringListCellRenderer;
import org.opentcs.virtualvehicle.commands.CurrentMovementCommandFailedCommand;
import org.opentcs.virtualvehicle.commands.SetSingleStepModeEnabledCommand;
import org.opentcs.virtualvehicle.commands.SetVehiclePausedCommand;
import org.opentcs.virtualvehicle.commands.TriggerCommand;
import org.opentcs.virtualvehicle.inputcomponents.DropdownListInputPanel;
import org.opentcs.virtualvehicle.inputcomponents.InputDialog;
import org.opentcs.virtualvehicle.inputcomponents.InputPanel;
import org.opentcs.virtualvehicle.inputcomponents.SingleTextInputPanel;
import org.opentcs.virtualvehicle.inputcomponents.TextInputPanel;
import org.opentcs.virtualvehicle.inputcomponents.TripleTextInputPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The panel corresponding to the LoopbackCommunicationAdapter.
 *
 * @author Iryna Felko (Fraunhofer IML)
 * @author Stefan Walter (Fraunhofer IML)
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class LoopbackCommAdapterPanel
    extends VehicleCommAdapterPanel {

  /**
   * The resource bundle.
   */
  private static final ResourceBundle BUNDLE
      = ResourceBundle.getBundle("org/opentcs/virtualvehicle/Bundle");
  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(LoopbackCommAdapterPanel.class);
  /**
   * The vehicle service used for interaction with the comm adapter.
   */
  private final VehicleService vehicleService;
  /**
   * The comm adapter's process model.
   */
  private LoopbackVehicleModelTO processModel;
  /**
   * The call wrapper to use for service calls.
   */
  private final CallWrapper callWrapper;

  /**
   * Creates new LoopbackCommunicationAdapterPanel.
   *
   * @param processModel The comm adapter's process model.
   * @param vehicleService The vehicle service.
   * @param callWrapper The call wrapper to use for service calls.
   */
  @Inject
  public LoopbackCommAdapterPanel(@Assisted LoopbackVehicleModelTO processModel,
                                  @Assisted VehicleService vehicleService,
                                  @ServiceCallWrapper CallWrapper callWrapper) {

    this.processModel = requireNonNull(processModel, "processModel");
    this.vehicleService = requireNonNull(vehicleService, "vehicleService");
    this.callWrapper = requireNonNull(callWrapper, "callWrapper");

    initComponents();
    initGuiContent();
  }

  @Override
  public void processModelChange(String attributeChanged, VehicleProcessModelTO newProcessModel) {
    if (!(newProcessModel instanceof LoopbackVehicleModelTO)) {
      return;
    }

    processModel = (LoopbackVehicleModelTO) newProcessModel;
    updateLoopbackVehicleModelData(attributeChanged, processModel);
    updateVehicleProcessModelData(attributeChanged, processModel);
  }

  private void initGuiContent() {
    for (VehicleProcessModel.Attribute attribute : VehicleProcessModel.Attribute.values()) {
      processModelChange(attribute.name(), processModel);
    }
    for (LoopbackVehicleModel.Attribute attribute : LoopbackVehicleModel.Attribute.values()) {
      processModelChange(attribute.name(), processModel);
    }
  }

  private void updateLoopbackVehicleModelData(String attributeChanged,
                                              LoopbackVehicleModelTO processModel) {
    if (Objects.equals(attributeChanged,
                       LoopbackVehicleModel.Attribute.OPERATING_TIME.name())) {
      updateOperatingTime(processModel.getOperatingTime());
    }
    else if (Objects.equals(attributeChanged,
                            LoopbackVehicleModel.Attribute.ACCELERATION.name())) {
      updateMaxAcceleration(processModel.getMaxAcceleration());
    }
    else if (Objects.equals(attributeChanged,
                            LoopbackVehicleModel.Attribute.DECELERATION.name())) {
      updateMaxDeceleration(processModel.getMaxDeceleration());
    }
    else if (Objects.equals(attributeChanged,
                            LoopbackVehicleModel.Attribute.MAX_FORWARD_VELOCITY.name())) {
      updateMaxForwardVelocity(processModel.getMaxFwdVelocity());
    }
    else if (Objects.equals(attributeChanged,
                            LoopbackVehicleModel.Attribute.MAX_REVERSE_VELOCITY.name())) {
      updateMaxReverseVelocity(processModel.getMaxRevVelocity());
    }
    else if (Objects.equals(attributeChanged,
                            LoopbackVehicleModel.Attribute.SINGLE_STEP_MODE.name())) {
      updateSingleStepMode(processModel.isSingleStepModeEnabled());
    }
    else if (Objects.equals(attributeChanged,
                            LoopbackVehicleModel.Attribute.VEHICLE_PAUSED.name())) {
      updateVehiclePaused(processModel.isVehiclePaused());
    }
  }

  private void updateVehicleProcessModelData(String attributeChanged,
                                             VehicleProcessModelTO processModel) {
    if (Objects.equals(attributeChanged,
                       VehicleProcessModel.Attribute.COMM_ADAPTER_ENABLED.name())) {
      updateCommAdapterEnabled(processModel.isCommAdapterEnabled());
    }
    else if (Objects.equals(attributeChanged,
                            VehicleProcessModel.Attribute.POSITION.name())) {
      updatePosition(processModel.getVehiclePosition());
    }
    else if (Objects.equals(attributeChanged,
                            VehicleProcessModel.Attribute.STATE.name())) {
      updateVehicleState(processModel.getVehicleState());
    }
    else if (Objects.equals(attributeChanged,
                            VehicleProcessModel.Attribute.PRECISE_POSITION.name())) {
      updatePrecisePosition(processModel.getPrecisePosition());
    }
    else if (Objects.equals(attributeChanged,
                            VehicleProcessModel.Attribute.ORIENTATION_ANGLE.name())) {
      updateOrientationAngle(processModel.getOrientationAngle());
    }
    else if (Objects.equals(attributeChanged,
                            VehicleProcessModel.Attribute.ENERGY_LEVEL.name())) {
      updateEnergyLevel(processModel.getEnergyLevel());
    }
    else if (Objects.equals(attributeChanged,
                            VehicleProcessModel.Attribute.LOAD_HANDLING_DEVICES.name())) {
      updateVehicleLoadHandlingDevice(processModel.getLoadHandlingDevices());
    }
  }

  private void updateVehicleLoadHandlingDevice(List<LoadHandlingDevice> devices) {
    if (devices.size() > 1) {
      LOG.warn("size of load handling devices greater than 1 ({})", devices.size());
    }
    Iterator<LoadHandlingDevice> deviceIterator = devices.iterator();
    boolean loaded = deviceIterator.hasNext() ? deviceIterator.next().isFull() : false;
    SwingUtilities.invokeLater(() -> lHDCheckbox.setSelected(loaded));
  }

  private void updateEnergyLevel(int energy) {
    SwingUtilities.invokeLater(() -> energyLevelTxt.setText(Integer.toString(energy)));
  }

  private void updateCommAdapterEnabled(boolean isEnabled) {
    SwingUtilities.invokeLater(() -> {
      setStatePanelEnabled(isEnabled);
      chkBoxEnable.setSelected(isEnabled);
    });
  }

  private void updatePosition(String vehiclePosition) {
    SwingUtilities.invokeLater(() -> {
      if (vehiclePosition == null) {
        positionTxt.setText("");
        return;
      }

      try {
        for (Point curPoint : callWrapper.call(() -> vehicleService.fetchObjects(Point.class))) {
          if (curPoint.getName().equals(vehiclePosition)) {
            positionTxt.setText(curPoint.getName());
            break;
          }
        }
      }
      catch (Exception ex) {
        LOG.warn("Error fetching points", ex);
      }
    });
  }

  private void updateVehicleState(Vehicle.State vehicleState) {
    SwingUtilities.invokeLater(() -> stateTxt.setText(vehicleState.toString()));
  }

  private void updatePrecisePosition(Triple precisePos) {
    SwingUtilities.invokeLater(() -> {
      if (precisePos == null) {
        setPrecisePosText(null, null, null);
      }
      else {
        setPrecisePosText(precisePos.getX(), precisePos.getY(), precisePos.getZ());
      }
    });
  }

  private void updateOrientationAngle(Double orientation) {
    SwingUtilities.invokeLater(() -> {
      if (Double.isNaN(orientation)) {
        orientationAngleTxt.setText(BUNDLE.getString("OrientationAngleNotSet"));
      }
      else {
        orientationAngleTxt.setText(Double.toString(orientation));
      }
    });
  }

  private void updateOperatingTime(int defaultOperatingTime) {
    SwingUtilities.invokeLater(() -> opTimeTxt.setText(Integer.toString(defaultOperatingTime)));
  }

  private void updateMaxAcceleration(int maxAcceleration) {
    SwingUtilities.invokeLater(() -> maxAccelTxt.setText(Integer.toString(maxAcceleration)));
  }

  private void updateMaxDeceleration(int maxDeceleration) {
    SwingUtilities.invokeLater(() -> maxDecelTxt.setText(Integer.toString(maxDeceleration)));
  }

  private void updateMaxForwardVelocity(int maxFwdVelocity) {
    SwingUtilities.invokeLater(() -> maxFwdVeloTxt.setText(Integer.toString(maxFwdVelocity)));
  }

  private void updateMaxReverseVelocity(int maxRevVelocity) {
    SwingUtilities.invokeLater(() -> maxRevVeloTxt.setText(Integer.toString(maxRevVelocity)));
  }

  private void updateSingleStepMode(boolean singleStepMode) {
    SwingUtilities.invokeLater(() -> {
      triggerButton.setEnabled(singleStepMode);
      singleModeRadioButton.setSelected(singleStepMode);
      flowModeRadioButton.setSelected(!singleStepMode);
    });
  }

  private void updateVehiclePaused(boolean isVehiclePaused) {
    SwingUtilities.invokeLater(() -> pauseVehicleCheckBox.setSelected(isVehiclePaused));
  }

  /**
   * Enable/disable the input fields and buttons in the "Current position/state" panel.
   * If disabled the user can not change any values or modify the vehicles state.
   *
   * @param enabled boolean indicating if the panel should be enabled
   */
  private void setStatePanelEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> positionTxt.setEnabled(enabled));
    SwingUtilities.invokeLater(() -> stateTxt.setEnabled(enabled));
    SwingUtilities.invokeLater(() -> energyLevelTxt.setEnabled(enabled));
    SwingUtilities.invokeLater(() -> precisePosTextArea.setEnabled(enabled));
    SwingUtilities.invokeLater(() -> orientationAngleTxt.setEnabled(enabled));
    SwingUtilities.invokeLater(() -> pauseVehicleCheckBox.setEnabled(enabled));
  }

  private TCSObjectReference<Vehicle> getVehicleReference()
      throws Exception {
    return callWrapper.call(() -> vehicleService.
        fetchObject(Vehicle.class, processModel.getVehicleName())).getReference();
  }

  private void sendCommAdapterCommand(AdapterCommand command) {
    try {
      TCSObjectReference<Vehicle> vehicleRef = getVehicleReference();
      callWrapper.call(() -> vehicleService.sendCommAdapterCommand(vehicleRef, command));
    }
    catch (Exception ex) {
      LOG.warn("Error sending comm adapter command '{}'", command, ex);
    }
  }

  // CHECKSTYLE:OFF
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    modeButtonGroup = new javax.swing.ButtonGroup();
    propertyEditorGroup = new javax.swing.ButtonGroup();
    vehicleBahaviourPanel = new javax.swing.JPanel();
    PropsPowerOuterContainerPanel = new javax.swing.JPanel();
    PropsPowerInnerContainerPanel = new javax.swing.JPanel();
    vehiclePropsPanel = new javax.swing.JPanel();
    maxFwdVeloLbl = new javax.swing.JLabel();
    maxFwdVeloTxt = new javax.swing.JTextField();
    maxFwdVeloUnitLbl = new javax.swing.JLabel();
    maxRevVeloLbl = new javax.swing.JLabel();
    maxRevVeloTxt = new javax.swing.JTextField();
    maxRevVeloUnitLbl = new javax.swing.JLabel();
    maxAccelLbl = new javax.swing.JLabel();
    maxAccelTxt = new javax.swing.JTextField();
    maxAccelUnitLbl = new javax.swing.JLabel();
    maxDecelTxt = new javax.swing.JTextField();
    maxDecelLbl = new javax.swing.JLabel();
    maxDecelUnitLbl = new javax.swing.JLabel();
    defaultOpTimeLbl = new javax.swing.JLabel();
    defaultOpTimeUntiLbl = new javax.swing.JLabel();
    opTimeTxt = new javax.swing.JTextField();
    trucknamelbl = new javax.swing.JLabel();
    truckname = new javax.swing.JTextField();
    engineTemplbl = new javax.swing.JLabel();
    jLabel10 = new javax.swing.JLabel();
    enginefluidlbl = new javax.swing.JLabel();
    jTextField6 = new javax.swing.JTextField();
    jLabel12 = new javax.swing.JLabel();
    inletexhaustlbl = new javax.swing.JLabel();
    jTextField7 = new javax.swing.JTextField();
    jLabel14 = new javax.swing.JLabel();
    fuelecolbl = new javax.swing.JLabel();
    jTextField8 = new javax.swing.JTextField();
    jLabel16 = new javax.swing.JLabel();
    cruisecontrollbl = new javax.swing.JLabel();
    jTextField9 = new javax.swing.JTextField();
    jLabel18 = new javax.swing.JLabel();
    vehicleidlbl = new javax.swing.JLabel();
    jTextField10 = new javax.swing.JTextField();
    jLabel20 = new javax.swing.JLabel();
    electricalpowerlbl = new javax.swing.JLabel();
    jTextField11 = new javax.swing.JTextField();
    jLabel22 = new javax.swing.JLabel();
    ambientconditionlbl = new javax.swing.JLabel();
    jTextField12 = new javax.swing.JTextField();
    jLabel24 = new javax.swing.JLabel();
    enginehourslbl = new javax.swing.JLabel();
    fandrivelbl = new javax.swing.JLabel();
    enginefluid2lbl = new javax.swing.JLabel();
    ldleoperationlbl = new javax.swing.JLabel();
    vehicledistancelbl = new javax.swing.JLabel();
    idleshutdownlbl = new javax.swing.JLabel();
    timedatelbl = new javax.swing.JLabel();
    vehiclehourslbl = new javax.swing.JLabel();
    fuelconsumptionlbl = new javax.swing.JLabel();
    waterfluidlbl = new javax.swing.JLabel();
    jTextField13 = new javax.swing.JTextField();
    jTextField14 = new javax.swing.JTextField();
    jTextField15 = new javax.swing.JTextField();
    jTextField16 = new javax.swing.JTextField();
    jTextField17 = new javax.swing.JTextField();
    jTextField18 = new javax.swing.JTextField();
    jTextField19 = new javax.swing.JTextField();
    jTextField20 = new javax.swing.JTextField();
    jTextField21 = new javax.swing.JTextField();
    jTextField22 = new javax.swing.JTextField();
    jLabel35 = new javax.swing.JLabel();
    jLabel36 = new javax.swing.JLabel();
    jLabel37 = new javax.swing.JLabel();
    jLabel38 = new javax.swing.JLabel();
    jLabel39 = new javax.swing.JLabel();
    jLabel40 = new javax.swing.JLabel();
    jLabel41 = new javax.swing.JLabel();
    jLabel42 = new javax.swing.JLabel();
    jLabel43 = new javax.swing.JLabel();
    jLabel44 = new javax.swing.JLabel();
    jTextField1 = new javax.swing.JTextField();
    profilesContainerPanel = new javax.swing.JPanel();
    filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
    jPanel4 = new javax.swing.JPanel();
    vehicleStatePanel = new javax.swing.JPanel();
    stateContainerPanel = new javax.swing.JPanel();
    connectionPanel = new javax.swing.JPanel();
    chkBoxEnable = new javax.swing.JCheckBox();
    curPosPanel = new javax.swing.JPanel();
    energyLevelTxt = new javax.swing.JTextField();
    energyLevelLbl = new javax.swing.JLabel();
    pauseVehicleCheckBox = new javax.swing.JCheckBox();
    orientationAngleLbl = new javax.swing.JLabel();
    precisePosUnitLabel = new javax.swing.JLabel();
    orientationAngleTxt = new javax.swing.JTextField();
    energyLevelLabel = new javax.swing.JLabel();
    orientationLabel = new javax.swing.JLabel();
    positionTxt = new javax.swing.JTextField();
    positionLabel = new javax.swing.JLabel();
    pauseVehicleLabel = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    stateTxt = new javax.swing.JTextField();
    jLabel3 = new javax.swing.JLabel();
    precisePosTextArea = new javax.swing.JTextArea();
    propertySetterPanel = new javax.swing.JPanel();
    keyLabel = new javax.swing.JLabel();
    valueTextField = new javax.swing.JTextField();
    propSetButton = new javax.swing.JButton();
    removePropRadioBtn = new javax.swing.JRadioButton();
    setPropValueRadioBtn = new javax.swing.JRadioButton();
    jPanel3 = new javax.swing.JPanel();
    keyTextField = new javax.swing.JTextField();
    eventPanel = new javax.swing.JPanel();
    includeAppendixCheckBox = new javax.swing.JCheckBox();
    appendixTxt = new javax.swing.JTextField();
    dispatchEventButton = new javax.swing.JButton();
    dispatchCommandFailedButton = new javax.swing.JButton();
    controlTabPanel = new javax.swing.JPanel();
    singleModeRadioButton = new javax.swing.JRadioButton();
    flowModeRadioButton = new javax.swing.JRadioButton();
    triggerButton = new javax.swing.JButton();
    loadDevicePanel = new javax.swing.JPanel();
    jPanel1 = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();
    lHDCheckbox = new javax.swing.JCheckBox();

    setName("LoopbackCommunicationAdapterPanel"); // NOI18N
    setLayout(new java.awt.BorderLayout());

    vehicleBahaviourPanel.setLayout(new java.awt.BorderLayout());

    PropsPowerOuterContainerPanel.setLayout(new java.awt.BorderLayout());

    PropsPowerInnerContainerPanel.setLayout(new javax.swing.BoxLayout(PropsPowerInnerContainerPanel, javax.swing.BoxLayout.X_AXIS));

    vehiclePropsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(BUNDLE.getString("Vehicle_properties"))); // NOI18N
    vehiclePropsPanel.setLayout(new java.awt.GridBagLayout());

    maxFwdVeloLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    maxFwdVeloLbl.setText(BUNDLE.getString("maxFwdVelocityLabel")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(maxFwdVeloLbl, gridBagConstraints);

    maxFwdVeloTxt.setEditable(false);
    maxFwdVeloTxt.setColumns(5);
    maxFwdVeloTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    maxFwdVeloTxt.setText("0");
    maxFwdVeloTxt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    maxFwdVeloTxt.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(maxFwdVeloTxt, gridBagConstraints);

    maxFwdVeloUnitLbl.setText("mm/s");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(maxFwdVeloUnitLbl, gridBagConstraints);

    maxRevVeloLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    maxRevVeloLbl.setText(BUNDLE.getString("maxRevVelocityLabel")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(maxRevVeloLbl, gridBagConstraints);

    maxRevVeloTxt.setEditable(false);
    maxRevVeloTxt.setColumns(5);
    maxRevVeloTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    maxRevVeloTxt.setText("0");
    maxRevVeloTxt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    maxRevVeloTxt.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(maxRevVeloTxt, gridBagConstraints);

    maxRevVeloUnitLbl.setText("mm/s");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(maxRevVeloUnitLbl, gridBagConstraints);

    maxAccelLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    maxAccelLbl.setText(BUNDLE.getString("maxAccelerationLabel")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(maxAccelLbl, gridBagConstraints);

    maxAccelTxt.setEditable(false);
    maxAccelTxt.setColumns(5);
    maxAccelTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    maxAccelTxt.setText("1000");
    maxAccelTxt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    maxAccelTxt.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(maxAccelTxt, gridBagConstraints);

    maxAccelUnitLbl.setText("<html>mm/s<sup>2</sup>");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(maxAccelUnitLbl, gridBagConstraints);

    maxDecelTxt.setEditable(false);
    maxDecelTxt.setColumns(5);
    maxDecelTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    maxDecelTxt.setText("1000");
    maxDecelTxt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    maxDecelTxt.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(maxDecelTxt, gridBagConstraints);

    maxDecelLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    maxDecelLbl.setText(BUNDLE.getString("maxDecelerationLabel")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(maxDecelLbl, gridBagConstraints);

    maxDecelUnitLbl.setText("<html>mm/s<sup>2</sup>");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(maxDecelUnitLbl, gridBagConstraints);

    defaultOpTimeLbl.setText(BUNDLE.getString("defaultOperatingTime")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(defaultOpTimeLbl, gridBagConstraints);

    defaultOpTimeUntiLbl.setText("ms");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(defaultOpTimeUntiLbl, gridBagConstraints);

    opTimeTxt.setEditable(false);
    opTimeTxt.setColumns(5);
    opTimeTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    opTimeTxt.setText("1000");
    opTimeTxt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    opTimeTxt.setEnabled(false);
    opTimeTxt.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        opTimeTxtActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    vehiclePropsPanel.add(opTimeTxt, gridBagConstraints);

    trucknamelbl.setText("Truck name:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(trucknamelbl, gridBagConstraints);

    truckname.setEditable(false);
    truckname.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    truckname.setText("DPWT3_000562");
    truckname.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    truckname.setCaretPosition(4);
    truckname.setEnabled(false);
    truckname.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        trucknameActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(truckname, gridBagConstraints);

    engineTemplbl.setText("Engine temperature:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(engineTemplbl, gridBagConstraints);

    jLabel10.setText("jLabel10");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 6;
    vehiclePropsPanel.add(jLabel10, gridBagConstraints);

    enginefluidlbl.setText("Engine fluid:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(enginefluidlbl, gridBagConstraints);

    jTextField6.setEditable(false);
    jTextField6.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField6.setText("102");
    jTextField6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jTextField6.setEnabled(false);
    jTextField6.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField6ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
    vehiclePropsPanel.add(jTextField6, gridBagConstraints);

    jLabel12.setText("jLabel12");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 7;
    vehiclePropsPanel.add(jLabel12, gridBagConstraints);

    inletexhaustlbl.setText("Inlet exhaust:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(inletexhaustlbl, gridBagConstraints);

    jTextField7.setEditable(false);
    jTextField7.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField7.setText("109");
    jTextField7.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField7, gridBagConstraints);

    jLabel14.setText("jLabel14");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 8;
    vehiclePropsPanel.add(jLabel14, gridBagConstraints);

    fuelecolbl.setText("Fuel eco:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(fuelecolbl, gridBagConstraints);

    jTextField8.setEditable(false);
    jTextField8.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField8.setText("1");
    jTextField8.setEnabled(false);
    jTextField8.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField8ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField8, gridBagConstraints);

    jLabel16.setText("jLabel16");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 9;
    vehiclePropsPanel.add(jLabel16, gridBagConstraints);

    cruisecontrollbl.setText("Cruise control:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(cruisecontrollbl, gridBagConstraints);

    jTextField9.setEditable(false);
    jTextField9.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField9.setText("0");
    jTextField9.setEnabled(false);
    jTextField9.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField9ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField9, gridBagConstraints);

    jLabel18.setText("jLabel18");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 10;
    vehiclePropsPanel.add(jLabel18, gridBagConstraints);

    vehicleidlbl.setText("Vehicle ID:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(vehicleidlbl, gridBagConstraints);

    jTextField10.setEditable(false);
    jTextField10.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField10.setText("000562");
    jTextField10.setEnabled(false);
    jTextField10.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField10ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField10, gridBagConstraints);

    jLabel20.setText("jLabel20");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 11;
    vehiclePropsPanel.add(jLabel20, gridBagConstraints);

    electricalpowerlbl.setText("Electrical power:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(electricalpowerlbl, gridBagConstraints);

    jTextField11.setEditable(false);
    jTextField11.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField11.setText("24.46");
    jTextField11.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField11, gridBagConstraints);

    jLabel22.setText("jLabel22");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 12;
    vehiclePropsPanel.add(jLabel22, gridBagConstraints);

    ambientconditionlbl.setText("Ambient condition:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 13;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(ambientconditionlbl, gridBagConstraints);

    jTextField12.setEditable(false);
    jTextField12.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField12.setText("34");
    jTextField12.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 13;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField12, gridBagConstraints);

    jLabel24.setText("jLabel24");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 13;
    vehiclePropsPanel.add(jLabel24, gridBagConstraints);

    enginehourslbl.setText("Engine hours:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(enginehourslbl, gridBagConstraints);

    fandrivelbl.setText("Fan drive:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 15;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(fandrivelbl, gridBagConstraints);

    enginefluid2lbl.setText("Engine fluid 2:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 16;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(enginefluid2lbl, gridBagConstraints);

    ldleoperationlbl.setText("Idle operation:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 17;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(ldleoperationlbl, gridBagConstraints);

    vehicledistancelbl.setText("Vehicle distance:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 18;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(vehicledistancelbl, gridBagConstraints);

    idleshutdownlbl.setText("Idle shutdown:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 19;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(idleshutdownlbl, gridBagConstraints);

    timedatelbl.setText("Time date:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 20;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(timedatelbl, gridBagConstraints);

    vehiclehourslbl.setText("Vehicle hours:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 21;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(vehiclehourslbl, gridBagConstraints);

    fuelconsumptionlbl.setText("Fuel consumption:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 22;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(fuelconsumptionlbl, gridBagConstraints);

    waterfluidlbl.setText("Water in fuel:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 23;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    vehiclePropsPanel.add(waterfluidlbl, gridBagConstraints);

    jTextField13.setEditable(false);
    jTextField13.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField13.setText("112475");
    jTextField13.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField13, gridBagConstraints);

    jTextField14.setEditable(false);
    jTextField14.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField14.setText("3285.5");
    jTextField14.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 15;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField14, gridBagConstraints);

    jTextField15.setEditable(false);
    jTextField15.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField15.setText("98");
    jTextField15.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 16;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField15, gridBagConstraints);

    jTextField16.setEditable(false);
    jTextField16.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField16.setText("1324540");
    jTextField16.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 17;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField16, gridBagConstraints);

    jTextField17.setEditable(false);
    jTextField17.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField17.setText("3002454");
    jTextField17.setEnabled(false);
    jTextField17.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField17ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 18;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField17, gridBagConstraints);

    jTextField18.setEditable(false);
    jTextField18.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField18.setText("113897");
    jTextField18.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 19;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField18, gridBagConstraints);

    jTextField19.setEditable(false);
    jTextField19.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField19.setText("29112018");
    jTextField19.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 20;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField19, gridBagConstraints);

    jTextField20.setEditable(false);
    jTextField20.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField20.setText("8432156");
    jTextField20.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 21;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField20, gridBagConstraints);

    jTextField21.setEditable(false);
    jTextField21.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField21.setText("53");
    jTextField21.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 22;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField21, gridBagConstraints);

    jTextField22.setEditable(false);
    jTextField22.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField22.setText("0");
    jTextField22.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 23;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField22, gridBagConstraints);

    jLabel35.setText("jLabel35");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 14;
    vehiclePropsPanel.add(jLabel35, gridBagConstraints);

    jLabel36.setText("jLabel36");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 15;
    vehiclePropsPanel.add(jLabel36, gridBagConstraints);

    jLabel37.setText("jLabel37");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 16;
    vehiclePropsPanel.add(jLabel37, gridBagConstraints);

    jLabel38.setText("jLabel38");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 17;
    vehiclePropsPanel.add(jLabel38, gridBagConstraints);

    jLabel39.setText("jLabel39");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 18;
    vehiclePropsPanel.add(jLabel39, gridBagConstraints);

    jLabel40.setText("jLabel40");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 19;
    vehiclePropsPanel.add(jLabel40, gridBagConstraints);

    jLabel41.setText("jLabel41");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 20;
    vehiclePropsPanel.add(jLabel41, gridBagConstraints);

    jLabel42.setText("jLabel42");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 21;
    vehiclePropsPanel.add(jLabel42, gridBagConstraints);

    jLabel43.setText("jLabel43");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 22;
    vehiclePropsPanel.add(jLabel43, gridBagConstraints);

    jLabel44.setText("jLabel44");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 23;
    vehiclePropsPanel.add(jLabel44, gridBagConstraints);

    jTextField1.setEditable(false);
    jTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField1.setText("80.46");
    jTextField1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jTextField1.setEnabled(false);
    jTextField1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField1ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    vehiclePropsPanel.add(jTextField1, gridBagConstraints);

    PropsPowerInnerContainerPanel.add(vehiclePropsPanel);

    PropsPowerOuterContainerPanel.add(PropsPowerInnerContainerPanel, java.awt.BorderLayout.WEST);

    vehicleBahaviourPanel.add(PropsPowerOuterContainerPanel, java.awt.BorderLayout.NORTH);

    profilesContainerPanel.setLayout(new java.awt.BorderLayout());
    profilesContainerPanel.add(filler1, java.awt.BorderLayout.CENTER);

    vehicleBahaviourPanel.add(profilesContainerPanel, java.awt.BorderLayout.SOUTH);

    jPanel4.setLayout(new java.awt.GridBagLayout());
    vehicleBahaviourPanel.add(jPanel4, java.awt.BorderLayout.LINE_START);

    add(vehicleBahaviourPanel, java.awt.BorderLayout.CENTER);

    vehicleStatePanel.setLayout(new java.awt.BorderLayout());

    stateContainerPanel.setLayout(new javax.swing.BoxLayout(stateContainerPanel, javax.swing.BoxLayout.Y_AXIS));

    connectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(BUNDLE.getString("Adapter_status"))); // NOI18N
    connectionPanel.setName("connectionPanel"); // NOI18N
    connectionPanel.setLayout(new java.awt.GridBagLayout());

    chkBoxEnable.setText(BUNDLE.getString("Enable_communication_adapter")); // NOI18N
    chkBoxEnable.setName("chkBoxEnable"); // NOI18N
    chkBoxEnable.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        chkBoxEnableActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    connectionPanel.add(chkBoxEnable, gridBagConstraints);

    stateContainerPanel.add(connectionPanel);

    curPosPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(BUNDLE.getString("CurrentPositionSettings"))); // NOI18N
    curPosPanel.setName("curPosPanel"); // NOI18N
    curPosPanel.setLayout(new java.awt.GridBagLayout());

    energyLevelTxt.setEditable(false);
    energyLevelTxt.setBackground(new java.awt.Color(255, 255, 255));
    energyLevelTxt.setText("100");
    energyLevelTxt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    energyLevelTxt.setName("energyLevelTxt"); // NOI18N
    energyLevelTxt.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        energyLevelTxtMouseClicked(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
    curPosPanel.add(energyLevelTxt, gridBagConstraints);

    energyLevelLbl.setText("%");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    curPosPanel.add(energyLevelLbl, gridBagConstraints);

    pauseVehicleCheckBox.setEnabled(false);
    pauseVehicleCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    pauseVehicleCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
    pauseVehicleCheckBox.setName("pauseVehicleCheckBox"); // NOI18N
    pauseVehicleCheckBox.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        pauseVehicleCheckBoxItemStateChanged(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    curPosPanel.add(pauseVehicleCheckBox, gridBagConstraints);

    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/opentcs/virtualvehicle/Bundle"); // NOI18N
    orientationAngleLbl.setText(bundle.getString("AngleUnit")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    curPosPanel.add(orientationAngleLbl, gridBagConstraints);

    precisePosUnitLabel.setText(bundle.getString("precisePosUnit")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    curPosPanel.add(precisePosUnitLabel, gridBagConstraints);

    orientationAngleTxt.setEditable(false);
    orientationAngleTxt.setBackground(new java.awt.Color(255, 255, 255));
    orientationAngleTxt.setText(bundle.getString("OrientationAngleNotSet")); // NOI18N
    orientationAngleTxt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    orientationAngleTxt.setName("orientationAngleTxt"); // NOI18N
    orientationAngleTxt.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        orientationAngleTxtMouseClicked(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
    curPosPanel.add(orientationAngleTxt, gridBagConstraints);

    energyLevelLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    energyLevelLabel.setText(bundle.getString("energyLevelLabel")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
    curPosPanel.add(energyLevelLabel, gridBagConstraints);

    orientationLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    orientationLabel.setText(bundle.getString("orientationLabel")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
    curPosPanel.add(orientationLabel, gridBagConstraints);

    positionTxt.setEditable(false);
    positionTxt.setBackground(new java.awt.Color(255, 255, 255));
    positionTxt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    positionTxt.setName("positionTxt"); // NOI18N
    positionTxt.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        positionTxtMouseClicked(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    curPosPanel.add(positionTxt, gridBagConstraints);

    positionLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    positionLabel.setText(bundle.getString("positionLabel")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
    curPosPanel.add(positionLabel, gridBagConstraints);

    pauseVehicleLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    pauseVehicleLabel.setText(bundle.getString("pauseVehicle")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
    curPosPanel.add(pauseVehicleLabel, gridBagConstraints);

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel2.setText(bundle.getString("stateLabel")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
    curPosPanel.add(jLabel2, gridBagConstraints);

    stateTxt.setEditable(false);
    stateTxt.setBackground(new java.awt.Color(255, 255, 255));
    stateTxt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    stateTxt.setName("stateTxt"); // NOI18N
    stateTxt.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        stateTxtMouseClicked(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
    curPosPanel.add(stateTxt, gridBagConstraints);

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel3.setText(bundle.getString("precisePosLabel")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
    curPosPanel.add(jLabel3, gridBagConstraints);

    precisePosTextArea.setEditable(false);
    precisePosTextArea.setFont(positionTxt.getFont());
    precisePosTextArea.setRows(3);
    precisePosTextArea.setText("X:\nY:\nZ:");
    precisePosTextArea.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    precisePosTextArea.setName("precisePosTextArea"); // NOI18N
    precisePosTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        precisePosTextAreaMouseClicked(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
    curPosPanel.add(precisePosTextArea, gridBagConstraints);

    stateContainerPanel.add(curPosPanel);
    curPosPanel.getAccessibleContext().setAccessibleName("Change");

    propertySetterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("PropertySetterPanelTitel"))); // NOI18N
    propertySetterPanel.setLayout(new java.awt.GridBagLayout());

    keyLabel.setText(bundle.getString("keyPanelText")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
    propertySetterPanel.add(keyLabel, gridBagConstraints);

    valueTextField.setMaximumSize(new java.awt.Dimension(4, 18));
    valueTextField.setMinimumSize(new java.awt.Dimension(4, 18));
    valueTextField.setPreferredSize(new java.awt.Dimension(100, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    propertySetterPanel.add(valueTextField, gridBagConstraints);

    propSetButton.setText(bundle.getString("setPropertyButtonText")); // NOI18N
    propSetButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        propSetButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 2;
    propertySetterPanel.add(propSetButton, gridBagConstraints);

    propertyEditorGroup.add(removePropRadioBtn);
    removePropRadioBtn.setText(bundle.getString("removePropertyRadioButtonText")); // NOI18N
    removePropRadioBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        removePropRadioBtnActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    propertySetterPanel.add(removePropRadioBtn, gridBagConstraints);

    propertyEditorGroup.add(setPropValueRadioBtn);
    setPropValueRadioBtn.setSelected(true);
    setPropValueRadioBtn.setText(bundle.getString("setPropertyRadioButtonText")); // NOI18N
    setPropValueRadioBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        setPropValueRadioBtnActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    propertySetterPanel.add(setPropValueRadioBtn, gridBagConstraints);

    jPanel3.setLayout(new java.awt.GridBagLayout());

    keyTextField.setPreferredSize(new java.awt.Dimension(100, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    jPanel3.add(keyTextField, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    propertySetterPanel.add(jPanel3, gridBagConstraints);

    stateContainerPanel.add(propertySetterPanel);

    eventPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("Event_dispatching"))); // NOI18N
    eventPanel.setLayout(new java.awt.GridBagLayout());

    includeAppendixCheckBox.setText(bundle.getString("Include_appendix")); // NOI18N
    includeAppendixCheckBox.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        includeAppendixCheckBoxItemStateChanged(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.weightx = 1.0;
    eventPanel.add(includeAppendixCheckBox, gridBagConstraints);

    appendixTxt.setEditable(false);
    appendixTxt.setColumns(10);
    appendixTxt.setText("XYZ");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    eventPanel.add(appendixTxt, gridBagConstraints);

    dispatchEventButton.setText(bundle.getString("Dispatch_event")); // NOI18N
    dispatchEventButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        dispatchEventButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    eventPanel.add(dispatchEventButton, gridBagConstraints);

    dispatchCommandFailedButton.setText(bundle.getString("dispatchCommandFailed.button.text")); // NOI18N
    dispatchCommandFailedButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        dispatchCommandFailedButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
    eventPanel.add(dispatchCommandFailedButton, gridBagConstraints);

    stateContainerPanel.add(eventPanel);

    controlTabPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(BUNDLE.getString("Command_processing"))); // NOI18N
    controlTabPanel.setLayout(new java.awt.GridBagLayout());

    modeButtonGroup.add(singleModeRadioButton);
    singleModeRadioButton.setText(BUNDLE.getString("SingleStepMode")); // NOI18N
    singleModeRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    singleModeRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
    singleModeRadioButton.setName("singleModeRadioButton"); // NOI18N
    singleModeRadioButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        singleModeRadioButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    controlTabPanel.add(singleModeRadioButton, gridBagConstraints);

    modeButtonGroup.add(flowModeRadioButton);
    flowModeRadioButton.setSelected(true);
    flowModeRadioButton.setText(BUNDLE.getString("FlowModus")); // NOI18N
    flowModeRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    flowModeRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
    flowModeRadioButton.setName("flowModeRadioButton"); // NOI18N
    flowModeRadioButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        flowModeRadioButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    controlTabPanel.add(flowModeRadioButton, gridBagConstraints);

    triggerButton.setText(BUNDLE.getString("Next_step")); // NOI18N
    triggerButton.setEnabled(false);
    triggerButton.setName("triggerButton"); // NOI18N
    triggerButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        triggerButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
    controlTabPanel.add(triggerButton, gridBagConstraints);

    stateContainerPanel.add(controlTabPanel);

    vehicleStatePanel.add(stateContainerPanel, java.awt.BorderLayout.NORTH);

    loadDevicePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("LoadHandlingDevices"))); // NOI18N
    loadDevicePanel.setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.GridBagLayout());
    loadDevicePanel.add(jPanel1, java.awt.BorderLayout.SOUTH);

    lHDCheckbox.setText("Device loaded");
    lHDCheckbox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lHDCheckboxClicked(evt);
      }
    });
    jPanel2.add(lHDCheckbox);

    loadDevicePanel.add(jPanel2, java.awt.BorderLayout.WEST);

    vehicleStatePanel.add(loadDevicePanel, java.awt.BorderLayout.CENTER);

    add(vehicleStatePanel, java.awt.BorderLayout.WEST);

    getAccessibleContext().setAccessibleName(bundle.getString("LoopbackOptions")); // NOI18N
  }// </editor-fold>//GEN-END:initComponents

  private void singleModeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleModeRadioButtonActionPerformed
    if (singleModeRadioButton.isSelected()) {
      triggerButton.setEnabled(true);

      sendCommAdapterCommand(new SetSingleStepModeEnabledCommand(true));
    }
  }//GEN-LAST:event_singleModeRadioButtonActionPerformed

  private void flowModeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flowModeRadioButtonActionPerformed
    if (flowModeRadioButton.isSelected()) {
      triggerButton.setEnabled(false);

      sendCommAdapterCommand(new SetSingleStepModeEnabledCommand(false));
      sendCommAdapterCommand(new TriggerCommand());
    }
  }//GEN-LAST:event_flowModeRadioButtonActionPerformed

  private void triggerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_triggerButtonActionPerformed
    sendCommAdapterCommand(new TriggerCommand());
  }//GEN-LAST:event_triggerButtonActionPerformed

private void chkBoxEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBoxEnableActionPerformed
  try {
    Vehicle vehicle = callWrapper.call(() -> vehicleService.fetchObject(Vehicle.class, processModel.getVehicleName()));

    if (chkBoxEnable.isSelected()) {
      callWrapper.call(() -> vehicleService.enableCommAdapter(vehicle.getReference()));
    }
    else {
      callWrapper.call(() -> vehicleService.disableCommAdapter(vehicle.getReference()));
    }

    setStatePanelEnabled(chkBoxEnable.isSelected());
  }
  catch (Exception ex) {
    LOG.warn("Error enabling/disabling comm adapter", ex);
  }
}//GEN-LAST:event_chkBoxEnableActionPerformed


  private void precisePosTextAreaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_precisePosTextAreaMouseClicked
    if (precisePosTextArea.isEnabled()) {
      Triple pos = processModel.getPrecisePosition();
      // Create panel and dialog
      TripleTextInputPanel.Builder builder
          = new TripleTextInputPanel.Builder(BUNDLE.getString("precisePosTitle"));
      builder.setUnitLabels(BUNDLE.getString("precisePosUnit"));
      builder.setLabels(BUNDLE.getString("precisePosXLabel"),
                        BUNDLE.getString("precisePosYLabel"),
                        BUNDLE.getString("precisePosZLabel"));
      builder.enableResetButton(null);
      builder.enableValidation(TextInputPanel.TextInputValidator.REGEX_INT);
      if (pos != null) {
        builder.setInitialValues(Long.toString(pos.getX()),
                                 Long.toString(pos.getY()),
                                 Long.toString(pos.getZ()));
      }
      InputPanel panel = builder.build();
      InputDialog dialog = new InputDialog(panel);
      dialog.setVisible(true);
      // Get dialog result and set vehicle precise position
      if (dialog.getReturnStatus() == InputDialog.ReturnStatus.ACCEPTED) {
        if (dialog.getInput() == null) {
          // Clear precise position
          sendCommAdapterCommand(new SetPercisePositionCommand(null));
        }
        else {
          // Set new precise position
          long x, y, z;
          String[] newPos = (String[]) dialog.getInput();
          try {
            x = Long.parseLong(newPos[0]);
            y = Long.parseLong(newPos[1]);
            z = Long.parseLong(newPos[2]);
          }
          catch (NumberFormatException | NullPointerException e) {
            return;
          }

          sendCommAdapterCommand(new SetPercisePositionCommand(new Triple(x, y, z)));
        }
      }
    }
  }//GEN-LAST:event_precisePosTextAreaMouseClicked

  private void stateTxtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stateTxtMouseClicked
    if (stateTxt.isEnabled()) {
      List<Vehicle.State> states
          = new ArrayList<>(Arrays.asList(Vehicle.State.values()));
      Vehicle.State currentState = processModel.getVehicleState();
      // Create panel and dialog
      InputPanel panel = new DropdownListInputPanel.Builder<>(BUNDLE.getString("stateTitle"),
                                                              states)
          .setLabel(BUNDLE.getString("stateLabel"))
          .setInitialSelection(currentState)
          .build();
      InputDialog dialog = new InputDialog(panel);
      dialog.setVisible(true);
      // Get dialog results and set vahicle stare
      if (dialog.getReturnStatus() == InputDialog.ReturnStatus.ACCEPTED) {
        Vehicle.State newState = (Vehicle.State) dialog.getInput();
        if (newState != currentState) {
          sendCommAdapterCommand(new SetStateCommand(newState));
        }
      }
    }
  }//GEN-LAST:event_stateTxtMouseClicked

  private void positionTxtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_positionTxtMouseClicked
    if (positionTxt.isEnabled()) {
      // Prepare list of model points

      Set<Point> pointSet;
      try {
        pointSet = callWrapper.call(() -> vehicleService.fetchObjects(Point.class));
      }
      catch (Exception ex) {
        LOG.warn("Error fetching points", ex);
        return;
      }

      List<Point> pointList = new ArrayList<>(pointSet);
      Collections.sort(pointList, Comparators.objectsByName());
      pointList.add(0, null);
      // Get currently selected point
      // TODO is there a better way to do this?
      Point currentPoint = null;
      String currentPointName = processModel.getVehiclePosition();
      for (Point p : pointList) {
        if (p != null && p.getName().equals(currentPointName)) {
          currentPoint = p;
          break;
        }
      }
      // Create panel and dialog
      InputPanel panel = new DropdownListInputPanel.Builder<>(
          BUNDLE.getString("positionTitle"), pointList)
          .setLabel(BUNDLE.getString("positionLabel"))
          .setEditable(true)
          .setInitialSelection(currentPoint)
          .setRenderer(new StringListCellRenderer<>(x -> x == null ? "" : x.getName()))
          .build();
      InputDialog dialog = new InputDialog(panel);
      dialog.setVisible(true);
      // Get result from dialog and set vehicle position
      if (dialog.getReturnStatus() == InputDialog.ReturnStatus.ACCEPTED) {
        Object item = dialog.getInput();
        if (item == null) {
          sendCommAdapterCommand(new SetPositionCommand(null));
        }
        else {
          sendCommAdapterCommand(new SetPositionCommand(((Point) item).getName()));
        }
      }
    }
  }//GEN-LAST:event_positionTxtMouseClicked

  private void orientationAngleTxtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orientationAngleTxtMouseClicked
    if (orientationAngleTxt.isEnabled()) {
      double currentAngle = processModel.getOrientationAngle();
      String initialValue = (Double.isNaN(currentAngle) ? "" : Double.toString(currentAngle));
      // Create dialog and panel
      InputPanel panel = new SingleTextInputPanel.Builder(
          BUNDLE.getString("orientationTitle"))
          .setLabel(BUNDLE.getString("orientationLabel"))
          .setUnitLabel(BUNDLE.getString("AngleUnit"))
          .setInitialValue(initialValue)
          .enableResetButton(null)
          .enableValidation(TextInputPanel.TextInputValidator.REGEX_FLOAT)
          .build();
      InputDialog dialog = new InputDialog(panel);
      dialog.setVisible(true);
      // Get input from dialog
      InputDialog.ReturnStatus returnStatus = dialog.getReturnStatus();
      if (returnStatus == InputDialog.ReturnStatus.ACCEPTED) {
        String input = (String) dialog.getInput();
        if (input == null) { // The reset button was pressed
          if (!Double.isNaN(processModel.getOrientationAngle())) {
            sendCommAdapterCommand(new SetOrienatationAngleCommand(Double.NaN));
          }
        }
        else {
          // Set orientation provided by the user
          double angle;
          try {
            angle = Double.parseDouble(input);
          }
          catch (NumberFormatException e) {
            //TODO log message?
            return;
          }

          sendCommAdapterCommand(new SetOrienatationAngleCommand(angle));
        }
      }
    }
  }//GEN-LAST:event_orientationAngleTxtMouseClicked

  private void pauseVehicleCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_pauseVehicleCheckBoxItemStateChanged
    if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
      sendCommAdapterCommand(new SetVehiclePausedCommand(true));
    }
    else if (evt.getStateChange() == java.awt.event.ItemEvent.DESELECTED) {
      sendCommAdapterCommand(new SetVehiclePausedCommand(false));
    }
  }//GEN-LAST:event_pauseVehicleCheckBoxItemStateChanged

  private void energyLevelTxtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_energyLevelTxtMouseClicked
    if (energyLevelTxt.isEnabled()) {
      // Create panel and dialog
      InputPanel panel = new SingleTextInputPanel.Builder(
          BUNDLE.getString("energyLevelTitle"))
          .setLabel(BUNDLE.getString("energyLevelLabel"))
          .setUnitLabel("%")
          .setInitialValue(energyLevelTxt.getText())
          .enableValidation(TextInputPanel.TextInputValidator.REGEX_INT_RANGE_0_100)
          .build();
      InputDialog dialog = new InputDialog(panel);
      dialog.setVisible(true);
      // Get result from dialog and set energy level
      if (dialog.getReturnStatus() == InputDialog.ReturnStatus.ACCEPTED) {
        String input = (String) dialog.getInput();
        int energy;
        try {
          energy = Integer.parseInt(input);
        }
        catch (NumberFormatException e) {
          return;
        }

        sendCommAdapterCommand(new SetEnergyLevelCommand(energy));
      }
    }
  }//GEN-LAST:event_energyLevelTxtMouseClicked

  private void includeAppendixCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_includeAppendixCheckBoxItemStateChanged
    appendixTxt.setEditable(includeAppendixCheckBox.isSelected());
  }//GEN-LAST:event_includeAppendixCheckBoxItemStateChanged

  private void dispatchEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispatchEventButtonActionPerformed
    String appendix = includeAppendixCheckBox.isSelected() ? appendixTxt.getText() : null;
    VehicleCommAdapterEvent event = new VehicleCommAdapterEvent(processModel.getVehicleName(),
                                                                appendix);
    sendCommAdapterCommand(new PublishEventCommand(event));
  }//GEN-LAST:event_dispatchEventButtonActionPerformed

  private void dispatchCommandFailedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispatchCommandFailedButtonActionPerformed
    sendCommAdapterCommand(new CurrentMovementCommandFailedCommand());
  }//GEN-LAST:event_dispatchCommandFailedButtonActionPerformed

  private void propSetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propSetButtonActionPerformed
    sendCommAdapterCommand(new SetVehiclePropertyCommand(keyTextField.getText(),
                                                         setPropValueRadioBtn.isSelected()
                                                         ? valueTextField.getText() : null));
  }//GEN-LAST:event_propSetButtonActionPerformed

  private void removePropRadioBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePropRadioBtnActionPerformed
    valueTextField.setEnabled(false);
  }//GEN-LAST:event_removePropRadioBtnActionPerformed

  private void setPropValueRadioBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setPropValueRadioBtnActionPerformed
    valueTextField.setEnabled(true);
  }//GEN-LAST:event_setPropValueRadioBtnActionPerformed

  private void lHDCheckboxClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lHDCheckboxClicked
    List<LoadHandlingDevice> devices = Arrays.asList(
        new LoadHandlingDevice(LoopbackCommunicationAdapter.LHD_NAME, lHDCheckbox.isSelected()));
    sendCommAdapterCommand(new SetLoadHandlingDevicesCommand(devices));
  }//GEN-LAST:event_lHDCheckboxClicked

  private void opTimeTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opTimeTxtActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_opTimeTxtActionPerformed

  private void trucknameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trucknameActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_trucknameActionPerformed

  private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_jTextField6ActionPerformed

  private void jTextField8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField8ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_jTextField8ActionPerformed

  private void jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField9ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_jTextField9ActionPerformed

  private void jTextField10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField10ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_jTextField10ActionPerformed

  private void jTextField17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField17ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_jTextField17ActionPerformed

  private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_jTextField1ActionPerformed

  /**
   * Set the specified precise position to the text area. The method takes care
   * of the formatting. If any of the parameters is null all values will be set
   * to the "clear"-value.
   *
   * @param x x-position
   * @param y y-position
   * @param z z-poition
   */
  private void setPrecisePosText(Long x, Long y, Long z) {
    // Convert values to srings
    String xS, yS, zS;
    try {
      xS = x.toString();
      yS = y.toString();
      zS = z.toString();
    }
    catch (NullPointerException e) {
      xS = yS = zS = BUNDLE.getString("PrecisePosNotSet");
    }
    // Clip extremely long string values
    xS = (xS.length() > 20) ? (xS.substring(0, 20) + "...") : xS;
    yS = (yS.length() > 20) ? (yS.substring(0, 20) + "...") : yS;
    zS = (zS.length() > 20) ? (zS.substring(0, 20) + "...") : zS;
    // Build formatted text
    StringBuilder text = new StringBuilder("");
    text.append("X: ").append(xS).append("\n")
        .append("Y: ").append(yS).append("\n")
        .append("Z: ").append(zS);
    precisePosTextArea.setText(text.toString());
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel PropsPowerInnerContainerPanel;
  private javax.swing.JPanel PropsPowerOuterContainerPanel;
  private javax.swing.JLabel ambientconditionlbl;
  private javax.swing.JTextField appendixTxt;
  private javax.swing.JCheckBox chkBoxEnable;
  private javax.swing.JPanel connectionPanel;
  private javax.swing.JPanel controlTabPanel;
  private javax.swing.JLabel cruisecontrollbl;
  private javax.swing.JPanel curPosPanel;
  private javax.swing.JLabel defaultOpTimeLbl;
  private javax.swing.JLabel defaultOpTimeUntiLbl;
  private javax.swing.JButton dispatchCommandFailedButton;
  private javax.swing.JButton dispatchEventButton;
  private javax.swing.JLabel electricalpowerlbl;
  private javax.swing.JLabel energyLevelLabel;
  private javax.swing.JLabel energyLevelLbl;
  private javax.swing.JTextField energyLevelTxt;
  private javax.swing.JLabel engineTemplbl;
  private javax.swing.JLabel enginefluid2lbl;
  private javax.swing.JLabel enginefluidlbl;
  private javax.swing.JLabel enginehourslbl;
  private javax.swing.JPanel eventPanel;
  private javax.swing.JLabel fandrivelbl;
  private javax.swing.Box.Filler filler1;
  private javax.swing.JRadioButton flowModeRadioButton;
  private javax.swing.JLabel fuelconsumptionlbl;
  private javax.swing.JLabel fuelecolbl;
  private javax.swing.JLabel idleshutdownlbl;
  private javax.swing.JCheckBox includeAppendixCheckBox;
  private javax.swing.JLabel inletexhaustlbl;
  private javax.swing.JLabel jLabel10;
  private javax.swing.JLabel jLabel12;
  private javax.swing.JLabel jLabel14;
  private javax.swing.JLabel jLabel16;
  private javax.swing.JLabel jLabel18;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel20;
  private javax.swing.JLabel jLabel22;
  private javax.swing.JLabel jLabel24;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel35;
  private javax.swing.JLabel jLabel36;
  private javax.swing.JLabel jLabel37;
  private javax.swing.JLabel jLabel38;
  private javax.swing.JLabel jLabel39;
  private javax.swing.JLabel jLabel40;
  private javax.swing.JLabel jLabel41;
  private javax.swing.JLabel jLabel42;
  private javax.swing.JLabel jLabel43;
  private javax.swing.JLabel jLabel44;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JTextField jTextField1;
  private javax.swing.JTextField jTextField10;
  private javax.swing.JTextField jTextField11;
  private javax.swing.JTextField jTextField12;
  private javax.swing.JTextField jTextField13;
  private javax.swing.JTextField jTextField14;
  private javax.swing.JTextField jTextField15;
  private javax.swing.JTextField jTextField16;
  private javax.swing.JTextField jTextField17;
  private javax.swing.JTextField jTextField18;
  private javax.swing.JTextField jTextField19;
  private javax.swing.JTextField jTextField20;
  private javax.swing.JTextField jTextField21;
  private javax.swing.JTextField jTextField22;
  private javax.swing.JTextField jTextField6;
  private javax.swing.JTextField jTextField7;
  private javax.swing.JTextField jTextField8;
  private javax.swing.JTextField jTextField9;
  private javax.swing.JLabel keyLabel;
  private javax.swing.JTextField keyTextField;
  private javax.swing.JCheckBox lHDCheckbox;
  private javax.swing.JLabel ldleoperationlbl;
  private javax.swing.JPanel loadDevicePanel;
  private javax.swing.JLabel maxAccelLbl;
  private javax.swing.JTextField maxAccelTxt;
  private javax.swing.JLabel maxAccelUnitLbl;
  private javax.swing.JLabel maxDecelLbl;
  private javax.swing.JTextField maxDecelTxt;
  private javax.swing.JLabel maxDecelUnitLbl;
  private javax.swing.JLabel maxFwdVeloLbl;
  private javax.swing.JTextField maxFwdVeloTxt;
  private javax.swing.JLabel maxFwdVeloUnitLbl;
  private javax.swing.JLabel maxRevVeloLbl;
  private javax.swing.JTextField maxRevVeloTxt;
  private javax.swing.JLabel maxRevVeloUnitLbl;
  private javax.swing.ButtonGroup modeButtonGroup;
  private javax.swing.JTextField opTimeTxt;
  private javax.swing.JLabel orientationAngleLbl;
  private javax.swing.JTextField orientationAngleTxt;
  private javax.swing.JLabel orientationLabel;
  private javax.swing.JCheckBox pauseVehicleCheckBox;
  private javax.swing.JLabel pauseVehicleLabel;
  private javax.swing.JLabel positionLabel;
  private javax.swing.JTextField positionTxt;
  private javax.swing.JTextArea precisePosTextArea;
  private javax.swing.JLabel precisePosUnitLabel;
  private javax.swing.JPanel profilesContainerPanel;
  private javax.swing.JButton propSetButton;
  private javax.swing.ButtonGroup propertyEditorGroup;
  private javax.swing.JPanel propertySetterPanel;
  private javax.swing.JRadioButton removePropRadioBtn;
  private javax.swing.JRadioButton setPropValueRadioBtn;
  private javax.swing.JRadioButton singleModeRadioButton;
  private javax.swing.JPanel stateContainerPanel;
  private javax.swing.JTextField stateTxt;
  private javax.swing.JLabel timedatelbl;
  private javax.swing.JButton triggerButton;
  private javax.swing.JTextField truckname;
  private javax.swing.JLabel trucknamelbl;
  private javax.swing.JTextField valueTextField;
  private javax.swing.JPanel vehicleBahaviourPanel;
  private javax.swing.JPanel vehiclePropsPanel;
  private javax.swing.JPanel vehicleStatePanel;
  private javax.swing.JLabel vehicledistancelbl;
  private javax.swing.JLabel vehiclehourslbl;
  private javax.swing.JLabel vehicleidlbl;
  private javax.swing.JLabel waterfluidlbl;
  // End of variables declaration//GEN-END:variables
  // CHECKSTYLE:ON

}
