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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wicket.RequestCycle;
import wicket.markup.html.HtmlContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.IOnChangeListener;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.validation.IValidationErrorHandler;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.Panel;
import wicket.markup.html.table.ListItem;
import wicket.markup.html.table.ListView;

import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterRegistry;
import nl.openedge.gaps.core.parameters.SaveException;
import nl.openedge.gaps.core.parameters.impl.BooleanParameter;
import nl.openedge.gaps.core.parameters.impl.DateParameter;
import nl.openedge.gaps.core.parameters.impl.NestedParameter;
import nl.openedge.gaps.core.parameters.impl.NumericParameter;
import nl.openedge.gaps.core.parameters.impl.PercentageParameter;
import nl.openedge.gaps.core.parameters.impl.StringParameter;
import nl.openedge.gaps.support.ParameterBuilder;


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
    /** volgorde. */
    private static final List TYPEMAP_ORDER = new ArrayList();
    static
    {
        TYPEMAP_ORDER.add(TYPE_TEXT);
        TYPEMAP_ORDER.add(TYPE_NUMERIC);
        TYPEMAP_ORDER.add(TYPE_BOOLEAN);
        TYPEMAP_ORDER.add(TYPE_PERCENTAGE);
        TYPEMAP_ORDER.add(TYPE_DATE);
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
            new PlainParameterTable("plainParams", grouped.getPlainParams());
        paramForm.add(plainParametersTable);
        plainParamsPanel.add(paramForm);
        NewParameterForm newParamForm = new NewParameterForm("newParamForm", null);
        plainParamsPanel.add(newParamForm);
        if(grouped.getPlainParams().isEmpty())
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
         * @see wicket.markup.html.form.Form#handleSubmit()
         */
        public void handleSubmit()
        {
            // op dit moment handelen de specifieke models achter de input
            // velden alles af.
        }
    }

    /**
     * Form voor editen geneste parameters.
     */
    private static class NestedParameterForm extends Form
    {
        /**
         * Construct.
         * @param name component name
         * @param validationErrorHandler validation error handler
         */
        public NestedParameterForm(String name,
                IValidationErrorHandler validationErrorHandler)
        {
            super(name, validationErrorHandler);
        }

        /**
         * @see wicket.markup.html.form.Form#handleSubmit()
         */
        public void handleSubmit()
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
            typeChoiceModel.addAll(TYPEMAP_ORDER);
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
         * @see wicket.markup.html.form.Form#handleSubmit()
         */
        public void handleSubmit()
        {   
            try
            {
                ParameterBuilder builder = new ParameterBuilder();
                builder.navigate(group.getId());
                String type = (String)typeChoice.getModelObject();
                String name = (String)nameInput.getModelObject();
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
             * @see wicket.markup.html.form.IOnChangeListener#selectionChanged(wicket.RequestCycle, java.lang.Object)
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
    private class PlainParameterTable extends ListView
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
         * @see wicket.markup.html.table.Table#populateCell(wicket.markup.html.table.Cell)
         */
        protected void populateItem(ListItem cell)
        {
            final Parameter parameter = (Parameter)cell.getModelObject();
    		cell.add(new Label("name", parameter.getLocalId()));
    		cell.add(new TextField("value", new ParameterModel(parameter)));
    		cell.add(new Link("delete") {
                public void linkClicked()
                {
                    try
                    {
                        ParameterRegistry.removeParameter(parameter);
                        ParameterPanel.this.removeAll();
                        ParameterPanel.this.invalidateModel();
                        addParamComponents(parameter.getParameterGroup());
                    }
                    catch (SaveException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
    		});
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
                    new ArrayList(grouped.getNestedParams().values()));
        nestedParamsPanel.add(nestedParametersTable);
        if(grouped.getNestedParams().isEmpty())
        {
            nestedParametersTable.setVisible(false);
        }
        outerPanel.add(nestedParamsPanel);
    }

    /**
     * Table voor de parameter onderdelen.
     */
    private static class NestedParametersOuterTable extends ListView
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
         * @see wicket.markup.html.table.Table#populateCell(wicket.markup.html.table.Cell)
         */
        protected void populateItem(ListItem cell)
        {
            NestedParametersWrapper wrapper =
                (NestedParametersWrapper)cell.getModelObject();
            NestedParameter nested = (NestedParameter)wrapper.get(0); // pak eerste
            NestedParameterForm paramForm = new NestedParameterForm("paramForm", null);
            cell.add(paramForm);
            NestedParameterHeaderTable headerTable =
                new NestedParameterHeaderTable("header", nested);
            paramForm.add(headerTable);
            NestedParameterTable table =
                new NestedParameterTable("nested", wrapper);
            paramForm.add(table);
        }

        /**
         * Table voor de header van een groep geneste parameters.
         * UITGANGSPUNT is dat de rijen netjes zijn gegroepeerd, en dat we zodoende
         * gewoon de eerste rij kunnen pakken voor de kolomnamen.
         */
        private static class NestedParameterHeaderTable extends ListView
        {
            /**
             * Construct.
             * @param componentName componentnaam
             * @param model 1e rij van de geneste parameter
             */
            public NestedParameterHeaderTable(String componentName, NestedParameter nested)
            {
                super(componentName, Arrays.asList(nested.getNested()));
            }

            /**
             * @see wicket.markup.html.table.Table#populateCell(wicket.markup.html.table.Cell)
             */
            protected void populateItem(ListItem cell)
            {
                Parameter parameter = (Parameter)cell.getModelObject();
                cell.add(new Label("columnName", parameter.getLocalId()));
            }
        }

        /**
         * Table voor geneste parameters.
         */
        private static class NestedParameterTable extends ListView
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
             * @see wicket.markup.html.table.Table#populateCell(wicket.markup.html.table.Cell)
             */
            protected void populateItem(ListItem cell)
            {
                NestedParameter parameter = (NestedParameter)cell.getModelObject();
                List nestedModel = Arrays.asList(parameter.getNested());
                cell.add(new Label("name", parameter.getLocalId()));
        		cell.add(new NestedParameterRowTable("row", nestedModel));
            }

            /**
             * Table voor een rij/ geneste parameter waarbij een cell overeenkomt
             * met een geneste parameter. 
             */
            private static class NestedParameterRowTable extends ListView
            {
                /**
                 * Construct.
                 * @param componentName componentnaam
                 * @param model list met parametergroepen
                 */
                public NestedParameterRowTable(String componentName, List model)
                {
                    super(componentName, model);
                }

                /**
                 * @see wicket.markup.html.table.Table#populateCell(wicket.markup.html.table.Cell)
                 */
                protected void populateItem(ListItem cell)
                {
                    Parameter parameter = (Parameter)cell.getModelObject();
                    //cell.add(new TextField("value", new ParameterModel(parameter)));
            		cell.add(new Label("value", new ParameterModel(parameter)));
                }
            }
        }
    }


}
