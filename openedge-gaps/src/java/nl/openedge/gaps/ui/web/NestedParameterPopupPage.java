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

import java.util.Arrays;
import java.util.List;

import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.impl.NestedParameter;

import com.voicetribe.wicket.markup.html.HtmlPage;
import com.voicetribe.wicket.markup.html.basic.Label;
import com.voicetribe.wicket.markup.html.table.Cell;
import com.voicetribe.wicket.markup.html.table.Table;

/**
 * Pagina voor geneste parameters.
 */
public final class NestedParameterPopupPage extends HtmlPage
{
    /** nested tabel. */
    private NestedParameterTable valueTable;

    /** header tabel. */
    private NestedParameterHeaderTable headerTable;

	/**
	 * Constructor.
	 * @param nested nested parameter
	 */
	public NestedParameterPopupPage(NestedParametersWrapper wrapper)
	{
	    super();
	    NestedParameter nested = (NestedParameter)wrapper.get(0); // pak eerste
	    ParameterGroup group = nested.getParameterGroup();
	    add(new Label("path", group.getId()));
	    add(new Label("parameterGroupName", group.getLocalId()));
	    headerTable = new NestedParameterHeaderTable("header", nested);
	    add(headerTable);
	    valueTable = new NestedParameterTable("nested", wrapper);
	    add(valueTable);
	}

    /**
     * Table voor de header van een groep geneste parameters.
     * UITGANGSPUNT is dat de rijen netjes zijn gegroepeerd, en dat we zodoende
     * gewoon de eerste rij kunnen pakken voor de kolomnamen.
     */
    private static class NestedParameterHeaderTable extends Table
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
         * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
         */
        protected void populateCell(Cell cell)
        {
            Parameter parameter = (Parameter)cell.getModelObject();
            cell.add(new Label("columnName", parameter.getLocalId()));
        }
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
            cell.add(new NestedParameterRowTable("values",
                    Arrays.asList(parameter.getNested())));
        }

        /**
         * Table voor een rij van een geneste parameter.
         */
        private static class NestedParameterRowTable extends Table
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
             * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
             */
            protected void populateCell(Cell cell)
            {
                Parameter parameter = (Parameter)cell.getModelObject();
                cell.add(new Label("nestedvalue", new ParameterModel(parameter)));
            }
        }

    }
}
