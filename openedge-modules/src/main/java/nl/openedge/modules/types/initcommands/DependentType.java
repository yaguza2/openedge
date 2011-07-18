package nl.openedge.modules.types.initcommands;

/**
 * A dependent type uses other components. An alternative is to use the module factory
 * directly to get the dependencies but then you loose:
 * <ul>
 * <li>startup checks on types and cyclic loops
 * <li>using aliases or logical names
 * </ul>
 * 
 * @author Eelco Hillenius
 */
public interface DependentType
{
}
