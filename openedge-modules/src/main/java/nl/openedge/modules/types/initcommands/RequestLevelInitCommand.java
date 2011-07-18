package nl.openedge.modules.types.initcommands;

/**
 * Tagging interface for InitCommands that should be executed on each request for a
 * component instead of just once for each instance as is the contract for the normal
 * InitCommands. With the default implementation, RequestLevelInitCommands will be called
 * once on initialization as well BEFORE the normal initialization commands are called.
 * Because of this, a component of types DependentType and ConfigurableType has its
 * dependencies resolved before the {@link ConfigurableType#init(org.jdom.Element)} method
 * of ConfigurableType is called, so the component can use its dependencies for further
 * initialization
 * 
 * @author Eelco Hillenius
 */
public interface RequestLevelInitCommand extends InitCommand
{

}
