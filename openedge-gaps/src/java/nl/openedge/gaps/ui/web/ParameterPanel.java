/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.gaps.ui.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.impl.BooleanParameter;
import nl.openedge.gaps.core.parameters.impl.DateParameter;
import nl.openedge.gaps.core.parameters.impl.NestedParameter;
import nl.openedge.gaps.core.parameters.impl.NumericParameter;
import nl.openedge.gaps.core.parameters.impl.PercentageParameter;
import nl.openedge.gaps.core.parameters.impl.StringParameter;
import nl.openedge.gaps.support.ParameterBuilder;

import com.voicetribe.wicket.RequestCycle;
import com.voicetribe.wicket.markup.html.HtmlContainer;
import com.voicetribe.wicket.markup.html.basic.Label;
import com.voicetribe.wicket.markup.html.form.CheckBox;
import com.voicetribe.wicket.markup.html.form.DropDownChoice;
import com.voicetribe.wicket.markup.html.form.Form;
import com.voicetribe.wicket.markup.html.form.IOnChangeListener;
import com.voicetribe.wicket.markup.html.form.TextField;
import com.voicetribe.wicket.markup.html.form.validation.IValidationErrorHandler;
import com.voicetribe.wicket.markup.html.panel.Panel;
import com.voicetribe.wicket.markup.html.table.Cell;
import com.voicetribe.wicket.markup.html.table.Table;

/**
 * Panel voor parameters.
 */
public class ParameterPanel extends Panel
{
    /** boolean type. */
    protected static final String TYPE_BOOLEAN = "logisch";

    /** numeriek type. */
    protected static final String TYPE_NUMERIC = "numeriek";

    /** tekst type. */
    protected static final String TYPE_TEXT = "tekst";

    /** datum type. */
    protected static final String TYPE_DATE = "datum";

    /** percentage type. */
    protected static final String TYPE_PERCENTAGE = "percentage";

    /** voor ui type/ parameter type (class). */
    private static final Map TYPEMAP = new HashMap();
    static
    {
        TYPEMAP.put(TYPE_BOOLEAN, BooleanParameter.class);
        TYPEMAP.put(TYPE_NUMERIC, NumericParameter.class);
        TYPEMAP.put(TYPE_TEXT, StringParameter.class);
        TYPEMAP.put(TYPE_DATE, DateParameter.class);
        TYPEMAP.put(TYPE_PERCENTAGE, PercentageParameter.class);
    }
    
    /** de huidige parametergroep. */
    private ParameterGroup group;

    /**
     * Construct.
     * @param componentName
     * @param group de parametergroep
     */
    public ParameterPanel(String componentName, ParameterGroup group)
    {
        super(componentName);
        this.group = group;
        addParamComponents(group); // voeg de componenten toe
    }

    /**
     * Voeg componenten voor parameters toe.
     * @param group de huidige parametergroep
     */
    private void addParamComponents(ParameterGroup group)
    {
        HtmlContainer outerPanel = new HtmlContainer("outerPanel"); // maak top container
        final Parameter[] parameters;
        if(group != null)
        {
            parameters = group.getParameters();
            setVisible(true);
        }
        else
        {
            parameters = new Parameter[0]; // lege array
            setVisible(false);
        }
        // splis en sorteer de parameters naar soort
        GroupedParameters grouped = new GroupedParameters(parameters);
        addPlain(outerPanel, grouped); // componenten voor gewone parameters
        addNested(outerPanel, grouped); // componenten voor geneste parameters
        add(outerPanel); // voeg top container aan dit panel toe
    }

    //-------------------------------------------------------------------------------
    //--------------------- afhandeling gewone parameters ---------------------------
    //-------------------------------------------------------------------------------

    /**
     * Voeg componenten voor gewone parameters toe.
     * @param outerPanel top level panel
     * @param grouped groupering
     */
    private void addPlain(HtmlContainer outerPanel, GroupedParameters grouped)
    {
        // voeg niet-geneste parameters toe
        HtmlContainer plainParamsPanel = new HtmlContainer("plainParamsPanel");
        PlainParameterForm paramForm = new PlainParameterForm("paramForm", null);
        PlainParameterTable plainParametersTable =
            new PlainParameterTable("plainParams", grouped.plainParams);
        paramForm.add(plainParametersTable);
        plainParamsPanel.add(paramForm);
        NewParameterForm newParamForm = new NewParameterForm("newParamForm", null);
        outerPanel.add(newParamForm);
        if(grouped.plainParams.isEmpty())
        {
            plainParamsPanel.setVisible(false);
        }
        outerPanel.add(plainParamsPanel);
    }

    /**
     * Form voor editen gewone parameters.
     */
    private static class PlainParameterForm extends Form
    {
        /**
         * Construct.
         * @param name component name
         * @param validationErrorHandler validation error handler
         */
        public PlainParameterForm(String name,
                IValidationErrorHandler validationErrorHandler)
        {
            super(name, validationErrorHandler);
        }

        /**
         * @see com.voicetribe.wicket.markup.html.form.Form#handleSubmit(com.voicetribe.wicket.RequestCycle)
         */
        public void handleSubmit(RequestCycle cycle)
        {
            // op dit moment handelen de specifieke models achter de input
            // velden alles af.
        }
    }

    /**
     * Form voor toevoegen parameters.
     */
    private class NewParameterForm extends Form
    {
        /** checkbox input voor value. */
        private CheckBox checkbox;

        /** text input voor value. */
        private TextField input;

        /** text input voor naam. */
        private TextField nameInput;

        /** choice component. */
        private TypeChoice typeChoice;

        /**
         * Construct.
         * @param name component name
         * @param validationErrorHandler validation error handler
         */
        public NewParameterForm(String name,
                IValidationErrorHandler validationErrorHandler)
        {
            super(name, validationErrorHandler);
            nameInput = new TextField("name", "");
            add(nameInput);
            List typeChoiceModel = new ArrayList();
            typeChoiceModel.addAll(TYPEMAP.keySet());
            typeChoice = new TypeChoice("typeChoice", "", typeChoiceModel);
            add(typeChoice);
            // voeg verschillende inputs toe. Uiteindelijk zal er 1 visible zijn
            checkbox = new CheckBox("checkbox", new Boolean(false));
            add(checkbox);
            input = new TextField("input", "");
            add(input);
            setInputForType(TYPE_TEXT);
        }

        /**
         * Zet juiste input aan (visible) op basis van het gegeven type.
         * @param type type uit selectie dropdown
         */
        private void setInputForType(String type)
        {
            if(TYPE_BOOLEAN.equals(type))
            {
                checkbox.setVisible(true);
                input.setVisible(false);
            }
            else
            {
                checkbox.setVisible(false);
                input.setVisible(true);
            }
        }

        /**
         * @see com.voicetribe.wicket.markup.html.form.Form#handleSubmit(com.voicetribe.wicket.RequestCycle)
         */
        public void handleSubmit(RequestCycle cycle)
        {
            ParameterBuilder builder = new ParameterBuilder();
            builder.setParameterGroup(group);
            String type = (String)typeChoice.getModelObject();
            String name = (String)nameInput.getModelObject();
            
            try
            {
                String value = null;
                Class parameterClass = (Class)TYPEMAP.get(type);
	            if(TYPE_BOOLEAN.equals(type))
	            {
	                value = checkbox.getModelObject().toString();
	            }
	            else
	            {
	                value = (String)input.getModelObject();
	            }
	            builder.createParameter(parameterClass, name, value);
	            ParameterPanel.this.invalidateModel();
	            ParameterPanel.this.removeAll();
	            addParamComponents(group);
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        /**
         * Klasse voor selectiebox met een onChanged event. Afhankelijk van het
         * geselecteerde type wordt het juiste invoerveld op visible gezet (en
         * de niet-relevante invoermogelijkheden op false).
         */
        private class TypeChoice extends DropDownChoice implements IOnChangeListener
        {
            /**
             * Construct.
             * @param componentName
             * @param model
             * @param values
             */
            public TypeChoice(String componentName, Serializable model, Collection values)
            {
                super(componentName, model, values);
            }

            /**
             * @see com.voicetribe.wicket.markup.html.form.IOnChangeListener#selectionChanged(com.voicetribe.wicket.RequestCycle, java.lang.Object)
             */
            public void selectionChanged(RequestCycle cycle, Object newSelection)
            {
                setInputForType((String)newSelection);
            }
        }
    }

    /**
     * Table voor niet-geneste parameters.
     */
    private static class PlainParameterTable extends Table
    {
        /**
         * Construct.
         * @param componentName componentnaam
         * @param model list met parametergroepen
         */
        public PlainParameterTable(String componentName, List model)
        {
            super(componentName, model);
        }

        /**
         * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
         */
        protected void populateCell(Cell cell)
        {
            Parameter parameter = (Parameter)cell.getModelObject();
    		cell.add(new Label("name", parameter.getLocalId()));
    		cell.add(new TextField("value",
    		        new ParameterModel(parameter, "value.value", true)));
        }
    }

    //-------------------------------------------------------------------------------
    //--------------------- afhandeling geneste parameters --------------------------
    //-------------------------------------------------------------------------------

    /**
     * Voeg componenten voor geneste parameters toe.
     * @param outerPanel top level panel
     * @param grouped groupering
     */
    private void addNested(HtmlContainer outerPanel, GroupedParameters grouped)
    {
        // voeg geneste parameters toe
        HtmlContainer nestedParamsPanel = new HtmlContainer("nestedParamsPanel");
        NestedParametersOuterTable nestedParametersTable =
            new NestedParametersOuterTable("nestedParams",
                    new ArrayList(grouped.nestedParams.values()));
        nestedParamsPanel.add(nestedParametersTable);
        if(grouped.nestedParams.isEmpty())
        {
            nestedParametersTable.setVisible(false);
        }
        outerPanel.add(nestedParamsPanel);
    }

    /**
     * Table voor de parameter onderdelen.
     */
    private static class NestedParametersOuterTable extends Table
    {
        /**
         * Construct.
         * @param componentName componentnaam
         * @param model list met NestedParametersWrappers
         */
        public NestedParametersOuterTable(String componentName, List model)
        {
            super(componentName, model);
        }

        /**
         * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
         */
        protected void populateCell(Cell cell)
        {
            GroupedParameters.NestedParametersWrapper wrapper =
                (GroupedParameters.NestedParametersWrapper)cell.getModelObject();
            NestedParameterTable table =
                new NestedParameterTable("nested", wrapper);
            cell.add(table);
        }

        /**
         * Table voor geneste parameters.
         */
        private static class NestedParameterTable extends Table
        {
            /**
             * Construct.
             * @param componentName componentnaam
             * @param model list met parametergroepen
             */
            public NestedParameterTable(String componentName, List model)
            {
                super(componentName, model);
            }

            /**
             * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
             */
            protected void populateCell(Cell cell)
            {
                NestedParameter parameter = (NestedParameter)cell.getModelObject();
                cell.add(new Label("name", parameter.getLocalId()));
            }
        }
    }

    //-------------------------------------------------------------------------------
    //--------------------- utilities -----------------------------------------------
    //-------------------------------------------------------------------------------

    /**
     * Groepeert de parameters in geneste/ niet geneste paramers, waarbij
     * de geneste parameter weer worden gegroepeerd naar het aantal kolommen
     * van de nesting.
     */
    private static class GroupedParameters
    {
        /** de niet-geneste parameters. */
        private List plainParams = new ArrayList();

        /** de geneste parameters. */
        private Map nestedParams = new HashMap();

        /**
         * Construct en groepeer.
         * @param parameters de te groeperen parameters
         */
        public GroupedParameters(Parameter[] parameters)
        {
            int len = parameters.length;
            for(int i = 0; i < len; i++)
            {
                add(parameters[i]);
            }
        }

        /**
         * Geeft of er helemaal geen parameters zijn gezet.
         * @return true indien er helemaal geen parameters zijn gezet, anders false
         */
        public boolean isEmpty()
        {
            return plainParams.isEmpty() && nestedParams.isEmpty();
        }

        /**
         * Voeg een parameter toe.
         * @param parameter de toe te voegen parameter
         */
        public void add(Parameter parameter)
        {
            if(!(parameter instanceof NestedParameter))
            {
                plainParams.add(parameter);
            }
            else
            {
                NestedParameter nested = (NestedParameter)parameter;
                int size = nested.getChildIds().size();
                NestedParametersWrapper wrapper =
                    (NestedParametersWrapper)nestedParams.get(String.valueOf(size));
                if(wrapper == null)
                {
                    wrapper = new NestedParametersWrapper(size);
                    nestedParams.put(String.valueOf(size), wrapper);
                }
                wrapper.addParameter(nested);
            }
        }

        /**
         * Wrapper klasse voor geneste parameters. De wrapper groepeert
         * de geneste parameters met hetzelfde aantal kolommen.
         */
        private static class NestedParametersWrapper extends ArrayList
        {
            /** het aantal kolommen. */
            private int cols;

            /**
             * Construct.
             * @param cols het aantal kolommen
             */
            public NestedParametersWrapper(int cols)
            {
                this.cols = cols;
            }

            /**
             * voeg een geneste parameter toe.
             * @param param de toe te voegen geneste parameter
             */
            public void addParameter(NestedParameter param)
            {
                add(param);
            }
        }
    }

}
