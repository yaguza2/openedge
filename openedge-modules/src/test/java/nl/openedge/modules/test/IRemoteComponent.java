/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Promedico ICT B.V.
 * All rights reserved.
 */
package nl.openedge.modules.test;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Eelco Hillenius
 */
public interface IRemoteComponent extends Remote
{
	public String ping() throws RemoteException;
}