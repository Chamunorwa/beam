/*
 * $Id: $
 *
 * Copyright (C) 2008 by Brockmann Consult (info@brockmann-consult.de)
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
package org.esa.beam.dataio.netcdf;

import java.util.HashMap;
import java.util.Map;

import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.StringUtils;

import ucar.ma2.DataType;

class NetcdfDataTypeWorkarounds {
    
    private static NetcdfDataTypeWorkarounds INSTANCE = new NetcdfDataTypeWorkarounds();
    private Map<NameTypePair, Integer> workaroundMap;
    
    private NetcdfDataTypeWorkarounds() {
        workaroundMap = new HashMap<NameTypePair, Integer>();
        workaroundMap.put(new NameTypePair("fapar", DataType.BYTE), ProductData.TYPE_UINT8);
        workaroundMap.put(new NameTypePair("sd_spatial_fapar", DataType.BYTE), ProductData.TYPE_UINT8);
        workaroundMap.put(new NameTypePair("nb_spatial_fapar", DataType.SHORT), ProductData.TYPE_UINT16);
    }
    
    public static NetcdfDataTypeWorkarounds getInstance() {
        return INSTANCE;
    }
    
    public boolean hasWorkaroud(String variableName, DataType dataType) {
        if (StringUtils.isNullOrEmpty(variableName) || dataType == null) {
            return false;
        }
        NameTypePair nameTypePair = new NameTypePair(variableName, dataType);
        return workaroundMap.containsKey(nameTypePair);
    }
    
    public int getRasterDataType(String variableName, DataType dataType) {
        if (StringUtils.isNullOrEmpty(variableName) || dataType == null) {
            throw new IllegalArgumentException();
        }
        NameTypePair nameTypePair = new NameTypePair(variableName, dataType);
        return workaroundMap.get(nameTypePair);
    }
    
    private static class NameTypePair {
        final String variableName;
        final DataType dataType;

        public NameTypePair(String variableName, DataType dataType) {
            this.variableName = variableName;
            this.dataType = dataType;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result+ dataType.hashCode();
            result = prime * result+ variableName.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            final NameTypePair other = (NameTypePair) obj;
            if (dataType.equals(other.dataType)
                    && variableName.equals(other.variableName)) {
                return true;
            }
            return false;
        }
    }
}
