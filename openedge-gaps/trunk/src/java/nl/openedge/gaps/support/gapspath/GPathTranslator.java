/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.gapspath;

import java.util.Iterator;
import java.util.LinkedList;

import nl.openedge.gaps.core.NotFoundException;
import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterRegistry;
import nl.openedge.gaps.core.parameters.impl.NestedParameter;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.core.versions.VersionRegistry;
import nl.openedge.gaps.support.gapspath.analysis.DepthFirstAdapter;
import nl.openedge.gaps.support.gapspath.node.AArraySelect;
import nl.openedge.gaps.support.gapspath.node.AAttribute;
import nl.openedge.gaps.support.gapspath.node.AOptionsPart;
import nl.openedge.gaps.support.gapspath.node.AParam;
import nl.openedge.gaps.support.gapspath.node.AParamGroup;
import nl.openedge.gaps.support.gapspath.node.ARootStructGroup;
import nl.openedge.gaps.support.gapspath.node.AStructGroup;
import nl.openedge.gaps.support.gapspath.node.AStructGroupTail;
import nl.openedge.gaps.support.gapspath.node.Start;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Interpreteert de parse resultaten.
 */
public final class GPathTranslator extends DepthFirstAdapter
{

	/** log. */
	private static Log log = LogFactory.getLog(GPathTranslator.class);

	/** Huidige 'positie'/ executie resultaat. */
	private Object currentPosition;

	/** Bewaar de laatst benaderde struct groep. */
	private StructuralGroup lastStructGroup = null;

	/** query. */
	private String query = "";

	/** pad naar structuurgroep. */
	private String structPath;

	/** naam parametergroep. */
	private String paramGroupName;

	/** naam parameter. */
	private String paramName;

	/** evt subselect parameter. */
	private String subSelect;

	/** naam attribuut. */
	private String attribName;

	/**
	 * naam versie (mogelijk in toekomst uitbreiden met
	 * ondersteuning voor meer opties).
	 */
	private String versionName;

	/**
	 * Construct.
	 */
	public GPathTranslator()
	{
		super();
	}

	/**
	 * @see nl.openedge.gaps.support.gapspath.analysis.DepthFirstAdapter#outARootStructGroup(nl.openedge.gaps.support.gapspath.node.ARootStructGroup)
	 */
	public void outARootStructGroup(ARootStructGroup node)
	{
		this.structPath = "/";
	}

	/**
	 * @see nl.openedge.gaps.support.gapspath.analysis.DepthFirstAdapter#outAStructGroup(nl.openedge.gaps.support.gapspath.node.AStructGroup)
	 */
	public void outAStructGroup(AStructGroup node)
	{
		StringBuffer pathPart = new StringBuffer();
		if (node.getSlash() != null)
		{
			pathPart.append("/");
		}
		if (structPath == null)
		{
			structPath = "";
		}
		pathPart.append(node.getId().getText());
		LinkedList tail = node.getStructGroupTail();
		if (tail != null)
		{
			for (Iterator i = tail.iterator(); i.hasNext();)
			{
				AStructGroupTail tailPart = (AStructGroupTail) i.next();
				pathPart.append("/").append(tailPart.getId().getText());
			}
		}
		structPath = structPath + pathPart.toString();
	}

	/**
	 * @see nl.openedge.gaps.support.gapspath.analysis.DepthFirstAdapter#outAParamGroup(nl.openedge.gaps.support.gapspath.node.AParamGroup)
	 */
	public void outAParamGroup(AParamGroup node)
	{
		this.paramGroupName = node.getId().getText();
	}

	/**
	 * @see nl.openedge.gaps.support.gapspath.analysis.DepthFirstAdapter#outAParam(nl.openedge.gaps.support.gapspath.node.AParam)
	 */
	public void outAParam(AParam node)
	{
		this.paramName = node.getId().getText();
	}

	/**
	 * @see nl.openedge.gaps.support.gapspath.analysis.DepthFirstAdapter#outAArraySelect(nl.openedge.gaps.support.gapspath.node.AArraySelect)
	 */
	public void outAArraySelect(AArraySelect node)
	{
		this.subSelect = node.getSelectExpr().getText();
	}

	/**
	 * @see nl.openedge.gaps.support.gapspath.analysis.DepthFirstAdapter#outAOptionsPart(nl.openedge.gaps.support.gapspath.node.AOptionsPart)
	 */
	public void outAOptionsPart(AOptionsPart node)
	{
		String optionParam = node.getParameter().getText();
		if (!"select".equals(optionParam.toLowerCase()))
		{
			throw new GPathInterpreterException(optionParam
					+ " is geen ondersteunde optie");
		}
		String expr = node.getSelectExpr().getText();
		this.versionName = expr;
	}

	/**
	 * @see nl.openedge.gaps.support.gapspath.analysis.DepthFirstAdapter#outAAttribute(nl.openedge.gaps.support.gapspath.node.AAttribute)
	 */
	public void outAAttribute(AAttribute node)
	{
		this.attribName = node.getExpr().getText();
	}

	/**
	 * @see nl.openedge.gaps.support.gapspath.analysis.DepthFirstAdapter#inStart(nl.openedge.gaps.support.gapspath.node.Start)
	 */
	public void inStart(Start node)
	{
		reset();
	}

	/**
	 * Reset voor nieuwe query.
	 */
	private void reset()
	{
		this.structPath = null;
		this.paramGroupName = null;
		this.paramName = null;
		this.versionName = null;
		this.attribName = null;
		this.subSelect = null;
	}

	/**
	 * @see nl.openedge.gaps.support.gapspath.analysis.DepthFirstAdapter#outStart(nl.openedge.gaps.support.gapspath.node.Start)
	 */
	public void outStart(Start node)
	{
		// bepaal de versie (indien gezet)
		Version version = version = getVersion();

		// normaliseer (vertaal naar absoluut) struct pad
		normalizeStructPath();
		
		if (paramGroupName != null)
		{
			if (paramName != null)
			{
				// haal een parameter op
				Parameter param = getParameter(version);
				if (attribName != null)
				{
					// indien attribuut gegeven is, zet deze
					Object attribValue = getAttribute(param);
					this.currentPosition = attribValue;
				}
				else
				{
					// anders de parameter zelf
					this.currentPosition = param;
				}
			}
			else
			{
				// haal een parametergroep op
				// haal een structgroep op
				ParameterGroup paramGroup = getParameterGroup(version);
				this.currentPosition = paramGroup;
			}
		}
		else
		{
			// haal een structgroep op
			StructuralGroup structGroup = getStructuralGroup(version);
			this.currentPosition = structGroup;
		}
		long tsEnd = System.currentTimeMillis();
	}

	/**
	 * Normaliseer het structPath door de '.' en '..' te substitueren,
	 * en - indien het een relatief pad is - het absolute pad te herleiden
	 * adv de huidige groep.
	 */
	private void normalizeStructPath()
	{
		boolean isAbsolute = (structPath.startsWith("/"));
		StringBuffer workPath = null;
		if (isAbsolute)
		{
			workPath = new StringBuffer(structPath);
		}
		else
		{
			if (lastStructGroup != null)
			{
				workPath = new StringBuffer(lastStructGroup.getPath()
						+ "/" + structPath);
			}
			else
			{
				throw new GPathInterpreterException(
						"kan niet relatief browsen; geen actieve structuurgroep");
			}
		}
		int dotIndex;
		while ((dotIndex = workPath.indexOf(".")) != -1)
		{
			boolean isDoubleDot =
				((dotIndex + 1 < workPath.length())
						&& (workPath.charAt(dotIndex + 1) == '.'));
			if (isDoubleDot)
			{
				int from = dotIndex - 2;
				int to = dotIndex + 2; 
				from = indexOf(workPath, '/', from);
				workPath.delete(from, to);
			}
			else
			{
				int from = dotIndex - 1;
				int to = dotIndex + 1; 
				workPath.delete(from, to);
			}
		}
		structPath = workPath.toString();
	}

	/**
	 * Zoekt vanaf de gegeven index achterwaards naar het gegeven karakter in de gegeven stringbuffer.
	 * @param buffer de stringbuffer waarin wordt gezocht
	 * @param scanChar het te vinden karakter
	 * @param indexFrom vanaf waar dient te worden gezocht
	 * @return de index of 0 indien niet gevonden
	 */
	private int indexOf(StringBuffer buffer, char scanChar, int indexFrom)
	{
		int index = 0;
		int current = indexFrom;
		while(current >= 0)
		{
			if(buffer.charAt(current) == scanChar)
			{
				index = current;
				break;
			}
			current--;
		}
		return index;
	}

	/**
	 * Bepaal versie (kan null zijn).
	 * @return de versie of null indien deze niet was opgegeven
	 */
	private Version getVersion()
	{
		Version version = null;
		if (versionName != null)
		{
			try
			{
				if (log.isDebugEnabled())
				{
					log.debug("haal versie op met naam: " + versionName);
				}
				version = VersionRegistry.getVersion(versionName);
			}
			catch (NotFoundException e)
			{
				throw new GPathInterpreterException(e);
			}
		}
		return version;
	}

	/**
	 * Bepaal de structuurgroep.
	 * @param version de versie of null
	 * @return de structuurgroep
	 */
	private StructuralGroup getStructuralGroup(Version version)
	{
		StructuralGroup structGroup = null;
		try
		{
			if (version == null)
			{ // haal recente versie
				structGroup = ParameterRegistry.getStructuralGroup(structPath);
			}
			else
			{ // haal op versie op
				structGroup = ParameterRegistry.getStructuralGroup(structPath, version);
			}
		}
		catch (NotFoundException e)
		{
			log.error(e.getMessage(), e);
			throw new GPathInterpreterException(e);
		}
		this.lastStructGroup = structGroup;
		return structGroup;
	}

	/**
	 * Bepaal de parametergroep.
	 * @param version de versie of null
	 * @return de parametergroep
	 */
	private ParameterGroup getParameterGroup(Version version)
	{
		ParameterGroup paramGroup = null;
		String entityId = structPath + ":" + paramGroupName;
		try
		{
			if (version == null)
			{ // haal recente versie
				paramGroup = ParameterRegistry.getParameterGroup(entityId);
			}
			else
			{ // haal op versie op
				paramGroup = ParameterRegistry.getParameterGroup(entityId, version);
			}
		}
		catch (NotFoundException e)
		{
			log.error(e.getMessage(), e);
			throw new GPathInterpreterException(e);
		}
		this.lastStructGroup = paramGroup.getParent();
		return paramGroup;
	}

	/**
	 * Bepaal de parameter.
	 * @param version de versie of null
	 * @return de parameter
	 */
	private Parameter getParameter(Version version)
	{
		Parameter param = null;
		// gebruik de parametergroep ipv direct via de ParameterRegistry om zo
		// overerving te ondersteunen
		ParameterGroup paramGroup = getParameterGroup(version);
		String entityId = this.paramName;
		param = paramGroup.getParameter(entityId);
		if (param == null)
		{
			throw new GPathInterpreterException("parameter "
					+ entityId + " niet gevonden " + "bij parametergroep " + paramGroup);
		}
		if (subSelect != null)
		{
			if (param instanceof NestedParameter)
			{
				NestedParameter nested = (NestedParameter) param;
				param = nested.get(subSelect);
				if (param == null)
				{
					throw new GPathInterpreterException("geneste parameter "
							+ subSelect + " niet gevonden " + "bij parameter " + nested);
				}
			}
			else
			{
				throw new GPathInterpreterException(
						"kan geen subselect uitvoeren op paramters van het "
								+ "type " + param.getClass().getName());
			}
		}
		return param;
	}

	/**
	 * Bepaal attribuut.
	 * @param param de parameter
	 * @return attribuut
	 */
	private Object getAttribute(Parameter param)
	{
		Object attribValue;
		if (attribName.equals("value"))
		{
			if (param.getValue() == null)
			{
				throw new GPathInterpreterException("waarde van parameter "
						+ param.getId() + " is null");
			}
			attribValue = param.getValue().getValue();
		}
		else
		{
			throw new GPathInterpreterException("attribuut "
					+ attribName + " niet ondersteund");
		}
		return attribValue;
	}

	/**
	 * Get currentPosition.
	 * @return currentPosition.
	 */
	public Object getCurrentPosition()
	{
		return currentPosition;
	}

	/**
	 * Get query.
	 * @return query.
	 */
	public String getQuery()
	{
		return query;
	}

	/**
	 * Set query.
	 * @param lastQuery query.
	 */
	public void setQuery(String lastQuery)
	{
		this.query = lastQuery;
	}
}

