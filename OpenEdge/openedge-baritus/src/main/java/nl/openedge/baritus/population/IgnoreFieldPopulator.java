package nl.openedge.baritus.population;

import nl.openedge.baritus.FormBeanContext;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Ignores the population of fields. Register IgnoreFieldPopulators if you want to ignore
 * the population of certain properties, for instance id's of persistent objects.
 * 
 * @author Eelco Hillenius
 */
public final class IgnoreFieldPopulator implements FieldPopulator
{
	private boolean fail;

	public IgnoreFieldPopulator()
	{
		setFail(false);
	}

	/**
	 * construct with parameter fail
	 * 
	 * @param fail
	 *            If fail == true, setProperty will always return false, and thus the
	 *            population process is flagged as failed, if fail == false, setProperty
	 *            will always return true, and thus has no effect on the total population.
	 */
	public IgnoreFieldPopulator(boolean fail)
	{
		setFail(fail);
	}

	/**
	 * Does nothing at all. Register IgnoreFieldPopulators if you want to ignore the
	 * population of certain properties, for instance id's of persistent objects.
	 */
	@Override
	public boolean setProperty(ControllerContext cctx, FormBeanContext form, String name,
			Object value)
	{
		return (!fail);
	}

	/**
	 * get the value of property fail
	 * 
	 * @return boolean value of property fail. By default fail == false, which means that
	 *         this method always returns true. If you set fail to true, this method will
	 *         always return false, and thus the population process is flagged as failed.
	 */
	public boolean isFail()
	{
		return fail;
	}

	/**
	 * set the value of property fail
	 * 
	 * @param b
	 *            value of property fail. By default fail == false, which means that this
	 *            method always returns true. If you set fail to true, this method will
	 *            always return false, and thus the population process is flagged as
	 *            failed.
	 */
	public void setFail(boolean b)
	{
		fail = b;
	}
}
