/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) 2004, Levob Bank en Verzekeringen Alle rechten voorbehouden.
 */
package nl.openedge.gaps.ui.web;

import java.util.Date;
import java.util.Locale;

import nl.openedge.baritus.ExecutionParams;
import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.FormBeanCtrl;

import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.ControllerContext;
import org.jdom.Element;

/**
 * Basis controller voor het project. Alle controls in dit project dienen van
 * deze base class te overerven, zodat een aantal standaard elementen kunnen
 * worden ondersteund.
 */
public abstract class AbstractBaseCtrl extends FormBeanCtrl {

    /**
     * De Nederlandse locale.
     */
    private static final Locale NL_LOCALE = new Locale("nl", "NL");

    /**
     * Initialiseert de base control. Deze methode is final gemaakt om af te
     * dwingen dat deze methode ALTIJD wordt aangeroepen (anders zijn de
     * overervende controls verantwoordelijk voor aanroepen
     * super.init(controllerNode). Controls die zich willen laten initialiseren
     * kunnen init2(controllerNode gebruiken, die na het uitvoeren van deze init
     * wordt opgeroepen.
     * <p />
     * LET OP: probeer vooral niet super.initControl() aan te roepen vanuit
     * init2; je krijgt dan een <b>stackoverflow </b> error.
     * @param controllerNode de XML node van Maverick configuratie
     * @throws ConfigException als er tijdens het configureren iets fout gaat.
     * @see ControllerSingleton@init
     */
    public final void init(final Element controllerNode) throws ConfigException {

        // registreer interceptor voor menu afhandeling
        MenuInterceptor leftMenuInterceptor = new MenuInterceptor();
        leftMenuInterceptor.setAttribKeyMenu("leftMenu");
        addInterceptor(leftMenuInterceptor);

        ExecutionParams params = getExecutionParams(null);
        // we willen alleen form validatie als al het voorgaande gelukt is
        params.setDoFormValidationIfFieldValidationFailed(false);
        // zet deze parameters vast voor al het gebruik van de controls
        fixExecutionParams(params);
        // geef overervende classes de mogelijkheid te initialiseren
        initControl(controllerNode);
    }

    /**
     * Deze init is beschikbaar voor classes die overerven van AbstractBaseCtrl
     * Aangezien dit een no-op is, hoeven overervende classes niet de super aan
     * te roepen. LET OP: probeer vooral niet super.init() aan te roepen; je
     * krijgt dan een <b>stackoverflow </b> error.
     * @param controllerNode de XML node van Maverick configuratie
     * @throws ConfigException als er tijdens het configureren iets fout gaat.
     */
    public void initControl(final Element controllerNode) throws ConfigException {
        // no-op
    }

    /**
     * Pint de NL locale vast voor IEDER request.
     * @param cctx de maverick context
     * @param formBeanContext de form bean context
     * @return de locale, altijd nl_NL.
     * @see nl.openedge.maverick.framework.FormBeanCtrl#getLocaleForRequest(org.infohazard.maverick.flow.ControllerContext,
     *      nl.openedge.maverick.framework.FormBean)
     */
    protected Locale getLocaleForRequest(final ControllerContext cctx,
            final FormBeanContext formBeanContext) {
        return NL_LOCALE;
    }

    /**
     * Bepaal de message bundle key voor een conversie fout voor het gegeven
     * type en veld met de gegeven naam. Override van de FormBeanCtrl functie
     * getConversionErrorLabelKey
     * @param type type of the target property that threw the conversion error
     * @param name name of the target property
     * @param triedValue the value that could not be converted to the type of
     *            the target property
     * @return String message bundle key
     */
    protected String getConversionErrorLabelKey(final Class type, final String name,
            final Object triedValue) {
        String key = null;

        if (java.lang.Number.class.isAssignableFrom(type)) {
            key = "invalid.field.input.number";
        } else if (Date.class.isAssignableFrom(type)) {
            key = "invalid.field.input.date";
        } else if (Boolean.TYPE.isAssignableFrom(type) || (Boolean.class.isAssignableFrom(type))) {
            key = "invalid.field.input.boolean";
        } else {
            key = "invalid.field.input";
        }

        return key;
    }

}