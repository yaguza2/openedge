/*
 * $Id: MasterFactory.java,v 1.4 2004/08/07 07:35:42 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/MasterFactory.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.util.XML;
import org.jdom.Element;

/**
 * Factory for creating View and Transform objects.  This calls out to specific instances
 * of ViewFactory and TransformFactory to actually create the objects.
 */
class MasterFactory
{

    /** Logger. */
    private static Log log = LogFactory.getLog(MasterFactory.class);

    /** xml attribute for type, value = 'type'. */
    public static final String ATTR_FACTORY_TYPE_NAME = "type";
    /** xml attribute for the factory provider, value = 'provider'. */
    public static final String ATTR_FACTORY_PROVIDER = "provider";
    /** xml attribute for the type name, value = 'type' */
    public static final String ATTR_TYPE_NAME = "type";
    /** xml attribute for the transform type name (for a view node), value = 'transform-type' */
    public static final String ATTR_TRANSFORM_TYPE_NAME = "transform-type";
    /** xml attribute for the path element. */
    public static final String ATTR_PATH = "path";
    /** xml tag name for a transform, value = 'transform'. */
    public static final String TAG_TRANSFORM = "transform";

    /**
     * Holds mapping of typeName to ViewFactory.
     */
    protected Map viewFactories = new HashMap();

    /**
     * Holds mapping of typeName to TransformFactory.
     */
    protected Map transformFactories = new HashMap();

    /**
     * The default type of factory to use if no explicit type is set.
     */
    protected String defaultViewType;

    /**
     * The default type of factory to use if no explicit type is set.
     */
    protected String defaultTransformType;

    /**
     * Reference to servlet config of Dispatcher servlet;
     * needed to initialize View and Transform factories.
     */
    protected ServletConfig servletCfg;

    /**
     * Create the master factory with the dispatcher servlet config.
     * @param servletCfg dispatcher servlet config
     */
    public MasterFactory(ServletConfig servletCfg)
    {
        this.servletCfg = servletCfg;
    }

    // --------- VIEWS ------------------------------------------------------

    /**
     * Sets the default type to use if one is not explicitly defined in the view node.
     * @param type the default view type
     */
    public void setDefaultViewType(String type)
    {
        this.defaultViewType = type;
    }

    /**
     * Creates a view object.
     * @param viewNode xml element for the view
     * @throws ConfigException
     * @return a View from the specified view node.
     */
    protected View createView(Element viewNode) throws ConfigException
    {
        View v = this.createPlainView(viewNode);

        final List transformsList = viewNode.getChildren(TAG_TRANSFORM);

        // Look for a "view transform", specified by the presence of a "path" attribute
        final String transformPath = viewNode.getAttributeValue(ATTR_PATH);
        if (transformPath != null)
        {
            String typeName = viewNode.getAttributeValue(ATTR_TRANSFORM_TYPE_NAME);
            if (typeName == null)
            {
                // No transform type was set explicitly so we look for a transform type
                // with the same name as that used for the view
                typeName = viewNode.getAttributeValue(ATTR_TYPE_NAME);
                if (typeName == null)
                    typeName = this.defaultViewType;
            }

            final TransformFactory tf = (TransformFactory)transformFactories.get(typeName);
            if (tf != null)
                v = new ViewWithTransforms(v, new Transform[] {tf.createTransform(viewNode)});
        }

        if (!transformsList.isEmpty())
        {
            final Transform[] t = this.createTransforms(transformsList);
            v = new ViewWithTransforms(v, t);
        }

        final Map params = XML.getParams(viewNode);
        if (params != null)
            v = new ViewWithParams(v, params);

        return v;
    }

    /**
     * Create a plain (undecorated) view object.
     * @param viewNode xml node of the view
     * @return View the plain (undecorated) view object
     * @throws ConfigException
     */
    protected View createPlainView(Element viewNode) throws ConfigException
    {
        String typeName = viewNode.getAttributeValue(ATTR_TYPE_NAME);
        if (typeName == null)
            typeName = this.defaultViewType;

        log.debug("Creating view of type " + typeName);

        ViewFactory fact = (ViewFactory)this.viewFactories.get(typeName);

        if (fact == null)
            throw new ConfigException("No view factory can be found for " + XML.toString(viewNode));

        return fact.createView(viewNode);
    }

    /**
     * Defines a view factory for the specified type; Can be used to
     * override a previously set factory.
     * @param typeName name of the view type
     * @param fact view factory
     */
    public void defineViewFactory(String typeName, ViewFactory fact)
    {
        log.info("View factory for \"" + typeName + "\" is " + fact.getClass().getName());

        this.viewFactories.put(typeName, fact);

        if (this.defaultViewType == null)
            this.defaultViewType = typeName;
    }

    /**
     * Define the view factories from the factory nodes.
     * @param viewFactoryNodes the factory nodes
     * @throws ConfigException
     */
    public void defineViewFactories(List viewFactoryNodes) throws ConfigException
    {
        // Define view factories specified in the config file.
        Iterator it = viewFactoryNodes.iterator();
        while (it.hasNext())
        {
            Element viewFactoryNode = (Element)it.next();

            String typeName = viewFactoryNode.getAttributeValue(ATTR_FACTORY_TYPE_NAME);
            String providerName = viewFactoryNode.getAttributeValue(ATTR_FACTORY_PROVIDER);

            if (typeName == null || providerName == null)
                throw new ConfigException("Not a valid view factory node:  " + XML.toString(viewFactoryNode));

            Class providerClass;
            ViewFactory instance;
            try
            {
                providerClass = loadClass(providerName);
                instance = (ViewFactory)providerClass.newInstance();
            }
            catch (Exception ex)
            {
                throw new ConfigException("Unable to define view factory for " + typeName, ex);
            }

            // Give the factory an opportunity to initialize itself from any subnodes
            instance.init(viewFactoryNode, this.servletCfg);

            this.defineViewFactory(typeName, instance);
        }
    }

    // --------- TRANSFORM DEFINES ------------------------------------------------------

    /**
     * Sets the default type to use if one is not explicitly defined in the transform node.
     * @param type the default transform type
     */
    public void setDefaultTransformType(String type)
    {
        this.defaultTransformType = type;
    }

    /**
     * Create transforms.
     * @param transformNodes transform nodes
     * @return array of transforms, possibly with length zero
     * @throws ConfigException
     */
    protected Transform[] createTransforms(List transformNodes) throws ConfigException
    {
        Transform[] retVal = new Transform[transformNodes.size()];

        int index = 0;
        Iterator it = transformNodes.iterator();
        while (it.hasNext())
        {
            Element transNode = (Element)it.next();

            retVal[index] = this.createTransform(transNode);

            index++;
        }

        return retVal;
    }

    /**
     * Creates a possibly decorated transform.
     * @param transformNode transform node
     * @return a transform object
     * @throws ConfigException
     */
    protected Transform createTransform(Element transformNode) throws ConfigException
    {
        Transform t = this.createPlainTransform(transformNode);

        Map params = XML.getParams(transformNode);
        if (params != null)
            t = new TransformWithParams(t, params);

        return t;
    }

    /**
     * Create a plain (undecorated) transform object.
     * @param transformNode transform node
     * @return transform object
     * @throws ConfigException
     */
    protected Transform createPlainTransform(Element transformNode) throws ConfigException
    {
        String typeName = transformNode.getAttributeValue(ATTR_TYPE_NAME);
        if (typeName == null)
            typeName = this.defaultTransformType;

        log.debug("Creating transform of type " + typeName);

        TransformFactory fact = (TransformFactory)this.transformFactories.get(typeName);

        if (fact == null)
            throw new ConfigException("No transform factory can be found for " + XML.toString(transformNode));

        return fact.createTransform(transformNode);
    }

    /**
     * Defines a transform factory for the specified type. Can be used to
     * override a previously set factory.
     * @param typeName name of type
     * @param fact the transform factory
     */
    public void defineTransformFactory(String typeName, TransformFactory fact)
    {
        log.info("Transform factory for \"" + typeName + "\" is " + fact.getClass().getName());

        this.transformFactories.put(typeName, fact);

        if (this.defaultTransformType == null)
            this.defaultTransformType = typeName;
    }

    /**
     * Processes a collection of transform factory nodes.
     * @param transFactoryNodes a list factory nodes
     * @throws ConfigException
     */
    public void defineTransformFactories(List transFactoryNodes) throws ConfigException
    {
        // Define transform factories specified in the config file.
        Iterator it = transFactoryNodes.iterator();
        while (it.hasNext())
        {
            Element transFactoryNode = (Element)it.next();

            String typeName = transFactoryNode.getAttributeValue(ATTR_FACTORY_TYPE_NAME);
            String providerName = transFactoryNode.getAttributeValue(ATTR_FACTORY_PROVIDER);

            if (typeName == null || providerName == null)
                throw new ConfigException("Not a valid transform factory node:  " + XML.toString(transFactoryNode));

            Class providerClass;
            TransformFactory instance;
            try
            {
                providerClass = loadClass(providerName);
                instance = (TransformFactory)providerClass.newInstance();
            }
            catch (Exception ex)
            {
                throw new ConfigException("Unable to define transform factory for " + typeName, ex);
            }

            // Give the factory an opportunity to initialize itself from any subnodes
            instance.init(transFactoryNode, this.servletCfg);

            this.defineTransformFactory(typeName, instance);
        }
    }

    // --------- UTILITY METHODS ------------------------------------------------------

    /**
     * Load class.
     * @param className full class name
     * @return Class the class
     * @throws ClassNotFoundException
     */
    protected Class loadClass(String className) throws ClassNotFoundException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null)
        {
            classLoader = DefaultControllerFactory.class.getClassLoader();
        }
        Class cls = classLoader.loadClass(className);
        return cls;
    }
}