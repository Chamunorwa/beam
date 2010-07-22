/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */
package org.esa.beam.dataio.netcdf.metadata.profiles.beam;

import org.esa.beam.dataio.netcdf.metadata.ProfilePart;
import org.esa.beam.dataio.netcdf.metadata.ProfileReadContext;
import org.esa.beam.dataio.netcdf.metadata.ProfileWriteContext;
import org.esa.beam.framework.dataio.ProductIOException;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.ColorPaletteDef;
import org.esa.beam.framework.datamodel.ImageInfo;
import org.esa.beam.framework.datamodel.Product;
import ucar.ma2.Array;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import static org.esa.beam.dataio.netcdf.util.ReaderUtils.*;

public class BeamImageInfoPart extends ProfilePart {

    public static final String COLOR_TABLE_SAMPLE_VALUES = "color_table_sample_values";
    public static final String COLOR_TABLE_RED_VALUES = "color_table_red_values";
    public static final String COLOR_TABLE_GREEN_VALUES = "color_table_green_values";
    public static final String COLOR_TABLE_BLUE_VALUES = "color_table_blue_values";

    @Override
    public void read(ProfileReadContext ctx, Product p) throws IOException {
        final List<Variable> variableList = ctx.getNetcdfFile().getVariables();
        for (Variable variable : variableList) {
            Band band = p.getBand(variable.getName());
            readImageInfo(variable, band);
        }
    }

    @Override
    public void define(ProfileWriteContext ctx, Product p) throws IOException {
        final Band[] bands = p.getBands();
        NetcdfFileWriteable fileWriteable = ctx.getNetcdfFileWriteable();
        for (Band band : bands) {
            ImageInfo imageInfo = band.getImageInfo();
            if (imageInfo != null) {
                Variable variable = fileWriteable.findVariable(band.getName());
                writeImageInfo(imageInfo.getColorPaletteDef().getPoints(), variable);
            }
        }
    }

    private static void writeImageInfo(ColorPaletteDef.Point[] points, Variable variable) {
        final double[] sampleValues = new double[points.length];
        final int[] redValues = new int[points.length];
        final int[] greenValues = new int[points.length];
        final int[] blueValues = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            ColorPaletteDef.Point point = points[i];
            sampleValues[i] = point.getSample();
            redValues[i] = point.getColor().getRed();
            greenValues[i] = point.getColor().getGreen();
            blueValues[i] = point.getColor().getBlue();
        }
        variable.addAttribute(new Attribute(COLOR_TABLE_SAMPLE_VALUES, Array.factory(sampleValues)));
        variable.addAttribute(new Attribute(COLOR_TABLE_RED_VALUES, Array.factory(redValues)));
        variable.addAttribute(new Attribute(COLOR_TABLE_GREEN_VALUES, Array.factory(greenValues)));
        variable.addAttribute(new Attribute(COLOR_TABLE_BLUE_VALUES, Array.factory(blueValues)));
    }

    private static void readImageInfo(Variable variable, Band band) throws ProductIOException {
        final Attribute sampleValues = variable.findAttributeIgnoreCase(COLOR_TABLE_SAMPLE_VALUES);
        final Attribute redValues = variable.findAttributeIgnoreCase(COLOR_TABLE_RED_VALUES);
        final Attribute greenValues = variable.findAttributeIgnoreCase(COLOR_TABLE_GREEN_VALUES);
        final Attribute blueValues = variable.findAttributeIgnoreCase(COLOR_TABLE_BLUE_VALUES);
        final Attribute[] attributes = {sampleValues, redValues, greenValues, blueValues};

        if (allAttributesAreNotNullAndHaveTheSameSize(attributes)) {
            final ColorPaletteDef.Point[] points = new ColorPaletteDef.Point[sampleValues.getLength()];
            for (int i = 0; i < points.length; i++) {
                final int red = redValues.getNumericValue(i).intValue();
                final int green = greenValues.getNumericValue(i).intValue();
                final int blue = blueValues.getNumericValue(i).intValue();
                final Color color = new Color(red, green, blue);
                points[i] = new ColorPaletteDef.Point(sampleValues.getNumericValue(i).doubleValue(), color);

            }
            band.setImageInfo(new ImageInfo(new ColorPaletteDef(points)));
        }
    }
}