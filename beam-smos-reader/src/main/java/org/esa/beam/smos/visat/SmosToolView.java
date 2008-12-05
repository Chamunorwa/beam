package org.esa.beam.smos.visat;

import com.bc.ceres.glayer.support.ImageLayer;
import com.bc.ceres.binio.CompoundData;
import com.bc.ceres.binio.Type;
import com.bc.ceres.binio.SimpleType;
import com.bc.ceres.binio.CompoundType;
import org.esa.beam.dataio.smos.SmosProductReader;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.ui.PixelPositionListener;
import org.esa.beam.framework.ui.application.support.AbstractToolView;
import org.esa.beam.framework.ui.product.ProductSceneView;
import org.esa.beam.visat.VisatApp;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.io.IOException;
import java.math.BigInteger;

public abstract class SmosToolView extends AbstractToolView {

    private ProductSceneView smosView;
    private PPL ppl;
    private IFL ifl;
    private JPanel panel;
    private JLabel defaultControl;
    private JComponent smosComponent;
    private Product smosProduct;
    private SmosProductReader smosProductReader;

    protected SmosToolView() {
        ppl = new PPL();
        ifl = new IFL();
    }

    @Override
    protected JComponent createControl() {
        panel = new JPanel(new BorderLayout());
        URL resource = getClass().getResource("smos-icon.png");
        if (resource != null) {
            defaultControl = new JLabel(new ImageIcon(resource));
        } else {
            defaultControl = new JLabel();
        }
        defaultControl.setIconTextGap(10);
        defaultControl.setText("No SMOS image selected.");
        panel.add(defaultControl);
        activateToolView();
        return panel;
    }

    @Override
    public void componentOpened() {
        System.out.println("[SMOS-Box] componentOpened");
        activateToolView();
    }

    @Override
    public void componentClosed() {
        System.out.println("[SMOS-Box] componentClosed");
        deactivateToolView();
    }

    @Override
    public void componentShown() {
        System.out.println("[SMOS-Box] componentShown");
        activateToolView();
    }

    @Override
    public void componentHidden() {
        System.out.println("[SMOS-Box] componentHidden");
        deactivateToolView();
    }

    @Override
    public void dispose() {
        super.dispose();
        getVisatApp().removeInternalFrameListener(ifl);
    }

    public ProductSceneView getSmosView() {
        return smosView;
    }

    public Product getSmosProduct() {
        return smosProduct;
    }

    public SmosProductReader getSmosProductReader() {
        return smosProductReader;
    }

    public VisatApp getVisatApp() {
        return VisatApp.getApp();
    }

    public void setView(ProductSceneView view) {
        ProductSceneView oldSmosView = smosView;
        if (smosView != view) {
            uninstallPPL();
            if (view != null) {
                final Product product = view.getProduct();
                final ProductReader productReader = product.getProductReader();
                if (productReader instanceof SmosProductReader) {
                    smosProductReader = (SmosProductReader) productReader;
                    smosProduct = product;
                    smosView = view;
                    if (smosComponent == null) {
                        smosComponent = createSmosComponent(smosView);
                    }
                    setToolViewComponent(smosComponent);
                    installPPL();
                } else {
                    handleNoSmos();
                }
            } else {
                handleNoSmos();
            }
            updateSmosComponent(oldSmosView, smosView);
        }
    }
    public static Number getNumbericMember(CompoundData compoundData, int memberIndex) throws IOException {
        Type memberType = compoundData.getCompoundType().getMemberType(memberIndex);
        Number number;
        if (memberType == SimpleType.DOUBLE) {
            number = compoundData.getDouble(memberIndex);
        } else if (memberType == SimpleType.FLOAT) {
            number = compoundData.getFloat(memberIndex);
        } else if (memberType == SimpleType.ULONG) {
            // This mask is used to obtain the value of an int as if it were unsigned.
            BigInteger mask = BigInteger.valueOf(0xffffffffffffffffL);
            BigInteger bi = BigInteger.valueOf(compoundData.getLong(memberIndex));
            number = bi.and(mask);
        } else if (memberType == SimpleType.LONG || memberType == SimpleType.UINT) {
            number = compoundData.getDouble(memberIndex);
        } else if (memberType == SimpleType.INT || memberType == SimpleType.USHORT) {
            number = compoundData.getDouble(memberIndex);
        } else if (memberType == SimpleType.SHORT || memberType == SimpleType.UBYTE) {
            number = compoundData.getDouble(memberIndex);
        } else if (memberType == SimpleType.BYTE) {
            number = compoundData.getDouble(memberIndex);
        } else {
            number = null;
        }
        return number;
    }

    public static Class<? extends Number> getNumbericMemberType(CompoundType compoundData, int memberIndex) {
        Type memberType = compoundData.getMemberType(memberIndex);
        Class<? extends Number> numberClass;
        if (memberType == SimpleType.DOUBLE) {
            numberClass = Double.class;
        } else if (memberType == SimpleType.FLOAT) {
            numberClass = Float.class;
        } else if (memberType == SimpleType.ULONG) {
            numberClass = BigInteger.class;
        } else if (memberType == SimpleType.LONG || memberType == SimpleType.UINT) {
            numberClass = Long.class;
        } else if (memberType == SimpleType.INT || memberType == SimpleType.USHORT) {
            numberClass = Integer.class;
        } else if (memberType == SimpleType.SHORT || memberType == SimpleType.UBYTE) {
            numberClass = Short.class;
        } else if (memberType == SimpleType.BYTE) {
            numberClass = Byte.class;
        } else {
            numberClass = null;
        }
        return numberClass;
    }



    
    private void handleNoSmos() {
        smosProductReader = null;
        smosProduct = null;
        smosView = null;
        setToolViewComponent(defaultControl);
    }

    private void setToolViewComponent(JComponent comp) {
        panel.removeAll();
        panel.add(comp, BorderLayout.CENTER);
        panel.invalidate();
        panel.validate();
    }

    private void activateToolView() {
        setView(getVisatApp().getSelectedProductSceneView());
        installIFL();
    }

    private void deactivateToolView() {
        uninstallIFL();
        setView(null);
    }

    private void installIFL() {
        getVisatApp().addInternalFrameListener(ifl);
    }

    private void uninstallIFL() {
        getVisatApp().removeInternalFrameListener(ifl);
    }

    private void installPPL() {
        if (this.smosView != null) {
            this.smosView.addPixelPositionListener(ppl);
        }
    }

    private void uninstallPPL() {
        if (this.smosView != null) {
            this.smosView.removePixelPositionListener(ppl);
        }
    }

    protected void handlePixelPosChanged(ImageLayer baseImageLayer,
                                         int pixelX,
                                         int pixelY,
                                         int currentLevel,
                                         boolean pixelPosValid) {
    }

    protected void handlePixelPosNotAvailable() {
    }

    protected abstract JComponent createSmosComponent(ProductSceneView smosView);

    protected void updateSmosComponent(ProductSceneView oldView, ProductSceneView newView) {
    }

    private class IFL extends InternalFrameAdapter {

        @Override
        public void internalFrameActivated(final InternalFrameEvent e) {
            final ProductSceneView view = getProductSceneViewByFrame(e);
            setView(view);
        }

        @Override
        public void internalFrameDeactivated(final InternalFrameEvent e) {
            if (getSmosView() == getProductSceneViewByFrame(e)) {
                setView(null);
            }
        }

        private ProductSceneView getProductSceneViewByFrame(final InternalFrameEvent e) {
            final Container content = getContent(e);
            if (content instanceof ProductSceneView) {
                return (ProductSceneView) content;
            } else {
                return null;
            }
        }

        private Container getContent(InternalFrameEvent e) {
            return e.getInternalFrame().getContentPane();
        }
    }

    private class PPL implements PixelPositionListener {
        public void pixelPosChanged(ImageLayer baseImageLayer, int pixelX, int pixelY, int currentLevel, boolean pixelPosValid, MouseEvent e) {
            handlePixelPosChanged(baseImageLayer, pixelX, pixelY, currentLevel, pixelPosValid);
        }

        public void pixelPosNotAvailable() {
            handlePixelPosNotAvailable();
        }
    }


}