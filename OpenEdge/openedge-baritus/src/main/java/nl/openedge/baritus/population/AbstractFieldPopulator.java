package nl.openedge.baritus.population;

import nl.openedge.baritus.FormBeanCtrlBase;

/**
 * Convenience class for populators.
 * 
 * @author Eelco Hillenius
 * @author Sander Hofstee
 */
public abstract class AbstractFieldPopulator implements FieldPopulator
{

	/** reference to the controller */
	protected FormBeanCtrlBase ctrl = null;

	/**
	 * default constructor
	 */
	public AbstractFieldPopulator()
	{
		// noop
	}

	/**
	 * constructor with control
	 */
	public AbstractFieldPopulator(FormBeanCtrlBase ctrl)
	{
		setCtrl(ctrl);
	}

	/**
	 * get reference to the controller
	 * 
	 * @return reference to the controller
	 */
	public FormBeanCtrlBase getCtrl()
	{
		return ctrl;
	}

	/**
	 * set reference to the controller
	 * 
	 * @param ctrl
	 *            reference to the controller
	 */
	public void setCtrl(FormBeanCtrlBase ctrl)
	{
		this.ctrl = ctrl;
	}

}
