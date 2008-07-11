/*
 * $Id: SmacUtilsTest.java,v 1.1.1.1 2006/09/11 08:16:52 norman Exp $
 *
 * Copyright (C) 2002,2003  by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation. This program is distributed in the hope it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 */
package org.esa.beam.processor.smac;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.processor.ProcessorException;

public class SmacUtilsTest extends TestCase {

    public SmacUtilsTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(SmacUtilsTest.class);
    }

    public void testGetSensorTypeDoesNotAcceptNullParameter() throws ProcessorException {
        try {
            SmacUtils.getSensorType(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testGetSensorTypeReturnsCorrectType() throws ProcessorException {
        String type;

        type = SmacUtils.getSensorType(EnvisatConstants.AATSR_L1B_TOA_PRODUCT_TYPE_NAME);
        assertEquals(SensorCoefficientManager.AATSR_NAME, type);

        type = SmacUtils.getSensorType(EnvisatConstants.MERIS_FR_L1B_PRODUCT_TYPE_NAME);
        assertEquals(SensorCoefficientManager.MERIS_NAME, type);

        type = SmacUtils.getSensorType(EnvisatConstants.MERIS_RR_L1B_PRODUCT_TYPE_NAME);
        assertEquals(SensorCoefficientManager.MERIS_NAME, type);
    }

    public void testGetSensorTypeFailsOnIllegalTypes() {
        try {
            SmacUtils.getSensorType("Nasenann");
            fail("ProcessorException expected");
        } catch (ProcessorException expected) {
        }

        try {
            SmacUtils.getSensorType("strange");
            fail("ProcessorException expected");
        } catch (ProcessorException expected) {
        }

        try {
            SmacUtils.getSensorType("");
            fail("ProcessorException expected");
        } catch (ProcessorException expected) {
        }
    }

    public void testIsSupportedFileType() {
         try {
            SmacUtils.isSupportedProductType(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        assertTrue(SmacUtils.isSupportedProductType(EnvisatConstants.AATSR_L1B_TOA_PRODUCT_TYPE_NAME));
        assertTrue(SmacUtils.isSupportedProductType(EnvisatConstants.MERIS_FR_L1B_PRODUCT_TYPE_NAME));
        assertTrue(SmacUtils.isSupportedProductType(EnvisatConstants.MERIS_RR_L1B_PRODUCT_TYPE_NAME));

        assertFalse(SmacUtils.isSupportedProductType("TomType"));
        assertFalse(SmacUtils.isSupportedProductType("NonExistingType"));
    }
}
