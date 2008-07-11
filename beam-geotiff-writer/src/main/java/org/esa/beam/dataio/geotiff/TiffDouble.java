package org.esa.beam.dataio.geotiff;

import org.esa.beam.framework.datamodel.ProductData;

/**
 * A TIFFValue implementation for the GeoTIFF format.
 *
 * @author Marco Peters
 * @author Sabine Embacher
 * @author Norman Fomferra
 * @version $Revision$ $Date$
 */
class TiffDouble extends TiffValue {

    public TiffDouble(final double value) {
        setData(ProductData.createInstance(ProductData.TYPE_FLOAT64));
        getData().setElemDouble(value);
    }

    public double getValue() {
        return getData().getElemDouble();
    }
}
