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
import nl.openedge.gaps.core.parameters.impl.PercentageParameter;
import nl.openedge.gaps.core.parameters.impl.StringParameter;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.core.versions.VersionRegistry;
import nl.openedge.gaps.support.gapspath.GPathInterpreter;
import nl.openedge.gaps.support.gapspath.GPathInterpreterException;
import nl.openedge.gaps.support.gapspath.lexer.LexerException;
import nl.openedge.gaps.support.gapspath.parser.ParserException;
import nl.openedge.gaps.util.EntityUtil;

/**
 * Utility class voor het eenvoudig kunnen aanmaken van - mogelijk - complexe parameter
 * structuren. <br/>
 * <p>
 * De buildXxx methoden creeeren nieuwe instanties van de parameters zonder deze te
 * registreren, de createXxx methoden creeeren EN registreren de parameters.
 * </p>
 */
public class ParameterBuilder
{

	/** tegebruiken bij stream verwerking. */
	public static final String TAB_EN_SPACE_CHARS = "\t ";

	/** Te gebruiken versie bij constructies (null voor de huidige). */
	private Version version = null;

	/** Huidige structurele groep. */
	private StructuralGroup structuralGroup = null;

	/** Huidige parameter groep. */
	private ParameterGroup parameterGroup = null;

	/** Parameter voor evt tijdelijk gebruik (generatie id's geneste parameters). */
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
		structuralGroup = ParameterRegistry.getRootGroup();
		parameterGroup = structuralGroup.getParameterChilds()[0];
	}

	/*
	 * ======================== methoden voor parameters ========================
	 */

	/**
	 * Creeer een {@link StringParameter}, en een bijbehorende {@link ParameterValue}met
	 * de gegeven string waarde, het id en de versie dat de property is van deze builder.
	 * <br>
	 * Deze functie registreert de parameter direct; gebruik deze functie om direct een
	 * persistente, voor berekeningen beschikbare parameter te maken, of gebruik de
	 * methode buildString om een 'speelinstantie' te maken.
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

		StringParameter param = buildString(localId, value);
		saveParameter(param);
		return param;
	}

	/**
	 * Creeer een {@link StringParameter}, en een bijbehorende {@link ParameterValue}met
	 * de gegeven string waarde, het id en de versie dat de property is van deze builder.
	 * <br>
	 * Deze functie registreert de parameter niet; gebruik deze functie 'om te spelen' met
	 * parameters, of gebruik methode createString van deze builder voor create en
	 * registratie van de parameter.
	 * @param localId het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @return een nieuwe parameter instantie
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	protected StringParameter buildString(String localId, String value)
			throws InputException, ParameterBuilderException
	{

		checkIdNotNull(localId);
		Parameter param = prepareParameter(new StringParameter(), localId, value);
		return (StringParameter) param;
	}

	/**
	 * Creeer een {@link NumericParameter}, en een bijbehorende
	 * {@link NumericParameterValue}met de gegeven numerieke waarde, het id en de versie
	 * dat de property is van deze builder. <br>
	 * Deze functie registreert de parameter direct; gebruik deze functie om direct een
	 * persistente, voor berekeningen beschikbare parameter te maken, of gebruik de
	 * methode buildNumeric om een 'speelinstantie' te maken.
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

		NumericParameter param = buildNumeric(localId, value);
		saveParameter(param);
		return param;
	}

	/**
	 * Creeer een {@link NumericParameter}, en een bijbehorende
	 * {@link NumericParameterValue}met de gegeven numerieke waarde, het id en de versie
	 * dat de property is van deze builder. <br>
	 * Deze functie registreert de parameter niet; gebruik deze functie 'om te spelen' met
	 * parameters, of gebruik methode createNumeric van deze builder voor create en
	 * registratie van de parameter.
	 * @param localId het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @return een nieuwe parameter instantie
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	protected NumericParameter buildNumeric(String localId, String value)
			throws InputException, ParameterBuilderException
	{

		checkIdNotNull(localId);
		Parameter param = prepareParameter(new NumericParameter(), localId, value);
		return (NumericParameter) param;
	}

	/**
	 * Creeer een {@link NestedParameter}met daarin genest een rij van
	 * {@link NumericParameter}s op basis van de gegeven id's en values. <br>
	 * Deze functie registreert de parameter direct; gebruik deze functie om direct een
	 * persistente, voor berekeningen beschikbare parameter te maken, of gebruik de
	 * methode buildNumeric om een 'speelinstantie' te maken.
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

		checkIdNotNull(localId);
		NestedParameter param = new NestedParameter();
		prepareParameterProperties(param, localId);

		NumericParameter[] params = null;
		if ((ids != null) || (values != null))
		{
			if ((ids.length != values.length))
			{
				throw new ParameterBuilderException(
						"invoer ids en values niet van gelijke grootte");
			}
			params = new NumericParameter[ids.length];
			int len = ids.length;
			this.topParam = param;
			for (int i = 0; i < len; i++)
			{
				params[i] = createNumeric(ids[i], values[i]);
			}
			this.topParam = null;
		}
		param.addAll(params);
		NestedParameterValue paramValue = (NestedParameterValue) param.createValue(
				context, values);
		param.setValue(paramValue);

		saveParameter(param);
		return param;
	}

	/**
	 * Lees bestand in van inputstream en converteer naar een array van nested parameters
	 * ([][] dus).
	 * @param is inputstream
	 * @param startcol eerste kolom waar de parameters beginnen
	 * @param startrij eerste rij waar de parameterrij begint
	 * @param rijIdIsCol1 het rij-id staat in de eerste kolom
	 * @param tokens scheidingstekens.
	 * @return array van nested parameters
	 * @throws SaveException indien de rij niet goed kon worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	protected NestedParameter[] buildNumericData(InputStream is, int startcol,
			int startrij, boolean rijIdIsCol1, String tokens) throws SaveException,
			InputException, ParameterBuilderException
	{

		List nestParas = new ArrayList();
		LineNumberReader line = null;
		try
		{
			line = new LineNumberReader(new InputStreamReader(is));
			String str;
			int rij = 0;
			while ((str = line.readLine()) != null)
			{
				str = str.trim();
				if ((!str.startsWith("#")) && (!"".equals(str)))
				{
					NestedParameter parameter = readRow(str, tokens, startcol,
							rijIdIsCol1, rij);
					nestParas.add(parameter);
					rij++;
				}
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				line.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		return (NestedParameter[]) nestParas
				.toArray(new NestedParameter[nestParas.size()]);
	}

	/**
	 * Lees een rij in vanuit de gegeven regel.
	 * @param line een regel
	 * @param tokens tokens te gebruiken als scheidingsteken(s)
	 * @param startcol eerste kolom waar de parameters beginnen
	 * @param rijIdIsCol1 het rij-id staat in de eerste kolom
	 * @param rij de huidige rij (nummer)
	 * @return een rijparameter
	 * @throws SaveException indien de rij niet goed kon worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	protected NestedParameter readRow(String line, String tokens, int startcol,
			boolean rijIdIsCol1, int rij) throws SaveException, InputException,
			ParameterBuilderException
	{

		StringTokenizer tk = new StringTokenizer(line, tokens);
		int len = tk.countTokens();
		String[] ids = new String[len - startcol];
		String[] values = new String[len - startcol];
		int kol = 0 - startcol;
		StringBuffer rijNaam = new StringBuffer(String.valueOf(rij));
		while (tk.hasMoreTokens())
		{
			if (kol >= 0)
			{
				ids[kol] = String.valueOf(kol);
				values[kol] = tk.nextToken().trim();
				kol++;
			}
			else
			{
				if ((rijIdIsCol1) && (kol == (0 - startcol)))
				{
					rijNaam.setLength(0);
					rijNaam.append(tk.nextToken().trim());
				}
				kol++;
			}
		}
		NestedParameter parameter = createNumericRow(rijNaam.toString(), ids, values);

		return parameter;
	}

	/**
	 * Lees bestand in van inputstream en converteer naar een array van nested parameters
	 * ([][] dus).
	 * @param is inputstream
	 * @return array van nested parameters
	 * @throws SaveException indien de rij niet goed kon worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	protected NestedParameter[] buildNumericData(InputStream is) throws SaveException,
			InputException, ParameterBuilderException
	{

		return buildNumericData(is, 0, 0, false, TAB_EN_SPACE_CHARS);
	}

	/**
	 * Lees bestand in van inputstream en converteer naar een array van nested data en
	 * registreerd deze parameters.
	 * @param is inputstream
	 * @param startcol eerste kolom waar de parameters beginnen
	 * @param startrij eerste rij waar de parameterrij begint
	 * @param rijIdIsCol1 het rij-id staat in de eerste kolom.
	 * @param tokens de tokens die gebruikt dienen te worden als scheidingsteken(s)
	 * @return array van nested parameters
	 * @throws RegistryException bij onverwachte fouten
	 * @throws SaveException indien de rij niet goed kon worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	public NestedParameter[] createNumericData(InputStream is, int startcol,
			int startrij, boolean rijIdIsCol1, String tokens) throws RegistryException,
			SaveException, InputException, ParameterBuilderException
	{

		NestedParameter[] param = buildNumericData(is, startcol, startrij, rijIdIsCol1,
				tokens);
		for (int i = 0; i < param.length; i++)
		{
			saveParameter(param[i]);
		}
		return param;
	}

	/**
	 * Lees bestand in van inputstream en converteer naar een array van nested data en
	 * registreerd deze parameters.
	 * @param is inputstream
	 * @return array van nested parameters
	 * @throws RegistryException bij onverwachte fouten
	 * @throws SaveException indien de rij niet goed kon worden opgeslagen
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	public NestedParameter[] createNumericData(InputStream is) throws RegistryException,
			SaveException, InputException, ParameterBuilderException
	{

		return createNumericData(is, 0, 0, false, TAB_EN_SPACE_CHARS);
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

		PercentageParameter param = buildPercentage(localId, value);
		saveParameter(param);
		return param;
	}

	/**
	 * Creeer een {@link PercentageParameter}, en een bijbehorende
	 * {@link PercentageParameterValue}met de gegeven numerieke waarde, het id en de
	 * versie dat de property is van deze builder. <br>
	 * Deze functie registreert de parameter niet; gebruik deze functie 'om te spelen' met
	 * parameters, of gebruik methode createPercentage van deze builder voor create en
	 * registratie van de parameter.
	 * @param localId het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @return een nieuwe parameter instantie
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	protected PercentageParameter buildPercentage(String localId, String value)
			throws InputException, ParameterBuilderException
	{

		checkIdNotNull(localId);
		Parameter param = prepareParameter(new PercentageParameter(), localId, value);
		return (PercentageParameter) param;
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

		BooleanParameter param = buildBoolean(localId, value);
		saveParameter(param);
		return param;
	}

	/**
	 * Creeer een {@link BooleanParameter}, en een bijbehorende {@link ParameterValue}
	 * met de gegeven waarde, het id en de versie dat de property is van deze builder.
	 * @param localId het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @return een instantie van BooleanParameter
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	protected BooleanParameter buildBoolean(String localId, String value)
			throws InputException, ParameterBuilderException
	{

		checkIdNotNull(localId);
		Parameter param = prepareParameter(new BooleanParameter(), localId, value);
		return (BooleanParameter) param;
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

		DateParameter param = buildDate(localId, value);
		saveParameter(param);
		return param;
	}

	/**
	 * Creeer een {@link DateParameter}, en een bijbehorende {@link ParameterValue}met
	 * de gegeven waarde, het id en de versie dat de property is van deze builder.
	 * @param localId het lokale id
	 * @param value de waarde als een string (leeg of null indien er geen value object
	 *            dient te worden gecreeerd)
	 * @return een nieuwe parameter instantie
	 * @throws InputException bij conversie fouten van de gegeven waarde
	 * @throws ParameterBuilderException indien de builder niet in staat is de
	 *             parameter(s) te construeren
	 */
	protected DateParameter buildDate(String localId, String value)
			throws InputException, ParameterBuilderException
	{

		checkIdNotNull(localId);
		Parameter param = prepareParameter(new DateParameter(), localId, value);
		return (DateParameter) param;
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
		Parameter param = prepareParameter(new FixedSetParameter(inputSet, converter),
				id, value);
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
		ParameterRegistry.saveGroup(parameterGroup);
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

		if (parameterGroup == null)
		{
			throw new ParameterBuilderException(
					"er is geen actieve parametergroep gezet!");
		}
		param.setLocalId(localId);
		param.setGroup(parameterGroup);
		param.setVersion(getVersionWithCheck());
		if (topParam != null)
		{ // mogelijk ooit mooier oplossen...
			param.setParent(topParam);
		}
		String id = EntityUtil.createId(param);
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
				version = VersionRegistry.getCurrent(structuralGroup);
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
		ParameterRegistry.saveGroup(structuralGroup); // gewijzigde parent
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

		checkIdNotNull(name);
		checkGroupLocalId(name);
		StructuralGroup group = new StructuralGroup();
		group.setLocalId(name);
		group.setDescription(description);
		group.setVersion(getVersionWithCheck());
		group.setParent(structuralGroup);
		String id = EntityUtil.createId(group);
		group.setId(id);
		structuralGroup.addStructuralChild(group);
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

		ParameterGroup group = buildParameterGroup(extendsFrom, name, description,
				navigateTo);
		ParameterRegistry.saveGroup(group);
		ParameterRegistry.saveGroup(structuralGroup); // gewijzigde parent
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

		checkIdNotNull(name);
		checkGroupLocalId(name);
		checkParentOfParameterGroup(structuralGroup);
		ParameterGroup group = new ParameterGroup();
		group.setLocalId(name);
		group.setDescription(description);
		group.setVersion(getVersionWithCheck());
		group.setParent(structuralGroup);
		String id = EntityUtil.createId(group);
		group.setId(id);
		group.setSuperGroup(extendsFrom);
		structuralGroup.addParameterChild(group);
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
		return parameterGroup;
	}

	/**
	 * Zet de parameterGroup; hiermee wordt direct de structural group op die van de
	 * parent gezet.
	 * @param parameterGroup parameterGroup.
	 */
	public void setParameterGroup(ParameterGroup parameterGroup)
	{
		this.parameterGroup = parameterGroup;
		if (parameterGroup != null)
		{
			if (!(parameterGroup.getParentId().equals(structuralGroup.getId())))
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
		return structuralGroup;
	}

	/**
	 * Zet structuralGroup.
	 * @param structuralGroup structuralGroup.
	 */
	public void setStructuralGroup(StructuralGroup structuralGroup)
	{
		checkGroupNotNull(structuralGroup);
		this.structuralGroup = structuralGroup;
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

}