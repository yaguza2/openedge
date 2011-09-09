package nl.openedge.modules.types.initcommands;

/**
 * A component that implement this interface will be populated (using BeanUtils). E.g:
 * take module mypackage.Foo with property Integer bar, the following configuration
 * fragment will have setBar(Integer val) called with value 12.
 * <p>
 * 
 * <pre>
 * {@literal
 * <component name="FooModule" class="mypackage.Foo">
 *    <property name="bar" value="12" />		
 * </component>
 * }
 * </pre>
 * 
 * @author Eelco Hillenius
 */
public interface BeanType
{
}
