/*
 * Project: Allureplan-rekenmodule
 * Door: Levob Java Ontwikkelteam
 *
 * $Id$
 * $Revision$
 * $Date$
 * 
 * ================================================================================
 * Copyright (c) 2004, Levob Bank en Verzekeringen
 * Alle rechten voorbehouden.
 */
package nl.levob.parameters.hibernate;

import junit.framework.TestCase;
import nl.openedge.util.hibernate.ConfigException;
import nl.openedge.util.hibernate.HibernateHelper;

/**
 * @author mn190350
 */
public class TestHibernate extends TestCase{

   static {
        try {
            HibernateHelper.init();
        } catch (ConfigException e) {
            e.printStackTrace();
        }
    }

   /**
    * Sluit na iedere test even de huidige verbinding.
    * @see junit.framework.TestCase#tearDown()
    */
   public void tearDown() throws Exception
   {
       HibernateHelper.closeSession();
   }
}
