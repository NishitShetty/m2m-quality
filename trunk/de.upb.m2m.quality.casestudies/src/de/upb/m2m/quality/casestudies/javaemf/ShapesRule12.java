package de.upb.m2m.quality.casestudies.javaemf;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.upb.m2m.quality.casestudies.Shapes.ModelElement;
import de.upb.m2m.quality.casestudies.Shapes.RootBlock;
import de.upb.m2m.quality.casestudies.Shapes.ShapesFactory;
import de.upb.m2m.quality.casestudies.Shapes.ShapesPackage;

/**
 * This transformation specifies a refactoring that rewrites mathematical
 * expressions without changing their value. Concretely, these two refactorings
 * are applied: 1) string1+'*'+string2 to string2+'*'+string1 if string1 <>
 * string2 2) string1+'*'+string1 to string1+'^2'
 * 
 * Implementation time: 20 Minutes.
 * 
 * @see MediniQVT Plugin "Rule12" (original scenario)
 * @version 0.1
 * @author Sebastian Lehrig (lehrig@mail.upb.de)
 */
public class ShapesRule12 {
	/**
	 * The Apache logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(ShapesRule12.class);

	/**
	 * Path to Shapes' metamodel. Important for setting the nsURI in Shapes
	 * model instance.
	 */
	private static final String METAMODEL_PATH_SHAPES = "platform:/resource/"
			+ "de.upb.m2m.quality.casestudies/"
			+ "metamodel/ShapesTutorial/Shapes.ecore";

	/**
	 * The RootBlock representing the primary entry point to the source Shapes
	 * model instance.
	 */
	private RootBlock sourceRootBlock;

	/**
	 * The RootBlock representing the primary entry point to the target Shapes
	 * model instance.
	 */
	private RootBlock targetRootBlock;

	/**
	 * Factory for creating Shapes model elements.
	 */
	private ShapesFactory factory;

	/**
	 * URI to which the transformation target is saved to.
	 */
	private URI targetURI;

	/**
	 * Default constructor. Loads the passed source Shapes model and an initial
	 * target Shapes model as member variables.
	 * 
	 * @param sourceShapesPath
	 *            Path to the source shapes model
	 * @param targetShapesPath
	 *            Path to the target shapes model
	 */
	public ShapesRule12(String sourceShapesPath, String targetShapesPath) {
		loadShapesModel(sourceShapesPath);
		initializeShapesModel();
		this.targetURI = URI.createURI(targetShapesPath);
	}

	/**
	 * Loads an existing Shapes model.
	 * 
	 * @param modelPath
	 *            Path to the Shapes model
	 * @see http://www.vogella.com/articles/EclipseEMFPersistence/article.html
	 */
	private void loadShapesModel(String modelPath) {
		// Initialize the model
		ShapesPackage.eINSTANCE.eClass();

		// Register the XMI resource factory for the .shapes extension
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("shapes", new XMIResourceFactoryImpl());

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();

		// Get the resource
		Resource resource = resSet.getResource(URI.createURI(modelPath), true);

		// Get the first model element and cast it to the right type
		EObject obj = resource.getContents().get(0);
		if (obj instanceof RootBlock) {
			sourceRootBlock = (RootBlock) obj;
		} else {
			throw new IllegalArgumentException(
					"RootBlock has to be first element in " + modelPath);
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Loaded Shapes model from " + modelPath);
		}
	}

	/**
	 * Creates a new Shapes model with one RootBlock in its root.
	 */
	private void initializeShapesModel() {
		// Initialize the model
		ShapesPackage.eINSTANCE.eClass();
		ShapesPackage.eINSTANCE.setNsURI(METAMODEL_PATH_SHAPES);

		// Retrieve the default factory singleton
		factory = ShapesFactory.eINSTANCE;

		// Create the content of the model via this program
		targetRootBlock = factory.createRootBlock();
	}

	/**
	 * Saves the contents of the targetRootBlock member variable to the
	 * specified output URI.
	 * 
	 * @see http://www.vogella.com/articles/EclipseEMFPersistence/article.html
	 */
	public void save() {
		// Register the XMI resource factory for the .xmi extension
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();

		// Create a resource
		Resource resource = resSet.createResource(this.targetURI);

		// Get the first model element and cast it to the right type
		resource.getContents().add(targetRootBlock);

		// Now save the content.
		Map<String, Object> options = new HashMap<String, Object>();
		options.put(XMIResource.OPTION_ENCODING, "UTF-8");
		try {
			resource.save(options);
		} catch (IOException e) {
			LOGGER.error("IOException when trying to save to " + this.targetURI);
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Saved Shapes model to " + this.targetURI);
		}
	}

	/**
	 * Executes the transformation from a Shapes model to a new Shapes model.
	 * First copies the source model, then selects every model element and
	 * checks for each element's name whether a refactoring should be applied to
	 * it. If this is the case, the name is refactored accordingly.
	 */
	public void transform() {
		this.targetRootBlock = copy(this.sourceRootBlock);

		List<ModelElement> modelElements = allSubobjects(targetRootBlock);

		for (ModelElement modelElement : modelElements) {
			String name = modelElement.getName();
			int index = name.indexOf("*");
			if (index != -1) {
				String before = name.substring(0, index);
				String after = name.substring(index + 1);

				if (before.equals(after)) {
					modelElement.setName(before + "^2");
				} else {
					modelElement.setName(after + "*" + before);
				}
			}
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Completed Transformation");
		}
	}

	/**
	 * Returns iteratively all sub objects of an object.
	 * 
	 * @param rootObject
	 *            The object to find the sub objects for
	 * @return A list of found sub objects
	 */
	private List<ModelElement> allSubobjects(EObject rootObject) {
		List<ModelElement> result = new LinkedList<ModelElement>();
		TreeIterator<EObject> iterator = rootObject.eAllContents();
		while (iterator.hasNext()) {
			EObject eObj = iterator.next();
			if (ModelElement.class.isAssignableFrom(eObj.getClass())) {
				result.add((ModelElement) eObj);
			}
		}
		return result;
	}

	/**
	 * Returns a copy of the given sourceRootBlock.
	 * 
	 * @param sourceRootBlock
	 *            The source root block to copy
	 * @return A copy of sourceRootBlock
	 */
	private RootBlock copy(RootBlock sourceRootBlock) {
		Copier copier = new Copier(true);
		RootBlock result = (RootBlock) copier.copy(sourceRootBlock);
		copier.copyReferences();
		return result;
	}

	/**
	 * Transformation entry point.
	 * 
	 * @param modelPaths
	 *            Array including paths to model instances involved in the
	 *            transformation
	 */
	public static void main(String[] modelPaths) {
		ShapesRule12 shapesRule12;
		try {
			shapesRule12 = new ShapesRule12(modelPaths[0], modelPaths[1]);
			shapesRule12.transform();
			shapesRule12.save();
		} catch (Exception e) {
			LOGGER.error("Transformation aborted. Reason: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
