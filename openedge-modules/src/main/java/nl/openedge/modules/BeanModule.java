/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules;

/**
 * Module that implement this interface will be populated (using BeanUtils).
 * E.g: take module mypackage.Foo with property Integer bar, the following
 * configuration fragment will have setBar(Integer val) called with value 12.
 * <p><pre>
 *		&lt;module name="FooModule" 
 *				class="mypackage.Foo"&gt;
 *			&lt;property name="bar" value="12"/&gt;		
 *		&lt;/module&gt;
 * </pre>
 * 
 * @author Eelco Hillenius
 */
public interface BeanModule {

}
