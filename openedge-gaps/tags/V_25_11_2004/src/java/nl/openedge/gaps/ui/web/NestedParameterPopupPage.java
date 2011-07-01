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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.impl.NestedParameter;

import com.voicetribe.util.convert.ConverterRegistry;
import com.voicetribe.util.convert.FormattingUtils;
import com.voicetribe.wicket.Model;
import com.voicetribe.wicket.RequestCycle;
import com.voicetribe.wicket.markup.ComponentTag;
import com.voicetribe.wicket.markup.MarkupStream;
import com.voicetribe.wicket.markup.html.HtmlComponent;
import com.voicetribe.wicket.markup.html.HtmlPage;
import com.voicetribe.wicket.markup.html.basic.Label;
import com.voicetribe.wicket.markup.html.table.Cell;
import com.voicetribe.wicket.markup.html.table.Table;

/**
 * Pagina voor geneste parameters.
 */
public final class NestedParameterPopupPage extends HtmlPage
{
    /** Nederlandse locale. */
    private static final Locale DUTCH = new Locale("nl", "NL");

    /** nested tabel. */
    private NestedParameterTable valueTable;

    /** header tabel. */
    private NestedParameterHeaderTable headerTable;

    /** format utils instantie. */
    private FormattingUtils formatUtils;

	/**
	 * Constructor.
	 * @param nested nested parameter
	 */
	public NestedParameterPopupPage(NestedParametersWrapper wrapper)
	{
	    super();
	    ConverterRegistry converterRegistry = getApplicationSettings().getConverterRegistry();
	    formatUtils = converterRegistry.getFormattingUtils();
	    NestedParameter nested = (NestedParameter)wrapper.get(0); // pak eerste
	    ParameterGroup group = nested.getParameterGroup();
	    add(new Label("path", group.getId()));
	    add(new Label("parameterGroupName", group.getLocalId()));
	    headerTable = new NestedParameterHeaderTable("header", nested);
	    add(headerTable);
	    addContentRaw(wrapper);
	}

	/**
	 * Voeg tabel toe als 'raw content' (efficient). Plak aan tbody element
	 * @param wrapper de wrapper met de rijen
	 */
	private void addContentRaw(NestedParametersWrapper wrapper)
	{
	    long begin = System.currentTimeMillis();
        StringBuffer b = new StringBuffer();
        NestedParameter parameter = null;
        for(Iterator i = wrapper.iterator(); i.hasNext();)
        {
            parameter = (NestedParameter)i.next();
            b.append("<tr><td>").append(parameter.getLocalId()).append("</td>");
            appendRow(b, parameter);
            b.append("</tr>");
        }
        long end = System.currentTimeMillis();
        System.err.println("*** comp created in " + Math.round((end - begin) / 1000) + " secs");
        add(new RawContentComponent("content", b.toString()));
	}

    /**
     * Voeg rij toe.
     * @param b string buffer
     * @param parameter parameter
     */
    private void appendRow(StringBuffer b, NestedParameter parameter)
    {
        Parameter[] nested = parameter.getNested();
        int len = nested.length;
        for(int i = 0; i < len; i++)
        {
            b.append("<td>");
            Object value = nested[i].getValue().getValue();
            b.append(formatUtils.getObjectFormatted(value, DUTCH));
            b.append("</td>");
        }
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
    private class NestedParameterTable extends Table
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
            StringBuffer b = new StringBuffer();
            Parameter[] nested = parameter.getNested();
            int len = nested.length;
            for(int i = 0; i < len; i++)
            {
                b.append("<td>");
                Object value = nested[i].getValue().getValue();
                b.append(formatUtils.getObjectFormatted(value, DUTCH));
                b.append("</td>");
            }
            cell.add(new Label("row", b.toString()));
        }
    }

    /**
     * Component voor rauwe content.
     */
    private static class RawContentComponent extends HtmlComponent
    {
        /**
         * Construct.
         * @param name component name
         * @param text body
         */
        public RawContentComponent(String name, String text)
        {
            super(name);
            setModel(new Model(text));
        }

        /**
         * Allows modification of component tag.
         * @param cycle The request cycle
         * @param tag The tag to modify
         * @see com.voicetribe.wicket.Component#handleComponentTag(RequestCycle, com.voicetribe.wicket.markup.ComponentTag)
         */
        protected final void handleComponentTag(final RequestCycle cycle, final ComponentTag tag)
        {
            //checkTag(tag, "tbody"); // wat te dwingend; hier niet nodig
            super.handleComponentTag(cycle, tag);
        }

        /**
         * @see com.voicetribe.wicket.Component#handleBody(com.voicetribe.wicket.RequestCycle, com.voicetribe.wicket.markup.MarkupStream, com.voicetribe.wicket.markup.ComponentTag)
         */
        protected void handleBody(final RequestCycle cycle, final MarkupStream markupStream, final ComponentTag openTag)
        {
            replaceBody(cycle, markupStream, openTag, (String)getModelObject());
        }
    }
}
