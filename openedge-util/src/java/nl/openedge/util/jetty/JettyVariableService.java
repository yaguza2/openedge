package nl.openedge.util.jetty;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.mortbay.jetty.plus.AbstractService;
import org.mortbay.jndi.Util;

/**
 * Registers an variable to the Jetty JNDI context.
 * @author Eelco Hillenius
 */
public final class JettyVariableService extends AbstractService {

    /**
     * Add a variable to the JNDI context.
     * @param name name of the variable
     * @param value value to bind
     */
    public void addVariable(String name, Object value) {
        
        // simply bind in the JNDI context
        try {
            InitialContext ctx = new InitialContext();
            Util.bind(ctx, name, value);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
