/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.test;

import java.util.Calendar;

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
import nl.openedge.gaps.support.berekeningen.BerekeningInterpreter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Parameter testen onafhankelijk van de gebruikte configuratie.
 * Testclasses kunnen hiervan extenden om de combinaties:
 * <ul>
 *  <li>
 *   HSQL store
 *  </li>
 *  <li>
 *   Oracle store
 *  </li>
 *  <li>
 * 	 ...
 *  </li>
 * </ul>
 * te testen. Nml, dezelfde testen dienen zich onder alle configuraties/
 * stores correct te gedragen.
 * De overervende klassen dienen alleen de suite methoden in te vullen
 * om zo de gewenste decorators op te kunnen starten.
 */
public abstract class AbstractParameterTest extends TestCase {

    /** Log. */
    private static Log log = LogFactory.getLog(AbstractParameterTest.class);

    /**
     * Construct.
     */
    public AbstractParameterTest() {
        super();
    }

    /**
     * Construct.
     * @param name
     */
    public AbstractParameterTest(String name) {
        super(name);
    }

    /**
     * Test gedrag groepen.
     * @throws Exception
     */
    public void testGroepen() throws Exception {

        ParameterBuilder builder = new ParameterBuilder();
        ParameterBrowser browser = new ParameterBrowser();

        StructuralGroup superGroup = builder.createStructuralGroup(
                "super", "een groep", true);
        StructuralGroup subgroep = builder.createStructuralGroup(
                "subgroep", "een subgroep", true);
        Version oudeVersieSubgroep = subgroep.getVersion();
        StructuralGroup subSubgroep = builder.createStructuralGroup(
                "subsubgroep", "een subsubgroep", false);
        ParameterGroup subParamGroep = builder.createParameterGroup(
                null, "subparamgroep", "Parameter groep onder subsubgroep", true);
        Parameter stringParam1 = builder.createString("teststring", "testwaarde");
        Version oudeVersieSubParamGroep = subParamGroep.getVersion();
        Calendar cal = Calendar.getInstance();
        cal.set(2004, 7, 31);
        Version nieuweVersieSubParamGroep = VersionRegistry.createVersion(
                cal.getTime(), "20040731", subParamGroep);
        assertFalse("de versies zouden moeten verschillen",
                (oudeVersieSubgroep.getName()
                        .equals(nieuweVersieSubParamGroep.getName())));

        Parameter checkParam1 = (Parameter)browser.navigate(
                "/super/subgroep:subparamgroep/teststring");
        assertTrue("de versies zouden gelijk moeten zijn",
                oudeVersieSubgroep.getName()
                		.equals(checkParam1.getVersion().getName()));
        nieuweVersieSubParamGroep.setGoedgekeurd(true);
        VersionRegistry.updateVersion(nieuweVersieSubParamGroep);
        Parameter checkParam2 = (Parameter)browser.navigate(
        		"/super/subgroep:subparamgroep/teststring");
    }

    /**
     * Test toevoegen, ophalen en verwijderen van een parameter.
     * @throws Exception
     */
    public void testSaveGetDelParameter() throws Exception {

        String localId = "test1";
        // creeer en sla op dmv de builder
        ParameterBuilder builder = new ParameterBuilder();
        Parameter paramOrig = builder.createNumeric(localId, null);
        String id = paramOrig.getId();
        log.info("parameter aangemaakt, id == " + id);
        // haal werkkopie op
        Parameter paramClone = ParameterRegistry.getParameter(id);
        // test dat het een kopie is
        assertNotSame(paramOrig, paramClone);

        // verwijder weer
        ParameterRegistry.removeParameter(paramOrig);
        try {
            // en test
            ParameterRegistry.getParameter(id);
            // een exception zou gegooid moeten zijn
            fail("kennelijk kan de verwijderde parameter toch nog gevonden worden");
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            // zou niet voor mogen komen
            fail(e.getMessage());
        } catch (NotFoundException e) {
            // dit is goed
        }
    }

    /**
     * Test parameter constructie.
     * @throws Exception
     */
    public void testBuildPercentage() throws Exception {

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
    public void testBuildString() throws Exception {

        ParameterBuilder builder = new ParameterBuilder();
        StringParameter param = null;
        param = builder.createString("foo", "My Name Is Bar");
        assertEquals("My Name Is Bar", param.getString());
    }

    /**
     * Test parameter constructie.
     * @throws Exception
     */
    public void testBuildBoolean() throws Exception {

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
    public void testBuildDate() throws Exception {

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
    public void testBuildFixedSet() throws Exception {

        ParameterBuilder builder = new ParameterBuilder();
        FixedSetParameter param = null;
        FixedSetInputConverter converter = new FixedSetInputConverter() {
            public Object convert(String valueAsString) throws ConversionException {
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

        try {
            value = param.createValue(null, "11"); // test ongeldige waarde (range check)
            fail("waarde had niet toegestaan mogen worden");
        } catch (InputException e) {
            assertTrue("verkeerde exception klasse", e instanceof ValueOutOfRangeException);
        }

        try {
            value = param.createValue(null, "foobar"); // test ongeldige waarde (conversie)
            fail("waarde had niet toegestaan mogen worden");
        } catch (InputException e) {
            assertTrue("verkeerde exception klasse", e instanceof ConversionException);
        }

    }

    /**
     * Test gebruik variabele in een expressie.
     * @throws Exception
     */
    public void testParameterInExpressie() throws Exception {

        // creeer en sla op dmv de builder
        ParameterBuilder builder = new ParameterBuilder();
        builder.createStructuralGroup("expressietest1", "test structuurgroep", true);
        ParameterGroup paramGroup = builder.createParameterGroup(
                "pgroup", "test parametergroep");
        builder.setParameterGroup(paramGroup);
        Parameter numberParam = builder.createNumeric("numberparam", "12,5"); // let op: NL locale!
        Parameter percParam = builder.createPercentage("percparam", "50");
        NestedParameter rowParam = builder.createNumericRow(
                "rowparam", new String[]{"1", "2"}, new String[]{"15", "25"});

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
        try {
            // en test
            ParameterRegistry.getParameter(numberParam.getId());
            // een exception zou gegooid moeten zijn
            fail("kennelijk kan de verwijderde parameter toch nog gevonden worden");
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
            // zou niet voor mogen komen
            fail(e.getMessage());
        } catch (NotFoundException e) {
            // dit is goed
        }
    }

    /**
     * Test navigatie met 'gpath'.
     * @throws Exception
     */
    public void testNavigatie() throws Exception {

        ParameterBrowser browser = new ParameterBrowser();
        StructuralRootGroup sroot = (StructuralRootGroup)browser.navigate("/");
        assertNotNull(sroot);
        ParameterRootGroup proot = (ParameterRootGroup)browser.navigate("/:DEFAULT");
        assertNotNull(proot);
        sroot = (StructuralRootGroup)browser.navigate("/");
        assertNotNull(sroot);
        proot = (ParameterRootGroup)browser.navigate("/:DEFAULT");
        assertNotNull(proot);

        ParameterBuilder builder = new ParameterBuilder();
        StructuralGroup testgroep = builder.createStructuralGroup(
                "testgroep", "Dit is een testgroep");
        log.info("nieuwe groep gemaakt met id: "+ testgroep.getId());
        // zet huidige groep op de nieuw gemaakte
        builder.setStructuralGroup(testgroep);
        StructuralGroup zoonGroep = builder.createStructuralGroup(
                "zoon", "kindgroep 1");
        log.info("nieuwe groep gemaakt met id: "+ zoonGroep.getId());
        StructuralGroup dochterGroep = builder.createStructuralGroup(
                "dochter", "kindgroep 2");
        log.info("nieuwe groep gemaakt met id: "+ dochterGroep.getId());
        // en zet huidige groep weer op de nieuw gemaakte
        builder.setStructuralGroup(dochterGroep);
        StructuralGroup kleinZoonGroep = builder.createStructuralGroup(
                "kleinzoon", "kind-kindgroep");
        log.info("nieuwe groep gemaakt met id: "+ kleinZoonGroep.getId());
        ParameterGroup paramGroup;
        builder.setStructuralGroup(kleinZoonGroep);
        paramGroup = builder.createParameterGroup(
                "paramgroep", "Een parametergroep");
        log.info("nieuwe parametergroep gemaakt met id: " + paramGroup.getId());

        // test browsen
        Group current;
        current = (Group)browser.navigate("/testgroep/dochter"); // absoluut
        assertEquals(dochterGroep, current);
//        current = (Group)browser.navigate("kleinzoon"); // relatief kind
//        assertEquals(kleinZoonGroep, current);
//        current = (Group)browser.navigate(".."); // relatief parent
//        assertEquals(dochterGroep, current);
//        current = (Group)browser.navigate(
//                "/testgroep/dochter/../dochter/./kleinzoon"); // absoluut
//        assertEquals(kleinZoonGroep, current);

        // test browsen met builder
        builder.navigate("/testgroep/dochter");
        StructuralGroup kleinDochterGroep = builder.createStructuralGroup(
                "kleindochter", "Nog een kleinkind");
        current = (Group)browser.navigate("/testgroep/dochter/kleindochter");
        assertEquals(kleinDochterGroep, current);

        // test toevoegen van en navigeren naar een parameter groep en parameters
        builder.setStructuralGroup(kleinDochterGroep);
        ParameterGroup parameterGroep = builder.createParameterGroup("eigenschappen",
                "eigenschappen van de kleindochter", true);
        //builder.setParameterGroup(parameterGroep);
        Parameter param = builder.createNumeric("leeftijd", "8");
        Object result = browser.navigate(
                "/testgroep/dochter/kleindochter:eigenschappen");
        assertEquals(parameterGroep, result);

        result = browser.navigate(
        	"/testgroep/dochter/kleindochter:eigenschappen/leeftijd");
        assertEquals(param, result);

        result = browser.navigate(
    		"/testgroep/dochter/kleindochter:eigenschappen/leeftijd@value");
        assertEquals(new Double(8), result);

        builder.navigate("/testgroep/dochter");
        NestedParameter rowParam = builder.createNumericRow("testrij",
                new String[]{"1", "2", "3"}, new String[]{"100", "112", "222"});
//        result = browser.navigate(
//			"/testgroep/dochter/kleindochter:eigenschappen/testrij['2']/@value");
//	    assertEquals(new Double(112), result);

    }

    /**
     * Test het overerf gedrag van parametergroepen.
     * top &lt;- super &lt;- deze
     */
    public void testParameterGroepOvererving() throws Exception {

        NumericParameter param = null;
        ParameterBuilder builder = new ParameterBuilder();
        StructuralGroup superSuperGroup = builder.createStructuralGroup(
                "top", "Dit is de top groep", true);
        ParameterGroup superSuperParamGroup = builder.createParameterGroup(
                "parametergroep", "Dit is de super.super parameter groep", true);
        param = builder.createNumeric("een", "1"); // wordt niet overriden
        param = builder.createNumeric("twee", "2"); // override door super
        param = builder.createNumeric("drie", "3"); // override door deze
        StructuralGroup superGroup = builder.createStructuralGroup(
                "super", "Dit is de super groep", true);
        ParameterGroup superParamGroup = builder.createParameterGroup(
                superSuperParamGroup, "parametergroep",
                "Dit is de super.super parameter groep", true);
        param = builder.createNumeric("twee", "22");
        param = builder.createNumeric("drie", "33");
        StructuralGroup dezeGroup = builder.createStructuralGroup(
                "deze", "Dit is de overervende groep", true);
        ParameterGroup dezeParamGroup = builder.createParameterGroup(
                superParamGroup, "parametergroep",
                "Dit is de super.super parameter groep", true);
        param = builder.createNumeric("drie", "333");

        Object current = null;
        ParameterBrowser browser = new ParameterBrowser();
        current = browser.evaluate("/top/super/deze:parametergroep[id='drie']/@value");
        assertEquals(new Double(333), current);
        current = browser.evaluate("/top/super/deze:parametergroep[id='twee']/@value");
        assertEquals(new Double(22), current);
        current = browser.evaluate("/top/super/deze:parametergroep[id='een']/@value");
        assertEquals(new Double(1), current);
    }

    /**
     * Creeer test inputs.
     * @return test inputs
     */
    private ParameterInput[] createTestInputs() {
        ParameterInput[] inputs = new ParameterInput[3];
        inputs[0] = new ParameterInput() {
            private final Integer VALUE = new Integer(10);
            public Object getValue() {
                return VALUE;
            }
        };
        inputs[1] = new ParameterInput() {
            private final Integer VALUE = new Integer(20);
            public Object getValue() {
                return VALUE;
            }
        };
        inputs[2] = new ParameterInput() {
            private final Integer VALUE = new Integer(30);
            public Object getValue() {
                return VALUE;
            }
        };
        return inputs;
    }

}
