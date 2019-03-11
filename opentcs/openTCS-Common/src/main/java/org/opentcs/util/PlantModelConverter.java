package org.opentcs.util;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

import org.opentcs.util.persistence.binding.AllowedOperationTO;
import org.opentcs.util.persistence.binding.BlockTO;
import org.opentcs.util.persistence.binding.LocationTO;
import org.opentcs.util.persistence.binding.LocationTypeTO;
import org.opentcs.util.persistence.binding.MemberTO;
import org.opentcs.util.persistence.binding.PathTO;
import org.opentcs.util.persistence.binding.PlantModelTO;
import org.opentcs.util.persistence.binding.PointTO;
import org.opentcs.util.persistence.binding.PointTO.OutgoingPath;
import org.opentcs.util.persistence.binding.PropertyTO;
import org.opentcs.util.persistence.binding.VehicleTO;
import org.opentcs.util.persistence.binding.VisualLayoutTO;
import org.opentcs.util.persistence.binding.VisualLayoutTO.Color;
import org.opentcs.util.persistence.binding.VisualLayoutTO.ModelLayoutElement;
import org.opentcs.util.persistence.binding.VisualLayoutTO.ShapeLayoutElement;
import org.opentcs.util.persistence.binding.VisualLayoutTO.ViewBookmark;
import org.opentcs.util.persistence.models.AllowedOperation;
import org.opentcs.util.persistence.models.Block;
import org.opentcs.util.persistence.models.Link;
import org.opentcs.util.persistence.models.Location;
import org.opentcs.util.persistence.models.LocationType;
import org.opentcs.util.persistence.models.Member;
import org.opentcs.util.persistence.models.Model;
///import org.opentcs.util.persistence.models.ModelColor;
//import org.opentcs.util.persistence.models.ModelShapeLayoutElement;
//import org.opentcs.util.persistence.models.ModelViewBookmark;
import org.opentcs.util.persistence.models.Path;
import org.opentcs.util.persistence.models.Point;
import org.opentcs.util.persistence.models.Property;
import org.opentcs.util.persistence.models.VehicleStatus;
import org.opentcs.util.persistence.models.Vehicle;
import org.opentcs.util.persistence.models.VisualLayout;

public final class PlantModelConverter {

	private PlantModelConverter() {

	}

	public static final PlantModelTO convertModelToPlantModelTO(final Model model) {
		final PlantModelTO plantModelTo = new PlantModelTO();
		plantModelTo.setVersion(model.getVersion());
		plantModelTo.setName(model.getName());

		final List<Block> modelBlocks = model.getBlocks();
		final List<BlockTO> blockTO = new ArrayList<>();
		for (final Block b : modelBlocks) {
			blockTO.add(blockTOFromBlock(b));
		}
		final List<PointTO> pointTO = new ArrayList<>();
		final List<Point> point = model.getPoints();
		for (final Point p : point) {
			pointTO.add(pointTOFronPoint(p));
		}
		final List<LocationTO> locationTO = new ArrayList<>();
		final List<Location> location = model.getLocations();
		for (final Location l : location) {
			locationTO.add(locationTOFromLocation(l));
		}

		final List<LocationTypeTO> locationTypeTO = new ArrayList();
		final List<LocationType> locationType = model.getLocationTypes();
		final LocationType locationtype = new LocationType();
		final List<Property> property = locationtype.getProperties();
		for (final LocationType lt : locationType) {
			locationTypeTO.add(locationTypeTOFromLocationType(lt));

		}
		final List<Path> path = model.getPaths();
		final List<PathTO> pathTO = new ArrayList();
		for (final Path p : path) {
			pathTO.add(pathTOFromPath(p));
		}
		final List<Vehicle> vehicle = model.getVehicles();
		final List<VehicleTO> vehicleTO = new ArrayList();
		for (final Vehicle v : vehicle) {
			vehicleTO.add(vehicleTOFromVehicle(v));
		}
		final List<VisualLayout> visualLayout = model.getVisualLayouts();
		final List<VisualLayoutTO> visualLayoutTO = new ArrayList();
		for (final VisualLayout v : visualLayout) {
			visualLayoutTO.add(visualLayoutTOFromVisualLayout(v));
		}

		plantModelTo.setBlocks(blockTO);
		plantModelTo.setPoints(pointTO);
		plantModelTo.setPaths(pathTO);
		plantModelTo.setVehicles(vehicleTO);
		plantModelTo.setLocations(locationTO);
		plantModelTo.setLocationTypes(locationTypeTO);
		plantModelTo.setVisualLayouts(visualLayoutTO);
		return plantModelTo;

	}

	private static BlockTO blockTOFromBlock(final Block b) {
		final List<MemberTO> memberTO = new ArrayList();
		b.getMembers().forEach(member -> {
			final MemberTO mt = new MemberTO();
			mt.setName(member.getName());
			memberTO.add(mt);
		});

		final BlockTO blockTO = new BlockTO();
		blockTO.setName(b.getName());
		blockTO.setMembers(memberTO);
		return blockTO;
	}

	private static PointTO pointTOFronPoint(final Point p) {
		final PointTO pointTO = new PointTO();
		pointTO.setName(p.getName());
    if (isNotEmpty(p.getxPosition())){
		pointTO.setxPosition(Long.parseLong(p.getxPosition()));}
    if (isNotEmpty(p.getyPosition())){
		pointTO.setyPosition(Long.parseLong(p.getyPosition()));}
    if (isNotEmpty(p.getzPosition())){
		pointTO.setzPosition(Long.parseLong(p.getzPosition()));}
    if (isNotEmpty(p.getVehicleOrientationAngle())){
		pointTO.setVehicleOrientationAngle(Float.parseFloat(p.getVehicleOrientationAngle()));}
    
		pointTO.setType(p.getType());
		final List<PointTO.OutgoingPath> op = new ArrayList<>();
		p.getOutGoingPaths().forEach(op1 -> {
			final PointTO.OutgoingPath pointTOOutgoingPath = new PointTO.OutgoingPath();
			pointTOOutgoingPath.setName(op1.getName());
			op.add(pointTOOutgoingPath);
		});
		pointTO.setOutgoingPaths(op);
		return pointTO;
	}

	private static LocationTO locationTOFromLocation(final Location l) {
		final LocationTO locationTO = new LocationTO();
    locationTO.setName(l.getName());
    if(isNotEmpty(l.getType())){
		locationTO.setType(l.getType());}
    if (isNotEmpty(l.getxPosition())){
		locationTO.setxPosition(Long.parseLong(l.getxPosition()));}
    if (isNotEmpty(l.getyPosition())){
		locationTO.setyPosition(Long.parseLong(l.getyPosition()));}
    if(isNotEmpty(l.getzPosition())){
		locationTO.setzPosition(Long.parseLong(l.getzPosition()));}
		

		final List<LocationTO.Link> linkTO = new ArrayList();
		l.getLinks().forEach(l1 -> {
			final LocationTO.Link link = new LocationTO.Link();
			link.setPoint(l1.getPoint());
      
			linkTO.add(link);
		});
		locationTO.setLinks(linkTO);
		return locationTO;
	}

	private static LocationTypeTO locationTypeTOFromLocationType(final LocationType locationType) { 
    final LocationTypeTO locationTypeTO = new LocationTypeTO();
    
    
    locationTypeTO.setName(locationType.getName());
		final List<PropertyTO> propertise = new ArrayList();
		locationType.getProperties().forEach(property -> {
			final PropertyTO pTO = new PropertyTO();
			pTO.setName(property.getName());
			pTO.setValue(property.getValue());
			propertise.add(pTO);
		});
    
    locationTypeTO.setProperties(propertise);
		
    final List<AllowedOperationTO> allowedOperationTO = new ArrayList();
		locationType.getAllowedOperations().forEach(allowedOperation -> {
			final AllowedOperationTO operationTO = new AllowedOperationTO();
			operationTO.setName(allowedOperation.getName());
						allowedOperationTO.add(operationTO);
		});
		locationTypeTO.setAllowedOperations(allowedOperationTO);
		return locationTypeTO;
	}

	private static PathTO pathTOFromPath(final Path path) {
		final PathTO pathTO = new PathTO();
		pathTO.setName(path.getName());
		pathTO.setSourcePoint(path.getSourcePoint());
		pathTO.setDestinationPoint(path.getDestinationPoint());
		if (isNotEmpty(path.getLength())){
    pathTO.setLength(Long.parseLong(path.getLength()));}
		if (isNotEmpty(path.getRoutingCost())){
    pathTO.setRoutingCost(Long.parseLong(path.getRoutingCost()));    }		
    if (isNotEmpty(path.getMaxVelocity())){
    pathTO.setMaxVelocity(Long.parseLong(path.getMaxVelocity()));}
		if (isNotEmpty(path.getMaxReverseVelocity())){
    pathTO.setMaxReverseVelocity(Long.parseLong(path.getMaxReverseVelocity()));;
    }
 
    pathTO.setLocked(path.getLocked());
		return pathTO;
	}

	private static VehicleTO vehicleTOFromVehicle(final Vehicle v) {
		final VehicleTO vehicleTO = new VehicleTO();
		vehicleTO.setName(v.getName());
		if (isNotEmpty(v.getLength())) {
			vehicleTO.setLength(Long.parseLong(v.getLength()));
		}

		if (isNotEmpty(v.getEnergyLevelCritical())) {
			vehicleTO.setEnergyLevelCritical(Long.parseLong(v.getEnergyLevelCritical()));
		}

		if (isNotEmpty(v.getEnergyLevelCritical())) {
			vehicleTO.setEnergyLevelGood(Long.parseLong(v.getEnergyLevelCritical()));
		}

		if (isNotEmpty(v.getMaxVelocity())) {
			vehicleTO.setMaxVelocity(Integer.parseInt(v.getMaxVelocity()));
		}

		if (isNotEmpty(v.getMaxReverseVelocity())) {
			vehicleTO.setMaxReverseVelocity(Integer.parseInt(v.getMaxReverseVelocity()));
		}
    
		vehicleTO.setType(v.getType());

		return vehicleTO;
	}

	

	private static VisualLayoutTO visualLayoutTOFromVisualLayout(final VisualLayout v) {
		final VisualLayoutTO visualLayoutTO = new VisualLayoutTO();
		
  final List<ModelLayoutElement> modelLayoutElement = new ArrayList<>();
		v.getModelLayoutElements().forEach(layout -> {
			final ModelLayoutElement modelLayout = new ModelLayoutElement();
			
      if(isNotEmpty(layout.getVisualizedObjectName())){
        modelLayout.setVisualizedObjectName(layout.getVisualizedObjectName());
      }
      
      if(isNotEmpty(layout.getLayer())){
			modelLayout.setLayer(Long.parseLong(layout.getLayer()));
      
      }
      
      final List<PropertyTO> propertise = new ArrayList();
			layout.getProperties().forEach(property -> {
				final PropertyTO p = new PropertyTO();
				p.setName(property.getName());
				p.setValue(property.getValue());
				propertise.add(p);
			});
			modelLayout.setProperties(propertise);
			modelLayoutElement.add(modelLayout);
		});
    visualLayoutTO.setName(v.getName());
    
    if (isNotEmpty(v.getScaleX())){
		visualLayoutTO.setScaleX(Float.parseFloat(v.getScaleX()));}
		
    if (isNotEmpty(v.getScaleY())){
    visualLayoutTO.setScaleY(Float.parseFloat(v.getScaleY()));
    }
    final List<Color> color=new ArrayList();
//    v.getColor().forEach(modelcolor->{
//    Color c=new Color();
//    c.setName(modelcolor.getName());
//    c.setRedValue(Long.parseLong(modelcolor.getRedValue()));
//    c.setGreenValue(Long.parseLong(modelcolor.getGreenValue()));
//    c.setBlueValue(Long.parseLong(modelcolor.getBlueValue()));
//    color.add(c);
//    });
//     visualLayoutTO.setColors(color);
//    final List<ShapeLayoutElement> shape=new ArrayList();
//    v.getShape().forEach(modelshape->{
//    ShapeLayoutElement s=new ShapeLayoutElement();
//    s.setLayer(Long.parseLong(modelshape.getLayer()));
//    final List<PropertyTO> propertyTO=new ArrayList();
//    modelshape.getProperties().forEach(property->{
//    final PropertyTO p = new PropertyTO();
//    p.setName(property.getName());
//    p.setValue(property.getValue());
//    propertyTO.add(p);
//    });
//    s.setProperties(propertyTO);
//    shape.add(s);
//    });
//    
//    visualLayoutTO.setShapeLayoutElements(shape);
//    
//    final List<ViewBookmark> viewBookmark=new ArrayList();
//    v.getBookmark().forEach(modelviewBookmark->{
//    ViewBookmark bookmark=new ViewBookmark();
//    bookmark.setLabel(modelviewBookmark.getLabel());
//    bookmark.setCenterX(Integer.parseInt(modelviewBookmark.getCenterX()));
//    bookmark.setCenterY(Integer.parseInt(modelviewBookmark.getCenterY()));
//    bookmark.setViewScaleX(Float.parseFloat(modelviewBookmark.getViewScaleX()));
//    bookmark.setViewScaleY(Float.parseFloat(modelviewBookmark.getViewRotation()));
//    bookmark.setViewRotation(Integer.parseInt(modelviewBookmark.getViewRotation()));
//    viewBookmark.add(bookmark);
//    });
//    visualLayoutTO.setViewBookmarks(viewBookmark);
//    
  visualLayoutTO.setModelLayoutElements(modelLayoutElement);
		return visualLayoutTO;
	}

  private static Boolean isNotEmpty(final String str) {
		return !(str == null || str.isEmpty());
	}
  
  
	public static final Model convertPlantModelTOtoDbModel(final PlantModelTO plantModelTo) {
		final Model model = new Model();
		model.setId(1);

		model.setVersion(plantModelTo.getVersion());
		model.setName(plantModelTo.getName());

		for (final PointTO pointTO : plantModelTo.getPoints()) {
			model.getPoints().add(pointTOtoPoint(pointTO, model));
		}

		// Setting Up Blocks
		final List<Block> modelBlocks = new ArrayList<>();
		plantModelTo.getBlocks().forEach(blockTO -> {
			final Block block = new Block();
			block.setName(blockTO.getName());
			final List<Member> modelMembers = new ArrayList<>();
			blockTO.getMembers().forEach(member -> {
				final Member member1 = new Member();
				member1.setName(member.getName());
				member1.setBlock(block);
				modelMembers.add(member1);
			});
			block.setMembers(modelMembers);
			block.setModel(model);
			modelBlocks.add(block);

		});
		model.setBlocks(modelBlocks);

		// Setting LocationType
		final List<LocationType> modelLoactionTypes = new ArrayList<>();




		plantModelTo.getLocationTypes().forEach(locationTypeTo -> {
			final LocationType locationType = new LocationType();
			locationType.setName(locationTypeTo.getName());
			final List<AllowedOperation> allowedOperationsModel = new ArrayList<>();
			locationTypeTo.getAllowedOperations().forEach(allowedOperation -> {
				final AllowedOperation allowedOperationForModel = new AllowedOperation();
				allowedOperationForModel.setName(allowedOperation.getName());
				allowedOperationForModel.setLocationType(locationType);
				allowedOperationsModel.add(allowedOperationForModel);
			});

			final List<Property> propertyListForLocationType = new ArrayList<>();
			locationTypeTo.getProperties().forEach(toProperty -> {
				final Property property = new Property();
				property.setName(toProperty.getName());
				property.setValue(toProperty.getValue());
				property.setLocationType(locationType);
				propertyListForLocationType.add(property);
			});

			locationType.setAllowedOperations(allowedOperationsModel);
			locationType.setProperties(propertyListForLocationType);
			locationType.setModel(model);
			modelLoactionTypes.add(locationType);
		});
		model.setLocationTypes(modelLoactionTypes);

		// setting up locations

		final List<Location> locations = new ArrayList<>();
		plantModelTo.getLocations().forEach(location -> {
			final Location locationModel = new Location();
			locationModel.setName(location.getName());
			locationModel.setxPosition(location.getxPosition().toString());
			locationModel.setyPosition(location.getyPosition().toString());
			locationModel.setzPosition(location.getzPosition().toString());
			locationModel.setType(location.getType());
			final List<Link> modelLinks = new ArrayList<>();
			location.getLinks().forEach(link -> {
				final Link modelLink = new Link();
				modelLink.setPoint(link.getPoint());
				modelLink.setLocation(locationModel);
				modelLinks.add(modelLink);
			});
			locationModel.setLinks(modelLinks);
			locationModel.setModel(model);
			locations.add(locationModel);
		});
		model.setLocations(locations);

		// setting up Paths
		final List<Path> modelPaths = new ArrayList<>();
		plantModelTo.getPaths().forEach(path -> {
			final Path modelPath = new Path();
			modelPath.setName(path.getName());
			modelPath.setSourcePoint(path.getSourcePoint());
			modelPath.setDestinationPoint(path.getDestinationPoint());
			modelPath.setLength(path.getLength().toString());
			modelPath.setRoutingCost(path.getRoutingCost().toString());
			modelPath.setMaxVelocity(path.getMaxVelocity().toString());
			modelPath.setMaxReverseVelocity(path.getMaxReverseVelocity().toString());
			modelPath.setLocked(path.isLocked());
			modelPath.setModel(model);
			modelPaths.add(modelPath);
		});
		model.setPaths(modelPaths);

		// setting up vehicles
		final List<Vehicle> modelVehicles = new ArrayList<>();
		plantModelTo.getVehicles().forEach(vehicle -> {
			final Vehicle modelVehicle = new Vehicle();
			modelVehicle.setName(vehicle.getName());
			modelVehicle.setLength(vehicle.getLength().toString());
			modelVehicle.setEnergyLevelCritical(vehicle.getEnergyLevelCritical().toString());
			modelVehicle.setEnergyLevelGood(vehicle.getEnergyLevelGood().toString());
			modelVehicle.setMaxVelocity(Integer.toString(vehicle.getMaxVelocity()));
			modelVehicle.setType(vehicle.getType());
			modelVehicle.setModel(model);
      modelVehicle.setMaxReverseVelocity(Integer.toString(vehicle.getMaxReverseVelocity()));
			modelVehicles.add(modelVehicle);
		});
		model.setVehicles(modelVehicles);

		// setting up VisualLayout
		final List<VisualLayout> visualLayouts = new ArrayList<>();
		plantModelTo.getVisualLayouts().forEach(visual -> {
			final VisualLayout visualLayout = new VisualLayout();
			visualLayout.setName(visual.getName());
			visualLayout.setScaleX(visual.getScaleX().toString());
			visualLayout.setScaleY(visual.getScaleY().toString());

//      final List<ModelColor> modelcolor=new ArrayList<>(); 
//      visual.getColors().forEach(color->{
//          final  ModelColor modelColor=new ModelColor(); 
//            modelColor.setVisualLayout(visualLayout);
//            modelColor.setName(color.getName());
//            modelColor.setRedValue(color.getRedValue().toString());
//            modelColor.setGreenValue(color.getGreenValue().toString());
//            modelColor.setBlueValue(color.getBlueValue().toString());
//            modelcolor.add(modelColor);
//      });
//      
//      visualLayout.setColor(modelcolor);
//      
//      final List<ModelShapeLayoutElement> shapelayoutelement=new ArrayList();
//      visual.getShapeLayoutElements().forEach(shapelayout->{
//        final ModelShapeLayoutElement shape=new ModelShapeLayoutElement();
//        shape.setVisualLayout(visualLayout);
//        shape.setLayer(shapelayout.getLayer().toString());
//        final List<Property> pt=new ArrayList();
//        shapelayout.getProperties().forEach(property->{
//          Property p=new Property();
//          p.setVisualLayout(visualLayout);
//          p.setName(property.getName());
//          p.setValue(property.getValue());
//          pt.add(p);
//        });
//        shape.setProperties(pt);
//        
//      });
//      
//            visualLayout.setShape(shapelayoutelement);
//
//      final List<ModelViewBookmark> viewBookmark=new ArrayList();
//            visual.getViewBookmarks().forEach(view->{
//            final ModelViewBookmark modelbookmark=new ModelViewBookmark();
//            modelbookmark.setLabel(view.getLabel());
//            modelbookmark.setCenterX(view.getCenterX().toString());
//            modelbookmark.setCenterY(view.getCenterY().toString());
//            modelbookmark.setViewScaleX(view.getViewScaleX().toString());
//            modelbookmark.setViewScaleY(view.getViewScaleY().toString());
//            modelbookmark.setViewRotation(view.getViewRotation().toString());
//            modelbookmark.setVisualLayout(visualLayout);
//            viewBookmark.add(modelbookmark);
//            });
//            
//          visualLayout.setBookmark(viewBookmark);
//            
			final List<org.opentcs.util.persistence.models.ModelLayoutElement> modelLayoutElements = new ArrayList<>();
			visual.getModelLayoutElements().forEach(layoutElement -> {
				final org.opentcs.util.persistence.models.ModelLayoutElement modelLayoutElement = new org.opentcs.util.persistence.models.ModelLayoutElement();
				modelLayoutElement.setVisualizedObjectName(layoutElement.getVisualizedObjectName());
				modelLayoutElement.setLayer(layoutElement.getLayer().toString());
				modelLayoutElement.setVisualLayout(visualLayout);

				final List<Property> property = new ArrayList<>();
				layoutElement.getProperties().forEach(propty -> {
					final Property modelProperty = new Property();
					modelProperty.setName(propty.getName());
					modelProperty.setValue(propty.getValue());
					modelProperty.setModelLayoutElement(modelLayoutElement);
					property.add(modelProperty);
				});
				modelLayoutElement.setProperties(property);
				modelLayoutElements.add(modelLayoutElement);
			});

			visualLayout.setModelLayoutElements(modelLayoutElements);

			final List<Property> visualLayoutModelProperties = new ArrayList<>();
			visual.getProperties().forEach(propty -> {
				final Property visualLayoutModelProperty = new Property();
				visualLayoutModelProperty.setName(propty.getName());
				visualLayoutModelProperty.setValue(propty.getValue());
				visualLayoutModelProperty.setVisualLayout(visualLayout);
				visualLayoutModelProperties.add(visualLayoutModelProperty);
			});

			visualLayout.setProperties(visualLayoutModelProperties);
			visualLayout.setModel(model);
			visualLayouts.add(visualLayout);

		});
		model.setVisualLayouts(visualLayouts);

		return model;
	}

	private static Point pointTOtoPoint(final PointTO pointTo, final Model model) {
		final Point point = new Point();
		point.setName(pointTo.getName());
		point.setType(pointTo.getType());
		point.setVehicleOrientationAngle(pointTo.getVehicleOrientationAngle().toString());
		point.setxPosition(pointTo.getxPosition().toString());
		point.setyPosition(pointTo.getyPosition().toString());
		point.setzPosition(pointTo.getzPosition().toString());

		final List<OutgoingPath> outgoingPathsTO = pointTo.getOutgoingPaths();
		for (final OutgoingPath outgoingPathTO : outgoingPathsTO) {
			final org.opentcs.util.persistence.models.OutgoingPath op = new org.opentcs.util.persistence.models.OutgoingPath();
			op.setName(outgoingPathTO.getName());
			point.getOutGoingPaths().add(op);
		}
		point.setModel(model);
		return point;

	}
  
}
