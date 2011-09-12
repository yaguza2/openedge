package nl.openedge.baritus;

import java.util.Map;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * ValidatorDelegates can do validation on input and populated form beans. Besides the
 * allways used DefaultValidatorDelegate, users of Baritus can register additional
 * delegates, for instance to be able to plug in validator mechanisms like FormProc or
 * Commons Validator.
 * 
 * @author Eelco Hillenius
 */
interface ValidatorDelegate
{
	/**
	 * handle the validation for all provided parameters. The parameters consist typically
	 * of the request parameters, possibly (depending on the also provided instance of
	 * ExecutionParams) with the addition of Maverick configuration parameters, session
	 * attributes and request attributes.
	 * 
	 * Implementers should take care to only use the fields stored in parameters and not
	 * to get the field directly from e.g. the http request.
	 * 
	 * The populated form is stored in the formBeanContext. If implementors have
	 * validation errors, they should store the error messages in the instance of
	 * FormBeanContext (using method(s) setError/ setErrors), and they should save the
	 * original input values (as stored in Map parameters) as override values in the
	 * formBeanContext (using method(s) setOverrideField)
	 * 
	 * @param cctx
	 *            controller context
	 * @param formBeanContext
	 *            form bean context with populated bean
	 * @param execParams
	 *            execution parameters
	 * @param parameters
	 *            the map in which the requested values are stored. This map has at least
	 *            the request parameters stored and depending on the execution parameters
	 *            the maverick configuration parameters, session attributes and request
	 *            attributes.
	 * @param succeeded
	 *            whether population/ validation succeeded so far (did not generate any
	 *            errors).
	 * @return whether validation passed
	 * 
	 *         NOTE: implementors should take note that it is possible that population/
	 *         validation allready failed before this method is called (in that case
	 *         succeeded is false). If you do not want to override the errors (what is
	 *         probably is good idea), you can check the current error map or overwrite
	 *         value map in the formBeanContext. This allways works for properties that
	 *         failed to populate, though it might not work for failed validations as that
	 *         depends on the registrations that the individual validators make in the
	 *         error map.
	 */
	public boolean doValidation(ControllerContext cctx, FormBeanContext formBeanContext,
			ExecutionParams execParams, Map<String, Object> parameters, boolean succeeded);
}
