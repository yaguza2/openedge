/*
 * Project: levob-allureplan-modellen Door: Levob Java Ontwikkelteam <copy>
 * ================================================================================
 * Copyright (c) 2004, Levob Bank en Verzekeringen Alle rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.stream.test;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.groups.Group;
import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.parameters.InputException;
import nl.openedge.gaps.core.parameters.SaveException;
import nl.openedge.gaps.core.parameters.impl.NestedParameter;
import nl.openedge.gaps.core.parameters.test.Util;
import nl.openedge.gaps.core.versions.VersionRegistry;
import nl.openedge.gaps.support.ParameterBrowser;
import nl.openedge.gaps.support.ParameterBuilder;
import nl.openedge.gaps.support.ParameterBuilderException;
import nl.openedge.gaps.support.gapspath.GPathInterpreterException;

/**
 * @author pv272000 TODO doc
 */
public class ParameterStreamTest extends TestCase
{

	private ParameterBuilder pb;

	private StructuralGroup sg;

	private ParameterGroup pg;

	protected void setUp() throws Exception
	{
		super.setUp();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		Date vdate = Util.getTestDatum();
		VersionRegistry.createVersion(vdate, sdf.format(vdate), null);
	}

	public void testSterfteTafels() throws RegistryException, SaveException,
			InputException, ParameterBuilderException, GPathInterpreterException
	{
		Object result = null;
		pb = new ParameterBuilder();
		sg = pb.createStructuralGroup("SterfteTafels",
				"StarefteTafels voor berekening van koopsommen/lijfrentes");
		pb.navigate("/SterfteTafels");
		pg = pb.createParameterGroup("Tafels", "Tafels met levenden");
		pg.setParent(sg);
		pg.setParentId(sg.getParentId());
		pg.setParentLocalId(sg.getParentLocalId());
		pg.setVersion(sg.getVersion());
		pb.navigate("/SterfteTafels:Tafels");
		InputStream is = ParameterStreamTest.class.getResourceAsStream("LGBM61.txt");
		pb.createNumericData(is, 1, 0, true, ParameterBuilder.TAB_EN_SPACE_CHARS);
		;
		pb.navigate("/SterfteTafels:Tafels");
		is = ParameterStreamTest.class.getResourceAsStream("LGBM71.txt");
		pb.createNumericData(is, 1, 0, true, ParameterBuilder.TAB_EN_SPACE_CHARS);
		;
		pb.navigate("/SterfteTafels:Tafels");
		is = ParameterStreamTest.class.getResourceAsStream("LGBM76.txt");
		pb.createNumericData(is, 1, 0, true, ParameterBuilder.TAB_EN_SPACE_CHARS);
		;
		pb.navigate("/SterfteTafels:Tafels");
		is = ParameterStreamTest.class.getResourceAsStream("LGBM80.txt");
		pb.createNumericData(is, 1, 0, true, ParameterBuilder.TAB_EN_SPACE_CHARS);
		;
		pb.navigate("/SterfteTafels:Tafels");
		is = ParameterStreamTest.class.getResourceAsStream("LGBM85.txt");
		pb.createNumericData(is, 1, 0, true, ParameterBuilder.TAB_EN_SPACE_CHARS);
		;
		pb.navigate("/SterfteTafels:Tafels");
		is = ParameterStreamTest.class.getResourceAsStream("LGBM90.txt");
		pb.createNumericData(is, 1, 0, true, ParameterBuilder.TAB_EN_SPACE_CHARS);
		;
		pb.navigate("/SterfteTafels:Tafels");
		is = ParameterStreamTest.class.getResourceAsStream("LGBV71.txt");
		pb.createNumericData(is, 1, 0, true, ParameterBuilder.TAB_EN_SPACE_CHARS);
		;
		pb.navigate("/SterfteTafels:Tafels");
		is = ParameterStreamTest.class.getResourceAsStream("LGBV76.txt");
		pb.createNumericData(is, 1, 0, true, ParameterBuilder.TAB_EN_SPACE_CHARS);
		;
		pb.navigate("/SterfteTafels:Tafels");
		is = ParameterStreamTest.class.getResourceAsStream("LGBV80.txt");
		pb.createNumericData(is, 1, 0, true, ParameterBuilder.TAB_EN_SPACE_CHARS);
		;
		pb.navigate("/SterfteTafels:Tafels");
		is = ParameterStreamTest.class.getResourceAsStream("LGBV85.txt");
		pb.createNumericData(is, 1, 0, true, ParameterBuilder.TAB_EN_SPACE_CHARS);
		;
		pb.navigate("/SterfteTafels:Tafels");
		is = ParameterStreamTest.class.getResourceAsStream("LGBV90.txt");
		pb.createNumericData(is, 1, 0, true, ParameterBuilder.TAB_EN_SPACE_CHARS);
		;
		Group current = null;
		NestedParameter np = null;
		ParameterBrowser browser = new ParameterBrowser();
		current = (Group) browser.navigate("/SterfteTafels");
		assertSame(sg, current);
		current = (Group) browser.navigate("/SterfteTafels:Tafels");
		assertSame(pg, current);
		np = (NestedParameter) browser.navigate("/SterfteTafels:Tafels[id='L_GBM61']");
		assertNotNull(np);
		result = browser.navigate("/SterfteTafels:Tafels[id='L_GBM61'][id='0']/@value");
		assertEquals(new Double(10000000), result);
		result = browser.navigate("/SterfteTafels:Tafels[id='L_GBM61'][id='25']/@value");
		assertEquals(new Double(9878089), result);
		result = browser.navigate("/SterfteTafels:Tafels[id='L_GBM61'][id='50']/@value");
		assertEquals(new Double(9305011), result);
		result = browser.navigate("/SterfteTafels:Tafels[id='L_GBM61'][id='75']/@value");
		assertEquals(new Double(5145965), result);
		result = browser.navigate("/SterfteTafels:Tafels[id='L_GBM61'][id='100']/@value");
		assertEquals(new Double(7260), result);
		result = browser.navigate("/SterfteTafels:Tafels[id='L_GBM71'][id='0']/@value");
		assertEquals(new Double(10000000), result);
	}
}