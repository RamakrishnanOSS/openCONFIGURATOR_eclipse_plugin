/*******************************************************************************
 * @file   ObjectPropertySource.java
 *
 * @author Ramakrishnan Periyakaruppan, Kalycito Infotech Private Limited.
 *
 * @copyright (c) 2015, Kalycito Infotech Private Limited
 *                    All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the copyright holders nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.epsg.openconfigurator.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.epsg.openconfigurator.lib.wrapper.OpenConfiguratorCore;
import org.epsg.openconfigurator.lib.wrapper.Result;
import org.epsg.openconfigurator.model.PowerlinkObject;
import org.epsg.openconfigurator.util.OpenConfiguratorLibraryUtils;
import org.epsg.openconfigurator.xmlbinding.xdd.TObject;
import org.epsg.openconfigurator.xmlbinding.xdd.TObjectAccessType;

/**
 * Describes the properties for a POWERLINK object.
 *
 * @see setObjectData
 * @author Ramakrishnan P
 *
 */
public class ObjectPropertySource extends AbstractObjectPropertySource
        implements IPropertySource {

    private TObject object;
    private PowerlinkObject plkObject;

    public ObjectPropertySource(final PowerlinkObject plkObject) {
        setObjectData(plkObject);

        objectIdDescriptor.setCategory(IPropertySourceSupport.BASIC_CATEGORY);

        nameDescriptor.setCategory(IPropertySourceSupport.BASIC_CATEGORY);
        objectTypeDescriptor
                .setCategory(IPropertySourceSupport.ADVANCED_CATEGORY);
        objectTypeDescriptor
                .setFilterFlags(IPropertySourceSupport.EXPERT_FILTER_FLAG);

        dataTypeDescriptor.setCategory(IPropertySourceSupport.BASIC_CATEGORY);

        lowLimitDescriptor
                .setCategory(IPropertySourceSupport.ADVANCED_CATEGORY);
        lowLimitDescriptor
                .setFilterFlags(IPropertySourceSupport.EXPERT_FILTER_FLAG);

        highLimitDescriptor
                .setCategory(IPropertySourceSupport.ADVANCED_CATEGORY);
        highLimitDescriptor
                .setFilterFlags(IPropertySourceSupport.EXPERT_FILTER_FLAG);

        accessTypeDescriptor.setCategory(IPropertySourceSupport.BASIC_CATEGORY);

        defaultValueDescriptor
                .setCategory(IPropertySourceSupport.BASIC_CATEGORY);
        actualValueReadOnlyDescriptor
                .setCategory(IPropertySourceSupport.BASIC_CATEGORY);
        actualValueEditableDescriptor
                .setCategory(IPropertySourceSupport.BASIC_CATEGORY);
        actualValueEditableDescriptor.setValidator(new ICellEditorValidator() {

            @Override
            public String isValid(Object value) {

                return handleActualValue(value);
            }
        });

        denotationDescriptor
                .setCategory(IPropertySourceSupport.ADVANCED_CATEGORY);
        denotationDescriptor
                .setFilterFlags(IPropertySourceSupport.EXPERT_FILTER_FLAG);

        pdoMappingDescriptor.setCategory(IPropertySourceSupport.BASIC_CATEGORY);
        objFlagsDescriptor
                .setCategory(IPropertySourceSupport.ADVANCED_CATEGORY);
        objFlagsDescriptor
                .setFilterFlags(IPropertySourceSupport.EXPERT_FILTER_FLAG);

        uniqueIDRefDescriptor
                .setCategory(IPropertySourceSupport.ADVANCED_CATEGORY);
        uniqueIDRefDescriptor
                .setFilterFlags(IPropertySourceSupport.EXPERT_FILTER_FLAG);
    }

    private void addObjectPropertyDescriptors(
            List<IPropertyDescriptor> propertyList) {

        propertyList.add(objectIdDescriptor);
        propertyList.add(nameDescriptor);
        propertyList.add(objectTypeDescriptor);

        if (object.getDataType() != null) {
            propertyList.add(dataTypeDescriptor);
        }
        if (object.getLowLimit() != null) {
            propertyList.add(lowLimitDescriptor);
        }
        if (object.getHighLimit() != null) {
            propertyList.add(highLimitDescriptor);
        }
        if (object.getAccessType() != null) {
            propertyList.add(accessTypeDescriptor);
        }

        if (object.getDefaultValue() != null) {
            propertyList.add(defaultValueDescriptor);
        }

        if (isActualValueEditable()) {
            propertyList.add(actualValueEditableDescriptor);
        } else {
            if (object.getActualValue() != null) {
                propertyList.add(actualValueReadOnlyDescriptor);
            }
        }

        if (object.getDenotation() != null) {
            propertyList.add(denotationDescriptor);
        }

        if (object.getPDOmapping() != null) {
            propertyList.add(pdoMappingDescriptor);
        }
        if (object.getObjFlags() != null) {
            propertyList.add(objFlagsDescriptor);
        }
        if (object.getUniqueIDRef() != null) {
            propertyList.add(uniqueIDRefDescriptor);
        }
    }

    @Override
    public Object getEditableValue() {
        return object;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        List<IPropertyDescriptor> propertyList = new ArrayList<IPropertyDescriptor>();
        addObjectPropertyDescriptors(propertyList);

        IPropertyDescriptor[] propertyDescriptorArray = {};
        propertyDescriptorArray = propertyList.toArray(propertyDescriptorArray);
        return propertyDescriptorArray;
    }

    @Override
    public Object getPropertyValue(Object id) {
        Object retObj = null;
        if (id instanceof String) {
            String objectId = (String) id;
            switch (objectId) {
                case OBJ_INDEX_ID:
                    retObj = plkObject.getObjectIndex();
                    break;
                case OBJ_NAME_ID:
                    retObj = object.getName();
                    break;
                case OBJ_TYPE_ID:
                    retObj = String.valueOf(object.getObjectType());
                    break;
                case OBJ_DATATYPE_ID:
                    retObj = plkObject.getDataType();
                    break;
                case OBJ_LOW_LIMIT_ID:
                    retObj = object.getLowLimit();
                    break;
                case OBJ_HIGH_LIMIT_ID:
                    retObj = object.getHighLimit();
                    break;
                case OBJ_ACCESS_TYPE_ID:
                    retObj = object.getAccessType().value();
                    break;
                case OBJ_DEFAULT_VALUE_ID:
                    retObj = object.getDefaultValue();
                    break;
                case OBJ_ACTUAL_VALUE_READ_ONLY_ID:
                case OBJ_ACTUAL_VALUE_EDITABLE_ID: //$FALL-THROUGH$
                    if (object.getActualValue() != null) {
                        retObj = object.getActualValue();
                    } else {
                        retObj = new String();
                    }
                    break;
                case OBJ_DENOTATION_ID:
                    retObj = object.getDenotation();
                    break;
                case OBJ_PDO_MAPPING_ID:
                    retObj = object.getPDOmapping().value();
                    break;
                case OBJ_OBJFLAGS_ID:
                    retObj = object.getObjFlags();
                    break;
                case OBJ_UNIQUEIDREF_ID:
                    retObj = object.getUniqueIDRef();
                    break;
                default:
                    break;
            }
        }
        return retObj;
    }

    /**
     * Handles the actual value modifications.
     *
     * @param value The value to be set.
     * @return Returns a string indicating whether the given value is valid;
     *         null means valid, and non-null means invalid, with the result
     *         being the error message to display to the end user.
     */
    protected String handleActualValue(Object value) {
        String actualVal = (String) value;
        Result res = OpenConfiguratorCore.GetInstance().SetObjectActualValue(
                plkObject.getNetworkId(), plkObject.getNodeId(),
                plkObject.getObjectId(), actualVal);
        if (!res.IsSuccessful()) {
            return OpenConfiguratorLibraryUtils.getErrorMessage(res);
        }
        return null;
    }

    private boolean isActualValueEditable() {
        boolean retVal = false;
        // Only VAR type is allowed to be edited.
        if ((object.getObjectType() != 7)) {
            return retVal;
        }

        if (object.getDataType() == null) {
            return retVal;
        }

        if (object.getAccessType() == null) {
            return retVal;
        }
        String accessType = object.getAccessType().value();
        // Only RW and WO types are allowed to be edited.
        if (!(accessType.equalsIgnoreCase(TObjectAccessType.WO.value())
                || accessType.equalsIgnoreCase(TObjectAccessType.RW.value()))) {
            return retVal;
        }

        retVal = true;
        return retVal;
    }

    @Override
    public boolean isPropertySet(Object id) {
        return false;
    }

    @Override
    public void resetPropertyValue(Object id) {
    }

    public void setObjectData(PowerlinkObject adaptableObject) {
        plkObject = adaptableObject;
        object = plkObject.getObject();
    }

    @Override
    public void setPropertyValue(Object id, Object value) {
        if (id instanceof String) {
            String objectId = (String) id;
            switch (objectId) {
                case OBJ_ACTUAL_VALUE_EDITABLE_ID:
                    plkObject.setActualValue((String) value, true);
                    break;
                default:
                    // others are not editable.
            }
        }
    }
}