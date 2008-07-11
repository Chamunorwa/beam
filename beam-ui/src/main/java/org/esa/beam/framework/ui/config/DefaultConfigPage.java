/*
 * $Id: DefaultConfigPage.java,v 1.1 2006/10/10 14:47:36 norman Exp $
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
package org.esa.beam.framework.ui.config;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.esa.beam.framework.param.ParamExceptionHandler;
import org.esa.beam.framework.param.ParamGroup;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.util.PropertyMap;

/**
 * A convinience implementation of the <code>ConfigPage</code> interface.
 *
 * @author Norman Fomferra
 * @author Sabine Embacher
 * @version $Revision$  $Date$
 */
public class DefaultConfigPage implements ConfigPage {

    private static int _lastKey;

    private final String _key;
    private final ParamGroup _configParams;
    private Icon _icon;
    private String _title;
    private boolean _modified;
    private Component _pageUI;
    private List _subPageList;

    public DefaultConfigPage() {
        _key = getClass().getName().concat(String.valueOf(_lastKey++));
        _configParams = new ParamGroup();
        initConfigParams(_configParams);
        initPageUI();
    }

    protected void initConfigParams(ParamGroup configParams) {
    }

    protected void initPageUI() {
    }

    public ParamGroup getConfigParams() {
        return _configParams;
    }

    public PropertyMap getConfigParamValues(PropertyMap propertyMap) {
        return getConfigParams().getParameterValues(propertyMap);
    }

    public void setConfigParamValues(PropertyMap propertyMap, ParamExceptionHandler errorHandler) {
        getConfigParams().setParameterValues(propertyMap, errorHandler);
    }

    public Parameter getConfigParam(String paramName) {
        return getConfigParams().getParameter(paramName);
    }

    public boolean isConfigParamUIEnabled(String paramName) {
        return getConfigParam(paramName).isUIEnabled();
    }

    public void setConfigParamUIEnabled(String paramName, boolean enabled) {
        getConfigParam(paramName).setUIEnabled(enabled);
    }

    public String getKey() {
        return _key;
    }

    public Icon getIcon() {
        return _icon;
    }

    public void setIcon(Icon icon) {
        _icon = icon;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public boolean isModified() {
        return _modified;
    }

    public void setModified(boolean modified) {
        _modified = modified;
    }

    public Component getPageUI() {
        return _pageUI;
    }

    public void setPageUI(Component pageUI) {
        _pageUI = pageUI;
    }

    public void addSubPage(ConfigPage subPage) {
        if (_subPageList == null) {
            _subPageList = new ArrayList();
        }
        _subPageList.add(subPage);
    }

    public void removeSubPage(ConfigPage subPage) {
        if (_subPageList == null) {
            return;
        }
        _subPageList.remove(subPage);
    }

    public ConfigPage[] getSubPages() {
        if (_subPageList == null) {
            return null;
        }
        final ConfigPage[] subPages = new ConfigPage[_subPageList.size()];
        _subPageList.toArray(subPages);
        return subPages;
    }

    public void applyPage() {
    }

    public void restorePage() {
    }

    public void onOK() {
    }

    public void updatePageUI() {
    }

    public boolean verifyUserInput() {
        return true;
    }
}
