package nl.openedge.util.baritus.validators;

import java.util.Date;

/**
 * (Form)Bean for testing.
 * 
 * @author Eelco Hillenius
 */
public class MockBean
{
	private Date testDate;

	public Date getTestDate()
	{
		return testDate;
	}

	public void setTestDate(final Date deTestDatum)
	{
		this.testDate = deTestDatum;
	}
}
