/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.test;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import junit.framework.TestCase;
import nl.openedge.gaps.core.NotFoundException;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.groups.Group;
import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.groups.ParameterRootGroup;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.groups.StructuralRootGroup;
import nl.openedge.gaps.core.parameters.ConversionException;
import nl.openedge.gaps.core.parameters.InputException;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterInput;
import nl.openedge.gaps.core.parameters.ParameterRegistry;
import nl.openedge.gaps.core.parameters.ParameterValue;
import nl.openedge.gaps.core.parameters.ValueOutOfRangeException;
import nl.openedge.gaps.core.parameters.impl.BooleanParameter;
import nl.openedge.gaps.core.parameters.impl.DateParameter;
import nl.openedge.gaps.core.parameters.impl.FixedSetInputConverter;
import nl.openedge.gaps.core.parameters.impl.FixedSetParameter;
import nl.openedge.gaps.core.parameters.impl.NestedParameter;
import nl.openedge.gaps.core.parameters.impl.NumericParameter;
import nl.openedge.gaps.core.parameters.impl.PercentageParameter;
import nl.openedge.gaps.core.parameters.impl.StringParameter;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.core.versions.VersionRegistry;
import nl.openedge.gaps.support.ParameterBrowser;
import nl.openedge.gaps.support.ParameterBuilder;
import nl.openedge.gaps.support.ParameterBuilderException;
import nl.openedge.gaps.support.berekeningen.BerekeningInterpreter;
import nl.openedge.gaps.util.CacheUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Parameter testen onafhankelijk van de gebruikte configuratie. Testclasses kunnen
 * hiervan extenden om de combinaties:
 * <ul>
 * <li>HSQL store</li>
 * <li>Oracle store</li>
 * <li>...</li>
 * </ul>
 * te testen. Nml, dezelfde testen dienen zich onder alle configuraties/ stores correct te
 * gedragen. De overervende klassen dienen alleen de suite methoden in te vullen om zo de
 * gewenste decorators op te kunnen starten.
 */
public abstract class AbstractParameterTest extends TestCase
{

	/** Log. */
	private static Log log = LogFactory.getLog(AbstractParameterTest.class);

	/**
	 * Construct.
	 */
	public AbstractParameterTest()
	{
		super();
	}

	/**
	 * Construct.
	 * @param name
	 */
	public AbstractParameterTest(String name)
	{
		super(name);
	}

	/**
	 * Test gedrag groepen.
	 * @throws Exception
	 */
	public void testGroepen() throws Exception
	{

		ParameterBuilder builder = new ParameterBuilder();
		ParameterBrowser browser = new ParameterBrowser();

		StructuralGroup superGroup = builder.createStructuralGroup("super", "een groep",
				true);
		StructuralGroup subgroep = builder.createStructuralGroup("subgroep",
				"een subgroep", true);
		Version oudeVersieSubgroep = subgroep.getVersion();
		StructuralGroup subSubgroep = builder.createStructuralGroup("subsubgroep",
				"een subsubgroep", false);
		ParameterGroup subParamGroep = builder.createParameterGroup(null,
				"subparamgroep", "Parameter groep onder subsubgroep", true);
		Parameter stringParam1 = builder.createString("teststring", "testwaarde");
		Version oudeVersieSubParamGroep = subParamGroep.getVersion();
		Calendar cal = Calendar.getInstance();
		cal.set(2004, 7, 31);
		Version nieuweVersieSubParamGroep = VersionRegistry.createVersion(cal.getTime(),
				"20040731", subParamGroep);
		assertFalse("de versies zouden moeten verschillen", (oudeVersieSubgroep.getName()
				.equals(nieuweVersieSubParamGroep.getName())));

		Parameter checkParam1 = (Parameter) browser
				.navigate("/super/subgroep:subparamgroep/teststring");
		assertTrue("de versies zouden gelijk moeten zijn", oudeVersieSubgroep.getName()
				.equals(checkParam1.getVersion().getName()));
		nieuweVersieSubParamGroep.setGoedgekeurd(true);
		VersionRegistry.updateVersion(nieuweVersieSubParamGroep);
//		Parameter checkParam2 = (Parameter) browser
//				.navigate("/super/subgroep:subparamgroep/teststring");

        Object pos = browser.navigate("/");
        assertNotNull(pos);
        assertTrue(pos instanceof StructuralRootGroup);
        builder.navigate("/");
        builder.createParameterGroup("rootParamGroup", "test voor groep direct onder root");
        // schoon caches, zodat we zeker weten dat de resultaten vers uit de database komen
        // zoals dit het geval zou zijn na een herstart van de applicatie
        CacheUtil.resetCache();

        // navigeer naar root
        pos = browser.navigate("/");
        assertNotNull(pos);
        assertTrue(pos instanceof StructuralRootGroup);
        StructuralGroup root = (StructuralGroup) pos;

        // check of 'super' nog steeds bekend is als structuurgroep onder de rootgroep
        List structChildIds = root.getStructuralChildIds();
        assertNotNull(structChildIds);
        assertFalse(structChildIds.isEmpty());
        StructuralGroup[] cchilds = root.getStructuralChilds();
        assertNotNull(cchilds);
        assertEquals(1, cchilds.length);
        assertEquals("super", cchilds[0].getLocalId());

        // check of er nog steeds 2 parametergroepen onder de rootgroep hangen
        //(nml de default en de groep die we hiervoor hebben toegevoegd).
        List paramChildIds = root.getParameterChildIds();
        assertNotNull(paramChildIds);
        assertFalse(paramChildIds.isEmpty());
        ParameterGroup[] pchilds = root.getParameterChilds();
        assertNotNull(pchilds);
        assertEquals(2, pchilds.length);
	}

	/**
	 * Test toevoegen, ophalen en verwijderen van een parameter.
	 * @throws Exception
	 */
	public void testSaveGetDelParameter() throws Exception
	{

		String localId = "test1";
		// creeer en sla op dmv de builder
		ParameterBuilder builder = new ParameterBuilder();
		Parameter param = builder.createNumeric(localId, null);
		String id = param.getId();
		log.info("parameter aangemaakt, id == " + id);

		// verwijder weer
		ParameterRegistry.removeParameter(param);
		try
		{
			// en test
			ParameterRegistry.getParameter(id);
			// een exception zou gegooid moeten zijn
			fail("kennelijk kan de verwijderde parameter toch nog gevonden worden");
		}
		catch (RegistryException e)
		{
			log.error(e.getMessage(), e);
			// zou niet voor mogen komen
			fail(e.getMessage());
		}
		catch (NotFoundException e)
		{
			// dit is goed
		}
	}

	/**
	 * Test parameter constructie.
	 * @throws Exception
	 */
	public void testCreatePercentage() throws Exception
	{

		ParameterBuilder builder = new ParameterBuilder();
		PercentageParameter param = null;
		param = builder.createPercentage("perc", "40,5");
		assertEquals(new Double(40.5), param.getPercentage());
		assertEquals(new Double(0.405), param.getFactor());
	}

	/**
	 * Test parameter constructie.
	 * @throws Exception
	 */
	public void testCreateString() throws Exception
	{
		ParameterBuilder builder = new ParameterBuilder();
		StringParameter param = null;
		param = builder.createString("foo", "My Name Is Bar");
		assertEquals("My Name Is Bar", param.getString());

		NestedParameter nParam = builder.createRow(
		        StringParameter.class, "nestedString",
		        new String[]{"nested1", "nested2"},
		        new String[]{"valNested1", "valNested2"});
		param = (StringParameter)nParam.get("nested1");
		assertNotNull(param);
		assertEquals("valNested1", param.getString());
		param = (StringParameter)nParam.get("nested2");
		assertNotNull(param);
		assertEquals("valNested2", param.getString());
	}

	/**
	 * Test parameter constructie.
	 * @throws Exception
	 */
	public void testCreateBoolean() throws Exception
	{

		ParameterBuilder builder = new ParameterBuilder();
		BooleanParameter param = null;
		param = builder.createBoolean("bool", "true");
		assertEquals(Boolean.TRUE, param.getBoolean());
		param = builder.createBoolean("bool", "false");
		assertEquals(Boolean.FALSE, param.getBoolean());
		param = builder.createBoolean("bool", "foobar");
		assertEquals(Boolean.FALSE, param.getBoolean());
	}

	/**
	 * Test parameter constructie.
	 * @throws Exception
	 */
	public void testCreateDate() throws Exception
	{

		ParameterBuilder builder = new ParameterBuilder();
		DateParameter param = null;
		Calendar cal1 = Calendar.getInstance();
		cal1.set(2004, 6, 31); // let op: maanden in cal van 0 - 11! 6 == juli
		param = builder.createDate("date", "31-07-2004");
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(param.getDate());
		assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
		assertEquals(cal1.get(Calendar.MONTH), cal2.get(Calendar.MONTH));
		assertEquals(cal1.get(Calendar.DATE), cal2.get(Calendar.DATE));
	}

	/**
	 * Test parameter constructie.
	 * @throws Exception
	 */
	public void testCreateFixedSet() throws Exception
	{

		ParameterBuilder builder = new ParameterBuilder();
		FixedSetParameter param = null;
		FixedSetInputConverter converter = new FixedSetInputConverter()
		{

			public Object convert(String valueAsString) throws ConversionException
			{
				return new Integer(valueAsString);
				// netter zou het zijn bij een conversie fout zelf een
				// InputException te gooien. We testen hiermee echter de
				// fallthrough van de FixedSetInputConverter
			}
		};
		ParameterInput[] inputs = createTestInputs();
		param = builder.createFixedSet("fixed", null, inputs, converter);

		ParameterValue value = null;
		value = param.createValue(null, "10"); // test geldige waarde
		assertEquals(new Integer(10), value.getValue());

		try
		{
			value = param.createValue(null, "11"); // test ongeldige waarde
			// (range check)
			fail("waarde had niet toegestaan mogen worden");
		}
		catch (InputException e)
		{
			assertTrue("verkeerde exception klasse",
					e instanceof ValueOutOfRangeException);
		}

		try
		{
			value = param.createValue(null, "foobar"); // test ongeldige waarde
			// (conversie)
			fail("waarde had niet toegestaan mogen worden");
		}
		catch (InputException e)
		{
			assertTrue("verkeerde exception klasse", e instanceof ConversionException);
		}

	}

	/**
	 * Test gebruik variabele in een expressie.
	 * @throws Exception
	 */
	public void testParameterInExpressie() throws Exception
	{

		// creeer en sla op dmv de builder
		ParameterBuilder builder = new ParameterBuilder();
		builder.createStructuralGroup("expressietest1", "test structuurgroep", true);
		ParameterGroup paramGroup = builder.createParameterGroup("pgroup",
				"test parametergroep");
		builder.setParameterGroup(paramGroup);
		Parameter numberParam = builder.createNumeric("numberparam", "12,5");
		Parameter percParam = builder.createPercentage("percparam", "50");
		NestedParameter rowParam = builder.createNumericRow("rowparam", new String[] {
				"1", "2"}, new String[] {"15", "25"});

		// test berekening met de parameter
		String expr = "(10 + :/expressietest1:pgroup/numberparam@value * 2) * "
				+ ":/expressietest1:pgroup/percparam@value";
		Double result = BerekeningInterpreter.evaluate(expr);
		assertEquals(new Double(17.5), result);

		// nog een test, nu met een row parameter
		expr = "100 + :/expressietest1:pgroup/rowparam['2']@value + 100";
		result = BerekeningInterpreter.evaluate(expr);
		assertEquals(new Double(225), result);

		// verwijder weer
		ParameterRegistry.removeParameter(numberParam);
		try
		{
			// en test
			ParameterRegistry.getParameter(numberParam.getId());
			// een exception zou gegooid moeten zijn
			fail("kennelijk kan de verwijderde parameter toch nog gevonden worden");
		}
		catch (RegistryException e)
		{
			log.error(e.getMessage(), e);
			// zou niet voor mogen komen
			fail(e.getMessage());
		}
		catch (NotFoundException e)
		{
			// dit is goed
		}
	}

	/**
	 * Test navigatie met 'gpath'.
	 * @throws Exception
	 */
	public void testNavigatie() throws Exception
	{

		ParameterBrowser browser = new ParameterBrowser();
		StructuralRootGroup sroot = (StructuralRootGroup) browser.navigate("/");
		assertNotNull(sroot);
		ParameterRootGroup proot = (ParameterRootGroup) browser.navigate("/:DEFAULT");
		assertNotNull(proot);

		ParameterBuilder builder = new ParameterBuilder();
		StructuralGroup testgroep = builder.createStructuralGroup("testgroep",
				"Dit is een testgroep");
		log.info("nieuwe groep gemaakt met id: " + testgroep.getId());
		// zet huidige groep op de nieuw gemaakte
		builder.setStructuralGroup(testgroep);
		StructuralGroup zoonGroep = builder.createStructuralGroup("zoon", "kindgroep 1");
		log.info("nieuwe groep gemaakt met id: " + zoonGroep.getId());
		StructuralGroup dochterGroep = builder.createStructuralGroup("dochter",
				"kindgroep 2");
		log.info("nieuwe groep gemaakt met id: " + dochterGroep.getId());
		// en zet huidige groep weer op de nieuw gemaakte
		builder.setStructuralGroup(dochterGroep);
		StructuralGroup kleinZoonGroep = builder.createStructuralGroup("kleinzoon",
				"kind-kindgroep");
		log.info("nieuwe groep gemaakt met id: " + kleinZoonGroep.getId());
		ParameterGroup paramGroup;
		builder.setStructuralGroup(kleinZoonGroep);
		paramGroup = builder.createParameterGroup("paramgroep", "Een parametergroep");
		log.info("nieuwe parametergroep gemaakt met id: " + paramGroup.getId());

		// test browsen
		Group current;
		current = (Group) browser.navigate("/testgroep/dochter"); // absoluut
		assertEquals(dochterGroep.getId(), current.getId());
		assertEquals(dochterGroep.getVersion().getName(), current.getVersion().getName());

		// test browsen met builder
		builder.navigate("/testgroep/dochter");
		StructuralGroup kleinDochterGroep = builder.createStructuralGroup("kleindochter",
				"Nog een kleinkind");
		current = (Group) browser.navigate("/testgroep/dochter/kleindochter");
		assertEquals(kleinDochterGroep.getId(), current.getId());
		assertEquals(kleinDochterGroep.getVersion().getName(), current.getVersion()
				.getName());

		// test toevoegen van en navigeren naar een parameter groep en
		// parameters
		builder.setStructuralGroup(kleinDochterGroep);
		ParameterGroup parameterGroep = builder.createParameterGroup("eigenschappen",
				"eigenschappen van de kleindochter", true);
		Parameter param = builder.createNumeric("leeftijd", "8");
		NestedParameter rowParam = builder.createNumericRow("testrij", new String[] {"1",
				"2", "3"}, new String[] {"100", "112", "222"});
		Object result = browser.navigate("/testgroep/dochter/kleindochter:eigenschappen");
		assertEquals(parameterGroep.getId(), ((ParameterGroup) result).getId());

		result = browser
				.navigate("/testgroep/dochter/kleindochter:eigenschappen/leeftijd");
		assertEquals(param.getId(), ((Parameter) result).getId());

		result = browser
				.navigate("/testgroep/dochter/kleindochter:eigenschappen/leeftijd@value");
		assertEquals(new Double(8), result);
		result = browser
				.navigate("/testgroep/dochter/kleindochter:eigenschappen/testrij['2']@value");
		assertEquals(new Double(112), result);
	}

	/**
	 * Test relatief browsen.
	 * @throws Exception
	 */
	public void testRelatiefBrowsen() throws Exception {
		ParameterBrowser browser = new ParameterBrowser();
		ParameterBuilder builder = new ParameterBuilder();
		StructuralGroup current = null;
		builder.navigate("/:DEFAULT");
		StructuralGroup g1 = builder.createStructuralGroup("RELTEST1", "", true);
		StructuralGroup g2 = builder.createStructuralGroup("RELTEST2", "", true);
		StructuralGroup g3 = builder.createStructuralGroup("RELTEST3", "", true);
		// '/RELTEST1/RELTEST2/RELTEST3'
		browser.navigate("/RELTEST1/RELTEST2/RELTEST3");
		current = (StructuralGroup)browser.navigate("..");
		assertEquals(g2.getId(), current.getId());
		current = (StructuralGroup)browser.navigate(".");
		assertEquals(g2.getId(), current.getId());
		current = (StructuralGroup)browser.navigate(
				"/RELTEST1/RELTEST2/../../RELTEST1/RELTEST2/../RELTEST2/./RELTEST3");
		assertEquals(g3.getId(), current.getId());
		current = (StructuralGroup)browser.navigate("../..");
		assertEquals(g1.getId(), current.getId());
		current = (StructuralGroup)browser.navigate("RELTEST2/RELTEST3");
		assertEquals(g3.getId(), current.getId());
	}

	/**
	 * Test het overerf gedrag van parametergroepen. top &lt;- super &lt;- deze
	 */
	public void testParameterGroepOvererving() throws Exception
	{

		NumericParameter param = null;
		ParameterBuilder builder = new ParameterBuilder();
		StructuralGroup superSuperGroup = builder.createStructuralGroup("top",
				"Dit is de top groep", true);
		ParameterGroup superSuperParamGroup = builder.createParameterGroup(
				"parametergroep", "Dit is de super.super parameter groep", true);
		param = builder.createNumeric("een", "1"); // wordt niet overriden
		param = builder.createNumeric("twee", "2"); // override door super
		param = builder.createNumeric("drie", "3"); // override door deze
		StructuralGroup superGroup = builder.createStructuralGroup("super",
				"Dit is de super groep", true);
		ParameterGroup superParamGroup = builder.createParameterGroup(
				superSuperParamGroup, "parametergroep",
				"Dit is de super.super parameter groep", true);
		param = builder.createNumeric("twee", "22");
		param = builder.createNumeric("drie", "33");
		StructuralGroup dezeGroup = builder.createStructuralGroup("deze",
				"Dit is de overervende groep", true);
		ParameterGroup dezeParamGroup = builder.createParameterGroup(superParamGroup,
				"parametergroep", "Dit is de super.super parameter groep", true);
		param = builder.createNumeric("drie", "333");

		Object current = null;
		ParameterBrowser browser = new ParameterBrowser();
		current = browser.evaluate("/top/super/deze:parametergroep/drie@value");
		assertEquals(new Double(333), current);
		current = browser.evaluate("/top/super/deze:parametergroep/twee@value");
		assertEquals(new Double(22), current);
		current = browser.evaluate("/top/super/deze:parametergroep/een@value");
		assertEquals(new Double(1), current);
	}

	/**
	 * Test upload functie.
	 * @throws Exception
	 */
	public void testUpload() throws Exception
	{
		Object result = null;
		NestedParameter np = null;
		InputStream is = null;
		ParameterBrowser browser = new ParameterBrowser();
		ParameterBuilder pb = new ParameterBuilder();
		pb.createStructuralGroup("datatest", "test voor uploadfunctie", true);
		pb.createParameterGroup("tabellen", "tabellen", true);

		is = AbstractParameterTest.class.getResourceAsStream("dat1.txt");
		pb.createNumericData(null, is, 0, true, false, ParameterBuilder.TAB_EN_SPACE_CHARS);
		np = (NestedParameter) browser.navigate("/datatest:tabellen/DAT_1");
		assertNotNull(np);
		result = browser.navigate("/datatest:tabellen/DAT_1['0']@value");
		assertEquals(new Double(1), result);
		result = browser.navigate("/datatest:tabellen/DAT_1['5']@value");
		assertEquals(new Double(6), result);
		result = browser.navigate("/datatest:tabellen/DAT_1['9']@value");
		assertEquals(new Double(10), result);

		is = AbstractParameterTest.class.getResourceAsStream("dat2.txt");
		pb.createNumericData("DAT_2", is, 0, false, false, ParameterBuilder.TAB_EN_SPACE_CHARS);
		np = (NestedParameter) browser.navigate("/datatest:tabellen/DAT_2");
		assertNotNull(np);
		result = browser.navigate("/datatest:tabellen/DAT_2['0']@value");
		assertEquals(new Double(11), result);
		result = browser.navigate("/datatest:tabellen/DAT_2['5']@value");
		assertEquals(new Double(16), result);
		result = browser.navigate("/datatest:tabellen/DAT_2['9']@value");
		assertEquals(new Double(20), result);

		is = AbstractParameterTest.class.getResourceAsStream("dat3.txt");
		pb.createNumericData(null, is, 2, true, false, ParameterBuilder.TAB_EN_SPACE_CHARS);
		np = (NestedParameter) browser.navigate("/datatest:tabellen/DAT_3");
		assertNotNull(np);
		result = browser.navigate("/datatest:tabellen/DAT_3['0']@value");
		assertEquals(new Double(21), result);
		result = browser.navigate("/datatest:tabellen/DAT_3['5']@value");
		assertEquals(new Double(26), result);
		result = browser.navigate("/datatest:tabellen/DAT_3['9']@value");
		assertEquals(new Double(30), result);

		is = AbstractParameterTest.class.getResourceAsStream("dat4.txt");
		pb.createNumericData("DAT_4", is, 2, false, false, ParameterBuilder.TAB_EN_SPACE_CHARS);
		np = (NestedParameter) browser.navigate("/datatest:tabellen/DAT_4");
		assertNotNull(np);
		result = browser.navigate("/datatest:tabellen/DAT_4['0']@value");
		assertEquals(new Double(31), result);
		result = browser.navigate("/datatest:tabellen/DAT_4['5']@value");
		assertEquals(new Double(36), result);
		result = browser.navigate("/datatest:tabellen/DAT_4['9']@value");
		assertEquals(new Double(40), result);

		is = AbstractParameterTest.class.getResourceAsStream("dat5.txt");
		pb.createNumericData(null, is, 0, true, false, ParameterBuilder.TAB_EN_SPACE_CHARS);
		np = (NestedParameter) browser.navigate("/datatest:tabellen/DAT_5_1");
		assertNotNull(np);
		result = browser.navigate("/datatest:tabellen/DAT_5_4['0']@value");
		assertEquals(new Double(31), result);
		result = browser.navigate("/datatest:tabellen/DAT_5_4['5']@value");
		assertEquals(new Double(36), result);
		result = browser.navigate("/datatest:tabellen/DAT_5_4['9']@value");
		assertEquals(new Double(40), result);

		is = AbstractParameterTest.class.getResourceAsStream("dat6.txt");
		pb.createNumericData(null, is, 0, true, true, ParameterBuilder.TAB_EN_SPACE_CHARS);
		np = (NestedParameter) browser.navigate("/datatest:tabellen/DAT_5_1");
		assertNotNull(np);
		result = browser.navigate("/datatest:tabellen/DAT_6_4['een']@value");
		assertEquals(new Double(31), result);
		result = browser.navigate("/datatest:tabellen/DAT_6_4['zes']@value");
		assertEquals(new Double(36), result);
		result = browser.navigate("/datatest:tabellen/DAT_6_4['tien']@value");
		assertEquals(new Double(40), result);

		try
		{
			is = AbstractParameterTest.class.getResourceAsStream("dat7.txt");
			pb.createNumericData("DAT_7", is, 0, false, false, ParameterBuilder.TAB_EN_SPACE_CHARS);
			fail("dat7.txt zou een exception moeten geven: bij een gegeven rijnaam met "
					+ " parameter 'rowIdInFirstColumn' op false is werken met meer dan "
					+ " een enkele rij niet toegestaan");
		}
		catch (ParameterBuilderException e)
		{
			log.debug("test geslaagd: " + e.getMessage());
		}

	}

	/**
	 * Creeer test inputs.
	 * @return test inputs
	 */
	private ParameterInput[] createTestInputs()
	{
		ParameterInput[] inputs = new ParameterInput[3];
		inputs[0] = new ParameterInput()
		{

			private final Integer VALUE = new Integer(10);

			public Object getValue()
			{
				return VALUE;
			}
		};
		inputs[1] = new ParameterInput()
		{

			private final Integer VALUE = new Integer(20);

			public Object getValue()
			{
				return VALUE;
			}
		};
		inputs[2] = new ParameterInput()
		{

			private final Integer VALUE = new Integer(30);

			public Object getValue()
			{
				return VALUE;
			}
		};
		return inputs;
	}

}