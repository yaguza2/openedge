/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.support.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import nl.openedge.gaps.core.parameters.test.Util;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.core.versions.VersionRegistry;
import nl.openedge.gaps.support.berekeningen.BerekeningInterpreter;
import nl.openedge.gaps.support.berekeningen.functies.Function;
import nl.openedge.gaps.support.berekeningen.functies.FunctionArgument;
import nl.openedge.gaps.support.berekeningen.functies.FunctionDescriptor;
import nl.openedge.gaps.support.berekeningen.functies.FunctionRegistry;
import nl.openedge.gaps.util.CustomHSQLDialect;
import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tests voor de interpreter.
 */
public class InterpreterTest extends TestCase
{

	/** Log. */
	private static Log log = LogFactory.getLog(InterpreterTest.class);

	/**
	 * Construct.
	 */
	public InterpreterTest()
	{
	}

	/**
	 * Construct.
	 * @param name
	 */
	public InterpreterTest(String name)
	{
		super(name);
	}

	/**
	 * Test een expressie zonder variabelen.
	 * @throws Exception
	 */
	public void testSimpel() throws Exception
	{
		String expr = "9 + 12.25 * 3.2 * (3 - 1) + (35 / 7) * 1.5";
		Double result = BerekeningInterpreter.evaluate(expr, null);
		assertEquals(new Double(94.9), result);
	}

	/**
	 * Test uni op.
	 * @throws Exception
	 */
	public void testCast() throws Exception
	{
		String expr;
		Double result;
		expr = "- (9 + 2)";
		result = BerekeningInterpreter.evaluate(expr, null);
		assertEquals(new Double(-11), result);
		expr = "+ (2 - 9)";
		result = BerekeningInterpreter.evaluate(expr, null);
		assertEquals(new Double(7), result);
		expr = "2 + (- (7 * 2 - 4))";
		result = BerekeningInterpreter.evaluate(expr, null);
		assertEquals(new Double(-8), result);
	}

	/**
	 * Test een eenvoudige expressie met invoerparameter.
	 * @throws Exception
	 */
	public void testMetInvoerVariabele() throws Exception
	{
		String expr = "20 + :/ctxparam * 2";
		Map context = new HashMap();
		context.put("/ctxparam", new Double(60.64));
		Double result = BerekeningInterpreter.evaluate(expr, context);
		assertEquals(new Double(141.28), result);
	}

	/**
	 * Test een eenvoudige expressie met functie calls (inclusief geneste call).
	 * @throws Exception
	 */
	public void testFunctie() throws Exception
	{
		//TODO versiebeheer voor functies werkt niet meer; implementeer.
		Function function1 = new AddTwoFunction();
		function1.setId("addTwo");
		FunctionRegistry.saveFunction(function1);
		Function function2 = new AddArgsFunction();
		function2.setId("addAll");
		FunctionRegistry.saveFunction(function2);
		String expr = "addAll(10, :/ctxparam, addTwo(30))";
		Map context = new HashMap();
		context.put("/ctxparam", new Double(20));
		Double result = BerekeningInterpreter.evaluate(expr, context);
		assertEquals(new Double(62), result);
	}

	/**
	 * Suite methode.
	 * @return Test suite
	 */
	public static Test suite() throws Exception
	{
		TestSuite suite = new TestSuite();
		suite.addTest(new InterpreterTest("testSimpel"));
		suite.addTest(new InterpreterTest("testCast"));
		suite.addTest(new InterpreterTest("testMetInvoerVariabele"));
		//	    suite.addTest(new InterpreterTest("testFunctie"));
		TestSetupDeco testDeco = new TestSetupDeco(suite);
		return testDeco;
	}

	/**
	 * Setup decorator voor het opzetten van de registries met dummy implementaties.
	 */
	private static class TestSetupDeco extends TestSetup
	{

		/** log. */
		private static Log log = LogFactory.getLog(TestSetupDeco.class);

		/**
		 * @param test
		 */
		public TestSetupDeco(Test test)
		{
			super(test);
		}

		/**
		 * @see junit.extensions.TestSetup#setUp()
		 */
		protected void setUp() throws Exception
		{

			try
			{
				String configFile = "/hibernate.cfg.xml";
				Util.initAndCreateDB(configFile, new CustomHSQLDialect());

				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
				Date vdate = Util.getTestDatum();
				Version version = VersionRegistry.createVersion(vdate, sdf.format(vdate),
						null);
				version.setGoedgekeurd(true);
				VersionRegistry.updateVersion(version);

			}
			catch (Throwable e)
			{
				log.error(e.getMessage(), e);
				throw new Exception(e);
			}
		}

		/**
		 * @see junit.extensions.TestSetup#tearDown()
		 */
		protected void tearDown() throws Exception
		{
			HibernateHelper.closeSession();
		}
	}

	/**
	 * Test functie; telt 2 op bij de input.
	 */
	private static class AddTwoFunction extends Function
	{

		/* voer uit. */
		public Object perform(Map context, Object[] arguments)
		{
			double arg1 = ((Double) arguments[0]).doubleValue();
			double result = arg1 + 2;
			return new Double(result);
		}

		/* geeft descriptor. */
		public FunctionDescriptor getDescriptor()
		{
			return new FunctionDescriptor()
			{

				public FunctionArgument[] getArgumentTypes()
				{
					return new FunctionArgument[] {new FunctionArgument("arg1",
							Double.class)};
				}
			};
		}
	}

	/**
	 * Test functie; telt alle argumenten bij elkaar op.
	 */
	private static class AddArgsFunction extends Function
	{

		/* voer uit. */
		public Object perform(Map context, Object[] arguments)
		{
			double result = 0;
			for (int i = 0; i < arguments.length; i++)
			{
				result = result + ((Double) arguments[i]).doubleValue();
			}
			return new Double(result);
		}

		/* geeft descriptor. */
		public FunctionDescriptor getDescriptor()
		{
			return new FunctionDescriptor()
			{

				public FunctionArgument[] getArgumentTypes()
				{
					return new FunctionArgument[] {new FunctionArgument("arglist",
							List.class)};
				}
			};
		}
	}
}