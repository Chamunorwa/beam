package org.esa.beam.visat.toolviews.imageinfo;

import com.bc.ceres.binding.ValueContainer;
import com.bc.ceres.binding.ValueModel;
import com.bc.ceres.binding.ValueSet;
import com.bc.ceres.binding.swing.Binding;
import com.bc.ceres.binding.swing.BindingContext;
import com.jidesoft.combobox.ColorComboBox;
import org.esa.beam.framework.datamodel.ImageInfo;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

class MoreOptionsForm {
    static final String NO_DATA_COLOR_PROPERTY = "noDataColor";
    static final String HISTOGRAM_MATCHING_PROPERTY = "histogramMatching";

    private JPanel contentPanel;
    private GridBagConstraints constraints;
    private BindingContext bindingContext;

    private ColorManipulationForm parentForm;
    private boolean hasHistogramMatching;

    MoreOptionsForm(ColorManipulationForm parentForm, boolean hasHistogramMatching) {
        this.parentForm = parentForm;
        ValueContainer valueContainer = new ValueContainer();
        valueContainer.addModel(ValueModel.createValueModel(NO_DATA_COLOR_PROPERTY, ImageInfo.NO_COLOR));

        this.hasHistogramMatching = hasHistogramMatching;
        if (this.hasHistogramMatching) {
            valueContainer.addModel(ValueModel.createValueModel(HISTOGRAM_MATCHING_PROPERTY, ImageInfo.HistogramMatching.None));
            valueContainer.getDescriptor(HISTOGRAM_MATCHING_PROPERTY).setNotNull(true);
            valueContainer.getDescriptor(HISTOGRAM_MATCHING_PROPERTY).setValueSet(new ValueSet(
                    new ImageInfo.HistogramMatching[]{
                            ImageInfo.HistogramMatching.None,
                            ImageInfo.HistogramMatching.Equalize,
                            ImageInfo.HistogramMatching.Normalize,
                    })
            );
        }

        contentPanel = new JPanel(new GridBagLayout());

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.BASELINE;
        constraints.weightx = 0.5;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(1, 0, 1, 0);

        bindingContext = new BindingContext(valueContainer);

        final PropertyChangeListener pcl = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                updateModel();
            }
        };

        JLabel noDataColorLabel = new JLabel("No-data colour: ");
        ColorComboBox noDataColorComboBox = new ColorComboBox();
        noDataColorComboBox.setColorValueVisible(false);
        noDataColorComboBox.setAllowDefaultColor(true);
        Binding noDataColorBinding = bindingContext.bind(NO_DATA_COLOR_PROPERTY, new ColorComboBoxAdapter(noDataColorComboBox));
        noDataColorBinding.addComponent(noDataColorLabel);
        addRow(noDataColorLabel, noDataColorComboBox);
        bindingContext.addPropertyChangeListener(NO_DATA_COLOR_PROPERTY, pcl);

        if (hasHistogramMatching) {
            JLabel histogramMatchingLabel = new JLabel("Histogram matching: ");
            JComboBox histogramMatchingBox = new JComboBox();
            Binding histogramMatchingBinding = bindingContext.bind(HISTOGRAM_MATCHING_PROPERTY, histogramMatchingBox);
            histogramMatchingBinding.addComponent(histogramMatchingLabel);
            addRow(histogramMatchingLabel, histogramMatchingBox);
            bindingContext.addPropertyChangeListener(HISTOGRAM_MATCHING_PROPERTY, pcl);
        }
    }

    private ImageInfo getImageInfo() {
        return getParentForm().getImageInfo();
    }

    public ColorManipulationForm getParentForm() {
        return parentForm;
    }

    public BindingContext getBindingContext() {
        return bindingContext;
    }

    public void addRow(JLabel label, JComponent editor) {
        constraints.gridwidth = 1;
        constraints.gridy++;
        constraints.gridx = 0;
        contentPanel.add(label, constraints);
        constraints.gridx = 1;
        contentPanel.add(editor, constraints);
    }

    public void addRow(JComponent editor) {
        constraints.gridwidth = 2;
        constraints.gridy++;
        constraints.gridx = 0;
        contentPanel.add(editor, constraints);
    }
        
    public void updateForm() {
        setNoDataColor(getImageInfo().getNoDataColor());
        if (hasHistogramMatching) {
            setHistogramMatching(getImageInfo().getHistogramMatching());
        }
    }

    public void updateModel() {
        getImageInfo().setNoDataColor(getNoDataColor());
        if (hasHistogramMatching) {
            getImageInfo().setHistogramMatching(getHistogramMatching());
        }
        getParentForm().setApplyEnabled(true);
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        bindingContext.addPropertyChangeListener(propertyChangeListener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener propertyChangeListener) {
        bindingContext.addPropertyChangeListener(propertyName, propertyChangeListener);
    }

    private Color getNoDataColor() {
        return (Color) getBindingContext().getBinding(NO_DATA_COLOR_PROPERTY).getPropertyValue();
    }

    private void setNoDataColor(Color color) {
        getBindingContext().getBinding(NO_DATA_COLOR_PROPERTY).setPropertyValue(color);
    }

    private ImageInfo.HistogramMatching getHistogramMatching() {
        return (ImageInfo.HistogramMatching) getBindingContext().getBinding(HISTOGRAM_MATCHING_PROPERTY).getPropertyValue();
    }

    private void setHistogramMatching(ImageInfo.HistogramMatching histogramMatching) {
        getBindingContext().getBinding(HISTOGRAM_MATCHING_PROPERTY).setPropertyValue(histogramMatching);
    }
}
