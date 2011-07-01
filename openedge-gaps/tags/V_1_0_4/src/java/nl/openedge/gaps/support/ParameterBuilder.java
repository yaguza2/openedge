/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */

package nl.openedge.gaps.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import nl.openedge.gaps.core.NotFoundException;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.groups.Group;
import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.parameters.InputException;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterInput;
import nl.openedge.gaps.core.parameters.ParameterRegistry;
import nl.openedge.gaps.core.parameters.ParameterValue;
import nl.openedge.gaps.core.parameters.SaveException;
import nl.openedge.gaps.core.parameters.impl.BooleanParameter;
import nl.openedge.gaps.core.parameters.impl.DateParameter;
import nl.openedge.gaps.core.parameters.impl.FixedSetInputConverter;
import nl.openedge.gaps.core.parameters.impl.FixedSetParameter;
import nl.openedge.gaps.core.parameters.impl.NestedParameter;
import nl.openedge.gaps.core.parameters.impl.NestedParameterValue;
import nl.openedge.gaps.core.parameters.impl.NumericParameter;
import nl.openedge.gaps.core.parameters.impl.NumericParameterValue;
import nl.openedge.gaps.core.parameters.impl.PercentageParameter;
import nl.openedge.gaps.core.parameters.impl.PercentageParameterValue;
import nl.openedge.gaps.core.parameters.impl.StringParameter;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.core.versions.VersionRegistry;
import nl.openedge.gaps.support.gapspath.GPathInterpreter;
import nl.openedge.gaps.support.gapspath.GPathInterpreterException;
import nl.openedge.gaps.support.gapspath.lexer.LexerException;
import nl.openedge.gaps.support.gapspath.parser.ParserException;
import nl.openedge.gaps.util.EntityUtil;
import nl.openedge.gaps.util.TransactionUtil;
import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class voor het eenvoudig kunnen aanmaken van - mogelijk - complexe parameter
 * structuren.
 */
public class ParameterBuilder
{
	/** tokens voor stream verwerking. */
	public static final String TAB_EN_SPACE_CHARS = "\t ";

	/** Log. */
	private Log log = LogFactory.getLog(ParameterBuilder.class);

	/** Te gebruiken versie bij constructies (null voor de huidige). */
	private Version version = null;

	/** Huidige structurele groep. */
	private StructuralGroup currentStructuralGroup = null;

	/** Huidige parameter groep. */
	private ParameterGroup currentParameterGroup = null;

	/**
	 * Parameter voor tijdelijk gebruik (generatie id's geneste parameters).
	 * Tevens indicator of deze parameter als parent dient te worden gezet ipv
	 * de parametergroep. */
	private NestedParameter topParam = null;

	/** De te gebruiken context bij het creeeren van de waarden; mag 'null' zijn. */
	private Map context = null;

	/** Browser voor intern gebruik. */
	private ParameterBrowser browser = new ParameterBrowser();

	/**
	 * Construct een builder instantie. NOTE: deze klasse is niet Threadsafe.
	 */
	public ParameterBuilder()
	{
	    // niets
	}

	/**
	 * Geeft huidige structuurgroep of de root indien nog niet van buitenaf gezet.
	 * @return huidige structuurgroep of de root indien nog niet van buitenaf gezet
	 */
	protected final StructuralGroup getCurrentStructuralGroup()
	{
	    if(currentStructuralGroup == null)
	    {
	        currentStructuralGroup = ParameterRegistry.getRootGroup();
	    }
	    return currentStructuralGroup;
	}

	/**
	 * Geeft huidige parametergroep of de root indien nog niet van buitenaf gezet.
	 * @return huidige parametergroep of de root indien nog niet van buitenaf gezet
	 */
	protected final ParameterGroup getCurrentParameterGroup()
	{
	    if(currentParameterGroup == null)
	    {
	        StructuralGroup structuralGroup = getCurrentStructuralGroup();
	        currentParameterGroup = structuralGroup.getParameterChilds()[0];
	        log.warn("geen huidige parametergroup gezet... beschouw "
	                + currentParameterGroup + " van structuurgroep "
	                + structuralGroup + " als huidige");
	    }
	    return currentParameterGroup;
	}

	/*
	 * ======================== methoden voor parameters ========================
	 */

	/**
	 * Creeer een {@link Parameter} van het gegeven type, en een bijbehorende
	 * {@link ParameterValue} met de gegeven string waarde, het id en de versie
	 * dat de property is van deze builder.
	 * @param type parameter klasse
	 * @param localId het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @return een nieuwe parameter instantie
	 * @throws RegistryException bij onverwachte registry problemen
	 * @throws SaveException als de parameter niet kan worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	public Parameter createParameter(Class type, String localId, String value)
		throws RegistryException, SaveException, InputException,
		ParameterBuilderException
	{
		Parameter param = buildParameter(type, localId, value);
		saveParameter(param);
		return param;
	}

	/**
	 * Creeer een {@link Parameter}, van het gegeven type en een bijbehorende
	 * {@link ParameterValue} met de gegeven string waarde, het id en de versie
	 * @param type parameter klasse
	 * dat de property is van deze builder.
	 * @param localId het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @return een nieuwe parameter instantie
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	protected Parameter buildParameter(Class type, String localId, String value)
			throws InputException, ParameterBuilderException
	{
		checkIdNotNull(localId);
		Parameter param = null;
        try
        {
            param = (Parameter)type.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new ParameterBuilderException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new ParameterBuilderException(e);
        }
        param = prepareParameter(param, localId, value);
		return param;
	}

	/**
	 * Creeer een {@link NestedParameter}met daarin genest een rij van
	 * {@link Parameter}s op basis van het gegeven type, id's en values.
	 * @param type parameter klasse
	 * @param localId het lokale id van deze parameter
	 * @param ids de ids van de geneste parameters
	 * @param values de waarden als een string array
	 * @return een nieuwe parameter instantie
	 * @throws RegistryException bij onverwachte registry problemen
	 * @throws SaveException als de parameter niet kan worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	public NestedParameter createRow(
	        Class type, String localId, String[] ids, String[] values)
			throws RegistryException, SaveException, InputException,
			ParameterBuilderException
	{
	    int len = ids.length;
		Class[] types = new Class[len];
		for(int i = 0; i < len; i++)
		{
		    types[i] = type;
		}
		return createRow(types, localId, ids, values);
	}

	/**
	 * Creeer een {@link NestedParameter}met daarin genest een rij van
	 * {@link Parameter}s op basis van de gegeven types, id's en values.
	 * @param type parameter klassen
	 * @param localId het lokale id van deze parameter
	 * @param ids de ids van de geneste parameters
	 * @param values de waarden als een string array
	 * @return een nieuwe parameter instantie
	 * @throws RegistryException bij onverwachte registry problemen
	 * @throws SaveException als de parameter niet kan worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	public NestedParameter createRow(
	        Class[] types, String localId, String[] ids, String[] values)
			throws RegistryException, SaveException, InputException,
			ParameterBuilderException
	{
		checkIdNotNull(localId);
		NestedParameter param = new NestedParameter();
		prepareParameterProperties(param, localId);
		Parameter[] params = createNested(types, ids, values, param);
		param.addAll(params);
		NestedParameterValue paramValue = (NestedParameterValue)
			param.createValue(context, values);
		param.setValue(paramValue);
		saveParameter(param);
		return param;
	}

	/**
	 * Creeer geneste string parameters.
	 * @param type parameter klassen
     * @param ids ids
     * @param values waarden
     * @param param parameter
     * @return array van geneste parameters
	 * @throws SaveException als de parameter niet kan worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
     */
    private Parameter[] createNested(
            Class[] types, String[] ids, String[] values,
            NestedParameter param)
    		throws ParameterBuilderException, SaveException, InputException
    {
        Parameter[] params = null;
        if ((ids != null) || (values != null))
		{
			if ((ids.length != values.length))
			{
				throw new ParameterBuilderException(
						"invoer ids en values niet van gelijke grootte");
			}
			params = new Parameter[ids.length];
			int len = ids.length;
			this.topParam = param;
			try
			{
				for (int i = 0; i < len; i++)
				{
					params[i] = createParameter(types[i], ids[i], values[i]);
				}
			}
			finally
			{
			    this.topParam = null;
			}
		}
        return params;
    }

	/**
	 * Creeer een {@link StringParameter}, en een bijbehorende {@link ParameterValue}met
	 * de gegeven string waarde, het id en de versie dat de property is van deze builder.
	 * @param localId het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @return een nieuwe parameter instantie
	 * @throws RegistryException bij onverwachte registry problemen
	 * @throws SaveException als de parameter niet kan worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	public StringParameter createString(String localId, String value)
			throws RegistryException, SaveException, InputException,
			ParameterBuilderException
	{
	    return (StringParameter)createParameter(StringParameter.class, localId, value);
	}

	/**
	 * Creeer een {@link NumericParameter}, en een bijbehorende
	 * {@link NumericParameterValue}met de gegeven numerieke waarde, het id en de versie
	 * dat de property is van deze builder.
	 * @param localId het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @return een nieuwe parameter instantie
	 * @throws RegistryException bij onverwachte registry problemen
	 * @throws SaveException als de parameter niet kan worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	public NumericParameter createNumeric(String localId, String value)
			throws RegistryException, SaveException, InputException,
			ParameterBuilderException
	{
	    return (NumericParameter)createParameter(NumericParameter.class, localId, value);
	}

	/**
	 * Creeer een {@link NestedParameter}met daarin genest een rij van
	 * {@link NumericParameter}s op basis van de gegeven id's en values.
	 * @param localId het lokale id van deze parameter
	 * @param ids de ids van de geneste parameters
	 * @param values de waarden als een string array
	 * @return een nieuwe parameter instantie
	 * @throws RegistryException bij onverwachte registry problemen
	 * @throws SaveException als de parameter niet kan worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	public NestedParameter createNumericRow(String localId, String[] ids, String[] values)
			throws RegistryException, SaveException, InputException,
			ParameterBuilderException
	{
	    return createRow(NumericParameter.class, localId, ids, values);
	}

	/**
	 * Leest bestand in van inputstream en converteer naar een array van nested data en
	 * registreert deze parameters.
	 * @param localId het lokale id van deze parameter; wordt genegeerd indien
	 *   parameter 'rowIdInFirstColumn' true is
	 * @param inputStream inputstream
	 * @param startcolumn eerste kolom waar de parameters beginnen
	 * @param rowIdInFirstColumn het rij-id staat in de eerste kolom (== startrij!!).
	 * @param colIdsInFirstRow de kolomkoppen staan in de eerste rij
	 * @param tokens de tokens die gebruikt dienen te worden als scheidingsteken(s)
	 * @return array van nested parameters
	 * @throws RegistryException bij onverwachte fouten
	 * @throws SaveException indien de rij niet goed kon worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	public NestedParameter[] createNumericData(
			String localId, InputStream inputStream,
			int startcolumn, boolean rowIdInFirstColumn,
			boolean colIdsInFirstRow, String tokens)
			throws RegistryException, SaveException,
			InputException, ParameterBuilderException
	{
		List parameters = new ArrayList();
		LineNumberReader reader = null;
		String[] columnNames = null;
		try
		{
			reader = new LineNumberReader(new InputStreamReader(inputStream));
			int row = 0;
			boolean isFirst = true;
			String line;
			while ((line = reader.readLine()) != null)
			{
				line = line.trim();
				if ((!line.startsWith("#")) && (!"".equals(line)))
				{
					if(colIdsInFirstRow && (isFirst))
					{
						isFirst = false;
						columnNames = getColumnNames(line);
					}
					else
					{
						isFirst = false;
						if((!rowIdInFirstColumn) && (row > 0))
						{
							throw new ParameterBuilderException(
									"als een rijnaam niet dient te worden gelezen uit het"
									+ " bestand maar is gegeven, kan de upload maar voor"
									+ " een enkele rij gelden");
						}
						NestedParameter parameter = createRowFromLine(
								localId, line, tokens, startcolumn,
								rowIdInFirstColumn, columnNames, row);
						parameters.add(parameter);
						row++;
					}
				}
			}
		}
		catch (IOException e)
		{
			throw new ParameterBuilderException(e);
		}
		finally
		{
			try
			{
				reader.close();
				inputStream.close();
			}
			catch (IOException e)
			{
				log.error(e.getMessage(), e);
			}
		}
		NestedParameter[] params = (NestedParameter[])
			parameters.toArray(new NestedParameter[parameters.size()]);
		return params;
	}

	/**
	 * Haal de kolomnamen uit de regel.
	 * @param line de regel
	 * @return array met kolomnamen
	 */
	private String[] getColumnNames(String line)
	{
		String[] columnNames;
		StringTokenizer tk = new StringTokenizer(line);
		int tokenCount = tk.countTokens();
		columnNames = new String[tokenCount];
		for(int i = 0; i < tokenCount; i++)
		{
			columnNames[i] = tk.nextToken();
		}
		return columnNames;
	}

	/**
	 * Leest een rij in vanuit de gegeven regel en creeert een
	 * {@link NestedParameter} met de ingelezen informatie.
	 * @param localId het lokale id van deze parameter; wordt genegeerd indien
	 *   parameter 'rowIdInFirstColumn' true is
	 * @param line een regel
	 * @param tokens tokens te gebruiken als scheidingsteken(s)
	 * @param startColumn eerste kolom waar de parameters beginnen
	 * @param rowIdInFirstColumn het rij-id staat in de eerste kolom
	 * @param columnNames de kolomkoppen; als null, dan worden de kolomnummers gebruikt
	 * @param row de huidige rij (nummer)
	 * @return een rijparameter
	 * @throws SaveException indien de rij niet goed kon worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	protected NestedParameter createRowFromLine(
			String localId, String line, String tokens,
			int startColumn, boolean rowIdInFirstColumn,
			String[] columnNames, int row)
			throws SaveException, InputException, ParameterBuilderException
	{

		StringTokenizer tk = new StringTokenizer(line, tokens);
		int nbrOfTokens = tk.countTokens();
		int rowIdInFirstColumnModifier = 0;
		if(rowIdInFirstColumn)
		{
			rowIdInFirstColumnModifier = 1;
		}
		int arrayLen;
		arrayLen = nbrOfTokens - startColumn - rowIdInFirstColumnModifier;
		String[] ids = null;
		if(columnNames == null)
		{
			ids = new String[arrayLen];
		}
		else
		{
			ids = columnNames;
		}
		String[] values = new String[arrayLen];
		int kol = 0;
		String parameterName = null;
		while (tk.hasMoreTokens())
		{
			String token = tk.nextToken().trim();
			if (kol >= startColumn)
			{
				if ((kol == startColumn) && rowIdInFirstColumn)
				{
					parameterName = token;
				}
				else
				{
					int arrayCol = kol - startColumn - rowIdInFirstColumnModifier;
					if(columnNames == null)
					{
						ids[arrayCol] = String.valueOf(arrayCol);
					}
					values[arrayCol] = token;	
				}
			}
			kol++;
		}
		if(!rowIdInFirstColumn)
		{
			parameterName = localId;
		}
		NestedParameter parameter = createNumericRow(parameterName, ids, values);
		return parameter;
	}

	/**
	 * Creeer een {@link PercentageParameter}, en een bijbehorende
	 * {@link PercentageParameterValue}met de gegeven percentage waarde, het id en de
	 * versie dat de property is van deze builder. <br>
	 * Deze functie registreert de parameter direct; gebruik deze functie om direct een
	 * persistente, voor berekeningen beschikbare parameter te maken, of gebruik de
	 * methode buildPercentage om een 'speelinstantie' te maken.
	 * @param localId het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @return een nieuwe parameter instantie
	 * @throws RegistryException bij onverwachte registry problemen
	 * @throws SaveException als de parameter niet kan worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	public PercentageParameter createPercentage(String localId, String value)
			throws RegistryException, SaveException, InputException,
			ParameterBuilderException
	{
	    return (PercentageParameter)createParameter(
	            PercentageParameter.class, localId, value);
	}

	/**
	 * Creeer een {@link BooleanParameter}, en een bijbehorende {@link ParameterValue}
	 * met de gegeven waarde, het id en de versie dat de property is van deze builder, en
	 * registreer de parameter.
	 * @param localId het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @return een nieuwe parameter instantie
	 * @throws RegistryException bij onverwachte registry problemen
	 * @throws SaveException als de parameter niet kan worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	public BooleanParameter createBoolean(String localId, String value)
			throws RegistryException, SaveException, InputException,
			ParameterBuilderException
	{
	    return (BooleanParameter)createParameter(BooleanParameter.class, localId, value);
	}

	/**
	 * Creeer een {@link DateParameter}, en een bijbehorende {@link ParameterValue}met
	 * de gegeven waarde, het id en de versie dat de property is van deze builder, en
	 * registreer de parameter.
	 * @param localId het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @return een nieuwe parameter instantie
	 * @throws RegistryException bij onverwachte registry problemen
	 * @throws SaveException als de parameter niet kan worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	public DateParameter createDate(String localId, String value)
			throws RegistryException, SaveException, InputException,
			ParameterBuilderException
	{
	    return (DateParameter)createParameter(DateParameter.class, localId, value);
	}

	/**
	 * Creeer een {@link FixedSetParameter}, en een bijbehorende {@link ParameterValue}
	 * met de gegeven waarde, het id, de set toegestane waarden, en de versie dat de
	 * property is van deze builder, en registreer de parameter.
	 * @param localId het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @param inputSet de toegestane waarden
	 * @param converter te gebruiken converter, mag null zijn
	 * @return een nieuwe parameter instantie
	 * @throws RegistryException bij onverwachte registry problemen
	 * @throws SaveException als de parameter niet kan worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	public FixedSetParameter createFixedSet(String localId, String value,
			ParameterInput[] inputSet, FixedSetInputConverter converter)
			throws RegistryException, SaveException, InputException,
			ParameterBuilderException
	{
		FixedSetParameter param = buildFixedSet(localId, value, inputSet, converter);
		saveParameter(param);
		return param;
	}

	/**
	 * Creeer een {@link FixedSetParameter}, en een bijbehorende {@link ParameterValue}
	 * met de gegeven waarde, het id, de set toegestane waarden, en de versie dat de
	 * property is van deze builder.
	 * @param id het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @param inputSet de toegestane waarden
	 * @param converter te gebruiken converter, mag null zijn
	 * @return een nieuwe parameter instantie
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	protected FixedSetParameter buildFixedSet(String id, String value,
			ParameterInput[] inputSet, FixedSetInputConverter converter)
			throws InputException, ParameterBuilderException
	{

		checkIdNotNull(id);
		Parameter param = prepareParameter(
		        new FixedSetParameter(inputSet, converter), id, value);
		return (FixedSetParameter) param;
	}

	/**
	 * Controleert of het gegeven id niet-null is en gooit een
	 * {@link ParameterBuilderException}exception als dit wel het geval is.
	 * @param id het te checken id
	 * @throws ParameterBuilderException indien id null is
	 */
	protected void checkIdNotNull(String id) throws ParameterBuilderException
	{
		if (id == null)
		{
			throw new ParameterBuilderException("null invoer niet toegstaan");
		}
	}

	/**
	 * Doe wat bouw werk voor de parameter zoals het zetten van het id en de value.
	 * @param param de parameter
	 * @param localId het lokale id
	 * @param value de waarde als een string
	 * @return de gegeven parameter na wijzigingen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException bij bouw fouten
	 */
	protected Parameter prepareParameter(Parameter param, String localId, String value)
			throws InputException, ParameterBuilderException
	{

		prepareParameterProperties(param, localId);
		if (value != null)
		{
			String trimmedValue = value.trim();
			if (!trimmedValue.equals(""))
			{
				ParameterValue paramValue = param.createValue(context, value);
				param.setValue(paramValue);
			}
		}
		return param;
	}

	/**
	 * Sla parameter op.
	 * @param param de parameter
	 * @throws SaveException bij onverwachte fouten
	 */
	protected void saveParameter(Parameter param) throws SaveException
	{
		ParameterRegistry.saveParameter(param);
		ParameterRegistry.saveGroup(getCurrentStructuralGroup());
	}

	/**
	 * Zet de standaard properties op basis van de builder properties.
	 * @param param de parameter
	 * @param localId het lokale id van de parameter
	 * @throws ParameterBuilderException bij bouw fouten
	 */
	protected void prepareParameterProperties(Parameter param, String localId)
			throws ParameterBuilderException
	{
	    ParameterGroup parameterGroup = getCurrentParameterGroup();
		if (parameterGroup == null)
		{
			throw new ParameterBuilderException(
					"er is geen actieve parametergroep gezet!");
		}
		param.setLocalId(localId);
		param.setVersion(getVersionWithCheck());
		String parentPath = null;
		if (topParam != null) // zet of de top parameter als parent
		{
			param.setParent(topParam);
			parentPath = topParam.getId();
		}
		else // of de parametergroep
		{
		    param.setParameterGroup(parameterGroup);
		    parentPath = parameterGroup.getPath();
		}
		String id = EntityUtil.createId(param, parentPath);
		param.setId(id);
		parameterGroup.addParameter(param);
	}

	/* ======================== methoden voor groepen ======================== */

	/**
	 * Bepaal versie adv ofwel de gezette versie of, indien deze null is, de huidige
	 * versie van de huidige structuurgroep.
	 * @return de versie
	 * @throws ParameterBuilderException indien de versie niet kan worden bepaald
	 */
	protected Version getVersionWithCheck() throws ParameterBuilderException
	{
		if (version == null)
		{
			try
			{
				version = VersionRegistry.getCurrent(getCurrentStructuralGroup());
			}
			catch (NotFoundException e)
			{
				throw new ParameterBuilderException(e);
			}
		}
		return version;
	}

	/**
	 * Maakt een {@link StructuralGroup}.
	 * @param name lokale naam van de groep, wordt gebruikt bij id generatie
	 * @param description de omschrijving
	 * @return een nieuwe groep
	 * @throws ParameterBuilderException bij build fouten
	 * @throws RegistryException bij onverwachte registry problemen
	 * @throws SaveException als de parameter niet kan worden opgeslagen
	 */
	public StructuralGroup createStructuralGroup(String name, String description)
			throws ParameterBuilderException, RegistryException, SaveException
	{

		return createStructuralGroup(name, description, false);
	}

	/**
	 * Maakt een {@link StructuralGroup}.
	 * @param name lokale naam van de groep, wordt gebruikt bij id generatie
	 * @param description de omschrijving
	 * @param navigateTo of de builder de nieuwe groep direct op de huidige dient te
	 *            zetten (volgende groepen worden dan dus aan deze nieuw gemaakte
	 *            toegevoegd)
	 * @return een nieuwe groep
	 * @throws ParameterBuilderException bij build fouten
	 * @throws RegistryException bij onverwachte registry problemen
	 * @throws SaveException als de parameter niet kan worden opgeslagen
	 */
	public StructuralGroup createStructuralGroup(String name, String description,
			boolean navigateTo) throws ParameterBuilderException, RegistryException,
			SaveException
	{
		StructuralGroup group = buildStructuralGroup(name, description, navigateTo);
		ParameterRegistry.saveGroup(group);
		ParameterRegistry.saveGroup(getCurrentStructuralGroup()); // gewijzigde parent
		return group;
	}

	/**
	 * Maakt een {@link StructuralGroup}.
	 * @param name lokale naam van de groep, wordt gebruikt bij id generatie
	 * @param description de omschrijving
	 * @return een nieuwe groep
	 * @throws ParameterBuilderException bij build fouten
	 */
	protected StructuralGroup buildStructuralGroup(String name, String description)
			throws ParameterBuilderException
	{
		return buildStructuralGroup(name, description, false);
	}

	/**
	 * Maakt een {@link StructuralGroup}.
	 * @param name lokale naam van de groep, wordt gebruikt bij id generatie
	 * @param description de omschrijving
	 * @param navigateTo of de builder de nieuwe groep direct op de huidige dient te
	 *            zetten (volgende groepen worden dan dus aan deze nieuw gemaakte
	 *            toegevoegd)
	 * @return een nieuwe groep
	 * @throws ParameterBuilderException bij build fouten
	 */
	protected StructuralGroup buildStructuralGroup(String name, String description,
			boolean navigateTo) throws ParameterBuilderException
	{
	    StructuralGroup currentStructuralGroup = getCurrentStructuralGroup();
		checkIdNotNull(name);
		checkGroupLocalId(name);
		StructuralGroup group = new StructuralGroup();
		group.setLocalId(name);
		group.setDescription(description);
		group.setVersion(getVersionWithCheck());
		group.setParent(currentStructuralGroup);
		String id = EntityUtil.createId(group);
		group.setId(id);
		currentStructuralGroup.addStructuralChild(group);
		if (navigateTo)
		{
			setStructuralGroup(group); // zet de huidige groep op de nieuw
			// gemaakte
		}
		return group;
	}

	/**
	 * Maakt een groep. Koppelt aan parent, en koppelt de parameters indien deze zijn
	 * gegeven. Slaat de groep op in de registry.
	 * @param name lokale naam van de groep, wordt gebruikt bij id generatie
	 * @param description de omschrijving van de groep
	 * @return een instantie van {@link StructuralGroup}
	 * @throws ParameterBuilderException indien id null is
	 */
	public ParameterGroup createParameterGroup(String name, String description)
			throws ParameterBuilderException
	{
		return createParameterGroup(null, name, description, false);
	}

	/**
	 * Maakt een groep. Koppelt aan parent, en koppelt de parameters indien deze zijn
	 * gegeven. Slaat de groep op in de registry.
	 * @param extendsFrom de parametergroep waarvan de nieuwe groep dient te overerven
	 * @param name lokale naam van de groep, wordt gebruikt bij id generatie
	 * @param description de omschrijving van de groep
	 * @return een instantie van {@link StructuralGroup}
	 * @throws ParameterBuilderException indien id null is
	 */
	public ParameterGroup createParameterGroup(ParameterGroup extendsFrom, String name,
			String description) throws ParameterBuilderException
	{
		return createParameterGroup(extendsFrom, name, description, false);
	}

	/**
	 * Maakt een groep. Koppelt aan parent, en koppelt de parameters indien deze zijn
	 * gegeven. Slaat de groep op in de registry.
	 * @param name lokale naam van de groep, wordt gebruikt bij id generatie
	 * @param description de omschrijving van de groep
	 * @param navigateTo of de builder de nieuwe groep direct op de huidige dient te
	 *            zetten (volgende groepen worden dan dus aan deze nieuw gemaakte
	 *            toegevoegd)
	 * @return een instantie van {@link StructuralGroup}
	 * @throws ParameterBuilderException indien id null is
	 */
	public ParameterGroup createParameterGroup(String name, String description,
			boolean navigateTo) throws ParameterBuilderException
	{
		return createParameterGroup(null, name, description, navigateTo);
	}

	/**
	 * Maakt een groep. Koppelt aan parent, en koppelt de parameters indien deze zijn
	 * gegeven. Slaat de groep op in de registry.
	 * @param extendsFrom de parametergroep waarvan de nieuwe groep dient te overerven
	 * @param name lokale naam van de groep, wordt gebruikt bij id generatie
	 * @param description de omschrijving van de groep
	 * @param navigateTo of de builder de nieuwe groep direct op de huidige dient te
	 *            zetten (volgende groepen worden dan dus aan deze nieuw gemaakte
	 *            toegevoegd)
	 * @return een instantie van {@link StructuralGroup}
	 * @throws ParameterBuilderException indien id null is
	 */
	public ParameterGroup createParameterGroup(ParameterGroup extendsFrom, String name,
			String description, boolean navigateTo) throws ParameterBuilderException
	{
		ParameterGroup group = buildParameterGroup(
				extendsFrom, name, description, navigateTo);
		ParameterRegistry.saveGroup(group);
		ParameterRegistry.saveGroup(getCurrentStructuralGroup()); // gewijzigde parent
		return group;
	}

	/**
	 * Maakt een groep. Koppelt aan parent, en koppelt de parameters indien deze zijn
	 * gegeven.
	 * @param name lokale naam van de groep, wordt gebruikt bij id generatie
	 * @param description de omschrijving van de groep
	 * @return een instantie van {@link ParameterGroup}
	 * @throws ParameterBuilderException indien id null is
	 */
	protected ParameterGroup buildParameterGroup(String name, String description)
			throws ParameterBuilderException
	{
		return buildParameterGroup(null, name, description, false);
	}

	/**
	 * Maakt een groep. Koppelt aan parent, en koppelt de parameters indien deze zijn
	 * gegeven.
	 * @param extendsFrom de parametergroep waarvan de nieuwe groep dient te overerven
	 * @param name lokale naam van de groep, wordt gebruikt bij id generatie
	 * @param description de omschrijving van de groep
	 * @return een instantie van {@link ParameterGroup}
	 * @throws ParameterBuilderException indien id null is
	 */
	protected ParameterGroup buildParameterGroup(ParameterGroup extendsFrom, String name,
			String description) throws ParameterBuilderException
	{
		return buildParameterGroup(extendsFrom, name, description, false);
	}

	/**
	 * Maakt een groep. Koppelt aan parent, en koppelt de parameters indien deze zijn
	 * gegeven.
	 * @param name lokale naam van de groep, wordt gebruikt bij id generatie
	 * @param description de omschrijving van de groep
	 * @param navigateTo of de builder de nieuwe groep direct op de huidige dient te
	 *            zetten (volgende groepen worden dan dus aan deze nieuw gemaakte
	 *            toegevoegd)
	 * @return een instantie van {@link ParameterGroup}
	 * @throws ParameterBuilderException indien id null is
	 */
	protected ParameterGroup buildParameterGroup(String name, String description,
			boolean navigateTo) throws ParameterBuilderException
	{
		return buildParameterGroup(null, name, description, navigateTo);
	}

	/**
	 * Maakt een groep. Koppelt aan parent, en koppelt de parameters indien deze zijn
	 * gegeven.
	 * @param extendsFrom de parametergroep waarvan de nieuwe groep dient te overerven
	 * @param name lokale naam van de groep, wordt gebruikt bij id generatie
	 * @param description de omschrijving van de groep
	 * @param navigateTo of de builder de nieuwe groep direct op de huidige dient te
	 *            zetten (volgende groepen worden dan dus aan deze nieuw gemaakte
	 *            toegevoegd)
	 * @return een instantie van {@link ParameterGroup}
	 * @throws ParameterBuilderException indien id null is
	 */
	protected ParameterGroup buildParameterGroup(ParameterGroup extendsFrom, String name,
			String description, boolean navigateTo) throws ParameterBuilderException
	{
	    StructuralGroup currentStructuralGroup = getCurrentStructuralGroup();
		checkIdNotNull(name);
		checkGroupLocalId(name);
		checkParentOfParameterGroup(currentStructuralGroup);
		ParameterGroup group = new ParameterGroup();
		group.setLocalId(name);
		group.setDescription(description);
		group.setVersion(getVersionWithCheck());
		group.setParent(currentStructuralGroup);
		String id = EntityUtil.createId(group);
		group.setId(id);
		group.setSuperGroup(extendsFrom);
		currentStructuralGroup.addParameterChild(group);
		if (navigateTo)
		{
			setParameterGroup(group); // zet de huidige groep op de nieuw
			// gemaakte
		}
		return group;
	}

	/**
	 * Check de parent.
	 * @param parent parent group
	 * @throws ParameterBuilderException indien parent null is
	 */
	private void checkParentOfParameterGroup(StructuralGroup parent)
			throws ParameterBuilderException
	{
		if (parent == null)
		{
			throw new ParameterBuilderException("parent dient gegeven te zijn "
					+ "voor parametergroepen");
		}
	}

	/**
	 * Geeft de huidige parameterGroup.
	 * @return parameterGroup.
	 */
	public ParameterGroup getParameterGroup()
	{
		return getCurrentParameterGroup();
	}

	/**
	 * Zet de parameterGroup; hiermee wordt direct de structural group op die van de
	 * parent gezet.
	 * @param parameterGroup parameterGroup.
	 */
	public void setParameterGroup(ParameterGroup parameterGroup)
	{
		this.currentParameterGroup = parameterGroup;
		StructuralGroup currentStructuralGroup = getCurrentStructuralGroup();
		if (parameterGroup != null)
		{
			if (!(parameterGroup.getParentId().equals(currentStructuralGroup.getId())))
			{
				setStructuralGroup(parameterGroup.getParent());
			}
		}
	}

	/**
	 * Get structuralGroup.
	 * @return structuralGroup.
	 */
	public StructuralGroup getStructuralGroup()
	{
		return getCurrentStructuralGroup();
	}

	/**
	 * Zet structuralGroup.
	 * @param structuralGroup structuralGroup.
	 */
	public void setStructuralGroup(StructuralGroup structuralGroup)
	{
		checkGroupNotNull(structuralGroup);
		this.currentStructuralGroup = structuralGroup;
	}

	/**
	 * Not-null check.
	 * @param group te checken groep
	 */
	private void checkGroupNotNull(Group group)
	{
		if (group == null)
		{
			throw new IllegalArgumentException("groep moet gegeven zijn");
		}
	}

	/**
	 * Navigeer naar een groep (bijvoorbeeld /groep/subgroep), en zet deze groep op de
	 * huidige. Dit kan worden gebruikt in plaats van zelf browsen/ zoeken en aanroepen
	 * setXxGroup met dat resultaat.
	 * @param query query naar groep
	 * @throws ParameterBuilderException bij parse fouten of indien het navigatie
	 *             resultaat geen groep is
	 */
	public void navigate(String query) throws ParameterBuilderException
	{
		Object result;
		try
		{
			result = browser.navigate(query);
		}
		catch (GPathInterpreterException e)
		{
			throw new ParameterBuilderException(e);
		}
		if (result instanceof StructuralGroup)
		{
			setStructuralGroup((StructuralGroup) result);
		}
		else if (result instanceof ParameterGroup)
		{
			setParameterGroup((ParameterGroup) result);
		}
		else
		{
			throw new ParameterBuilderException(
					"navigatie dient naar een groep te zijn (navigatie"
							+ "resultaat == " + result + ")");
		}
	}

	/**
	 * Check de groepsnaam door een test-parse uit te voeren.
	 * @param localId de te checken naam
	 * @throws ParameterBuilderException indien de naam niet kon worden geparsed
	 */
	public void checkGroupLocalId(String localId) throws ParameterBuilderException
	{
		try
		{
			GPathInterpreter.parse(localId);
		}
		catch (ParserException e)
		{
			throw new ParameterBuilderException(e);
		}
		catch (LexerException e)
		{
			throw new ParameterBuilderException(e);
		}
		catch (IOException e)
		{
			throw new ParameterBuilderException(e);
		}
	}

	/**
	 * Get versie.
	 * @return versie.
	 * @throws ParameterBuilderException indien de versie niet kan worden bepaald
	 */
	public Version getVersion() throws ParameterBuilderException
	{
		return getVersionWithCheck();
	}

	/**
	 * Set versie.
	 * @param version versie.
	 */
	public void setVersion(Version version)
	{
		this.version = version;
	}

	/**
	 * Get context.
	 * @return context.
	 */
	public Map getContext()
	{
		return context;
	}

	/**
	 * Set context.
	 * @param context context.
	 */
	public void setContext(Map context)
	{
		this.context = context;
	}

	/**
	 * Begin een batch van opdrachten.
	 * @throws ParameterBuilderException bij transactie fouten
	 */
	public void beginBatch() throws ParameterBuilderException
	{
		if(TransactionUtil.isTransactionStarted())
		{
			throw new ParameterBuilderException(
					"reeds in batchmode of voorgaande batch is niet goed afgesloten");
		}
		try
		{
			Session session = HibernateHelper.getSession();
			TransactionUtil.begin(session);
		}
		catch (HibernateException e)
		{
			throw new ParameterBuilderException(e);
		}
	}

	/**
	 * Beeindig een batch van opdrachten.
	 * @throws ParameterBuilderException bij transactie fouten
	 */
	public void commitBatch() throws ParameterBuilderException
	{
		if(!TransactionUtil.isTransactionStarted())
		{
			throw new ParameterBuilderException("niet in batchmode");
		}
		try
		{
			TransactionUtil.commit();
		}
		catch (HibernateException e)
		{
			TransactionUtil.rollback();
			throw new ParameterBuilderException(e);
		}
	}
}