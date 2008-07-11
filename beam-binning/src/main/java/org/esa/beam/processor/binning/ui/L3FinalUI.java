/*
 * $Id: L3FinalUI.java,v 1.5 2007/04/18 13:01:12 norman Exp $
 *
 * Copyright (C) 2002 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.esa.beam.processor.binning.ui;

import org.esa.beam.framework.param.ParamChangeEvent;
import org.esa.beam.framework.param.ParamChangeListener;
import org.esa.beam.framework.param.ParamGroup;
import org.esa.beam.framework.param.ParamValidateException;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.framework.processor.ProcessorConstants;
import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.framework.processor.ProcessorUtils;
import org.esa.beam.framework.processor.Request;
import org.esa.beam.framework.processor.RequestElementFactoryException;
import org.esa.beam.framework.help.HelpSys;
import org.esa.beam.processor.binning.L3Constants;
import org.esa.beam.processor.binning.L3Processor;
import org.esa.beam.util.Debug;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.List;

/**
 * Provides the user interface functionality for the Level 3 final processor.
 */
public class L3FinalUI extends L3UI {

    private JPanel uiPane;
    private Request finalRequest;

    /**
     * Constructs the object with given processor.
     */
    public L3FinalUI(final L3Processor processor) throws ProcessorException {
        super(processor);
        uiPane = null;
        createParameterGroup();
    }

    /**
     * Retrieves the base component for the processor specific user interface classes. This can be any Java Swing
     * containertype.
     */
    public JComponent getGuiComponent() {
        if (uiPane == null) {
            createUI();
        }
        return uiPane;
    }

    public void setRequests() throws ProcessorException {
        ensureFinalRequest();
    }

    /**
     * Retrieves the requests currently edited.
     */
    protected void collectRequestsFromUI(final List requests) throws ProcessorException {
        final Request request = new Request();
        request.setType(L3Constants.REQUEST_TYPE);
        addParameterToRequest(request);

        this.requests.add(request);
    }

    /**
     * Create a set of new default requests.
     */
    protected void setDefaultRequestsImpl() throws ProcessorException {
        finalRequest = new Request();

        try {
            finalRequest.setType(L3Constants.REQUEST_TYPE);
            finalRequest.addParameter(reqElemFactory.createParameter(L3Constants.PROCESS_TYPE_PARAM_NAME,
                                                                       L3Constants.PROCESS_TYPE_FINALIZE));
            finalRequest.addParameter(reqElemFactory.generateDefaultDbLocation());
            finalRequest.addParameter(
                    reqElemFactory.createParamWithDefaultValueSet(L3Constants.DELETE_DB_PARAMETER_NAME));
            finalRequest.addParameter(
                    reqElemFactory.createDefaultLogPatternParameter(L3Constants.DEFAULT_LOG_PREFIX_FINAL));
            finalRequest.addParameter(
                    reqElemFactory.createParamWithDefaultValueSet(L3Constants.TAILORING_PARAM_NAME));
            try {
                finalRequest.addParameter(reqElemFactory.createLogToOutputParameter("false"));
            } catch (ParamValidateException e) {
                logger.warning("Unable to validate parameter '" + ProcessorConstants.LOG_TO_OUTPUT_PARAM_NAME + "'");
                Debug.trace(e);
            }
        } catch (RequestElementFactoryException e) {
            throw e;
        }

        updateUI();
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Creates the user interface
     */
    private void createUI() {
        uiPane = createOutParamsPane();
        HelpSys.enableHelp(uiPane, "binningFinalTool");
    }

    /**
     * Creates the parameter group
     */
    private void createParameterGroup() throws ProcessorException {
        paramGroup = new ParamGroup();

        try {
            paramGroup.addParameter(reqElemFactory.createParameter(L3Constants.PROCESS_TYPE_PARAM_NAME,
                                                                     L3Constants.PROCESS_TYPE_FINALIZE));
            paramGroup.addParameter(reqElemFactory.generateDefaultDbLocation());
            paramGroup.addParameter(reqElemFactory.createDefaultOutputProductParameter());
            paramGroup.addParameter(reqElemFactory.createOutputFormatParameter());
            paramGroup.addParameter(
                    reqElemFactory.createParamWithDefaultValueSet(L3Constants.DELETE_DB_PARAMETER_NAME));
            paramGroup.addParameter(
                    reqElemFactory.createDefaultLogPatternParameter(L3Constants.DEFAULT_LOG_PREFIX_FINAL));
            paramGroup.addParameter(reqElemFactory.createParamWithDefaultValueSet(L3Constants.TAILORING_PARAM_NAME));
            try {
                paramGroup.addParameter(reqElemFactory.createLogToOutputParameter("false"));
            } catch (ParamValidateException e) {
                logger.warning("Unable to validate parameter '" + ProcessorConstants.LOG_TO_OUTPUT_PARAM_NAME + "'");
                Debug.trace(e);
            }
            paramGroup.addParamChangeListener(new ParamChangeListener() {
                public void parameterValueChanged(final ParamChangeEvent event) {
                    // Must not respect any parameter changes
                }
            });
        } catch (RequestElementFactoryException e) {
            throw e;
        }
    }

    /**
     * Checks the requests vector for a request of type finalize and sets the one found to the member field.
     */
    private void ensureFinalRequest() throws ProcessorException {
        Request request = null;
        Parameter param = null;
        String value = null;
        boolean bFound = false;

        for (int n = 0; n < requests.size(); n++) {
            request = (Request) requests.elementAt(n);
            // check for correct type
            if (!request.isRequestType(L3Constants.REQUEST_TYPE)) {
                continue;
            }

            param = request.getParameter(L3Constants.PROCESS_TYPE_PARAM_NAME);
            if (param == null) {
                continue;
            }

            value = param.getValueAsText();
            if (value == null) {
                continue;
            }

            if (!value.equalsIgnoreCase(L3Constants.PROCESS_TYPE_FINALIZE)) {
                continue;
            }

            // passed all tests
            bFound = true;
            break;
        }

        if (bFound) {
            finalRequest = request;
        } else {
            setDefaultRequests();
        }
    }

    /**
     * Updates the UI and the connected parameters with the values of the currenr request
     */
    protected void updateUI() throws ProcessorException {
        try {
            setOutputFile(finalRequest);
            updateUIComponent(L3Constants.DATABASE_PARAM_NAME);
            updateUIComponent(L3Constants.DELETE_DB_PARAMETER_NAME);
            updateUIComponent(ProcessorConstants.LOG_PREFIX_PARAM_NAME);
            updateUIComponent(ProcessorConstants.LOG_TO_OUTPUT_PARAM_NAME);
            updateUIComponent(L3Constants.TAILORING_PARAM_NAME);
        } catch (ParamValidateException e) {
            throw new ProcessorException(e.getMessage(), e);
        }
    }

    private void updateUIComponent(final String paramName) throws ParamValidateException {
        final Parameter param;
        final Parameter toUpdate;
        param = finalRequest.getParameter(paramName);
        toUpdate = paramGroup.getParameter(paramName);
        if (param != null) {
            toUpdate.setValue(param.getValue());
        }
    }

    /**
     * Adds all parameter needed for a complete request from the UI components the request passed in.
     */
    private void addParameterToRequest(final Request request) throws ProcessorException {
        request.addParameter(reqElemFactory.createParameter(L3Constants.PROCESS_TYPE_PARAM_NAME,
                                                             L3Constants.PROCESS_TYPE_FINALIZE));
        request.addParameter(paramGroup.getParameter(L3Constants.DATABASE_PARAM_NAME));
        final String fileName = paramGroup.getParameter(L3Constants.OUTPUT_PRODUCT_PARAM_NAME).getValueAsText();

        final String format = paramGroup.getParameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME).getValueAsText();
        request.addOutputProduct(ProcessorUtils.createProductRef(fileName, format));
        request.addParameter(paramGroup.getParameter(L3Constants.DELETE_DB_PARAMETER_NAME));
        request.addParameter(paramGroup.getParameter(ProcessorConstants.LOG_PREFIX_PARAM_NAME));
        request.addParameter(paramGroup.getParameter(ProcessorConstants.LOG_TO_OUTPUT_PARAM_NAME));
        request.addParameter(paramGroup.getParameter(L3Constants.TAILORING_PARAM_NAME));
    }
}
