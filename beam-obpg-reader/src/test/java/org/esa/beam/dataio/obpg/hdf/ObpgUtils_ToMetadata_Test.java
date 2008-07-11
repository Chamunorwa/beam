/*
 * $Id$
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
package org.esa.beam.dataio.obpg.hdf;

import junit.framework.TestCase;
import ncsa.hdf.hdflib.HDFConstants;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.ProductData;

public class ObpgUtils_ToMetadata_Test extends TestCase {

    private ObpgUtils obpgUtils;

    protected void setUp() throws Exception {
        obpgUtils = new ObpgUtils();
    }

    public void test_single_int() {
        final HdfAttribute hdfAttribute = new HdfAttribute("name", HDFConstants.DFNT_INT32, "23985623", 1);

        final MetadataAttribute metaAttribute = obpgUtils.attributeToMetadata(hdfAttribute);

        assertNotNull(metaAttribute);
        assertEquals("name", metaAttribute.getName());
        assertEquals(1, metaAttribute.getNumDataElems());
        assertEquals(ProductData.TYPE_INT32, metaAttribute.getDataType());
        assertTrue(metaAttribute.getData() instanceof ProductData.Int);
        final Object dataElems = metaAttribute.getDataElems();
        assertTrue(dataElems instanceof int[]);
        final int[] ints = (int[]) dataElems;
        assertEquals(1, ints.length);
        assertEquals(23985623, ints[0]);
    }

    public void test_three_Ints() {
        final HdfAttribute hdfAttribute = new HdfAttribute("name", HDFConstants.DFNT_INT16, "23,56,1234", 3);

        final MetadataAttribute metaAttribute = obpgUtils.attributeToMetadata(hdfAttribute);

        assertNotNull(metaAttribute);
        assertEquals("name", metaAttribute.getName());
        assertEquals(3, metaAttribute.getNumDataElems());
        assertEquals(ProductData.TYPE_INT32, metaAttribute.getDataType());
        assertTrue(metaAttribute.getData() instanceof ProductData.Int);
        final Object dataElems = metaAttribute.getDataElems();
        assertTrue(dataElems instanceof int[]);
        final int[] ints = (int[]) dataElems;
        assertEquals(3, ints.length);
        assertEquals(23, ints[0]);
        assertEquals(56, ints[1]);
        assertEquals(1234, ints[2]);
    }

    public void test_single_float() {
        final HdfAttribute hdfAttribute = new HdfAttribute("name", HDFConstants.DFNT_FLOAT, "0.23", 1);

        final MetadataAttribute metaAttribute = obpgUtils.attributeToMetadata(hdfAttribute);

        assertNotNull(metaAttribute);
        assertEquals("name", metaAttribute.getName());
        assertEquals(1, metaAttribute.getNumDataElems());
        assertEquals(ProductData.TYPE_FLOAT32, metaAttribute.getDataType());
        assertTrue(metaAttribute.getData() instanceof ProductData.Float);
        final Object dataElems = metaAttribute.getDataElems();
        assertTrue(dataElems instanceof float[]);
        final float[] floats = (float[]) dataElems;
        assertEquals(1, floats.length);
        assertEquals(0.23f, floats[0]);
    }

    public void test_three_floats() {
        final HdfAttribute hdfAttribute = new HdfAttribute("name", HDFConstants.DFNT_FLOAT, "0.23,3.56,1234.34", 3);

        final MetadataAttribute metaAttribute = obpgUtils.attributeToMetadata(hdfAttribute);

        assertNotNull(metaAttribute);
        assertEquals("name", metaAttribute.getName());
        assertEquals(3, metaAttribute.getNumDataElems());
        assertEquals(ProductData.TYPE_FLOAT32, metaAttribute.getDataType());
        assertTrue(metaAttribute.getData() instanceof ProductData.Float);
        final Object dataElems = metaAttribute.getDataElems();
        assertTrue(dataElems instanceof float[]);
        final float[] floats = (float[]) dataElems;
        assertEquals(3, floats.length);
        assertEquals(0.23f, floats[0]);
        assertEquals(3.56f, floats[1]);
        assertEquals(1234.34f, floats[2]);
    }

    public void test_single_double() {
        final HdfAttribute hdfAttribute = new HdfAttribute("name", HDFConstants.DFNT_FLOAT64, "0.56", 1);

        final MetadataAttribute metaAttribute = obpgUtils.attributeToMetadata(hdfAttribute);

        assertNotNull(metaAttribute);
        assertEquals("name", metaAttribute.getName());
        assertEquals(1, metaAttribute.getNumDataElems());
        assertEquals(ProductData.TYPE_FLOAT64, metaAttribute.getDataType());
        assertTrue(metaAttribute.getData() instanceof ProductData.Double);
        final Object dataElems = metaAttribute.getDataElems();
        assertTrue(dataElems instanceof double[]);
        final double[] doubles = (double[]) dataElems;
        assertEquals(1, doubles.length);
        assertEquals(0.56, doubles[0]);
    }

    public void test_three_doubles() {
        final HdfAttribute hdfAttribute = new HdfAttribute("name", HDFConstants.DFNT_FLOAT64, "0.23,3.56,1234.34", 3);

        final MetadataAttribute metaAttribute = obpgUtils.attributeToMetadata(hdfAttribute);

        assertNotNull(metaAttribute);
        assertEquals("name", metaAttribute.getName());
        assertEquals(3, metaAttribute.getNumDataElems());
        assertEquals(ProductData.TYPE_FLOAT64, metaAttribute.getDataType());
        assertTrue(metaAttribute.getData() instanceof ProductData.Double);
        final Object dataElems = metaAttribute.getDataElems();
        assertTrue(dataElems instanceof double[]);
        final double[] doubles = (double[]) dataElems;
        assertEquals(3, doubles.length);
        assertEquals(0.23, doubles[0]);
        assertEquals(3.56, doubles[1]);
        assertEquals(1234.34, doubles[2]);
    }

    public void test_single_char() {
        final HdfAttribute hdfAttribute = new HdfAttribute("name", HDFConstants.DFNT_CHAR, "A", 1);

        final MetadataAttribute metaAttribute = obpgUtils.attributeToMetadata(hdfAttribute);

        assertNotNull(metaAttribute);
        assertEquals("name", metaAttribute.getName());
        assertEquals(1, metaAttribute.getNumDataElems());
        assertEquals(ProductData.TYPE_INT8, metaAttribute.getDataType());
        assertTrue(metaAttribute.getData() instanceof ProductData.ASCII);
        final Object dataElems = metaAttribute.getDataElems();
        assertTrue(dataElems instanceof byte[]);
        final byte[] chars = (byte[]) dataElems;
        assertEquals(1, chars.length);
        assertEquals('A', chars[0]);
    }

    public void test_more_chars() {
        final HdfAttribute hdfAttribute = new HdfAttribute("name", HDFConstants.DFNT_CHAR, "Skasom", 6);

        final MetadataAttribute metaAttribute = obpgUtils.attributeToMetadata(hdfAttribute);

        assertNotNull(metaAttribute);
        assertEquals("name", metaAttribute.getName());
        assertEquals(6, metaAttribute.getNumDataElems());
        assertEquals(ProductData.TYPE_INT8, metaAttribute.getDataType());
        assertTrue(metaAttribute.getData() instanceof ProductData.ASCII);
        final Object dataElems = metaAttribute.getDataElems();
        assertTrue(dataElems instanceof byte[]);
        final byte[] chars = (byte[]) dataElems;
        assertEquals(6, chars.length);
        assertEquals("Skasom", new String(chars));
    }
}
