/*******************************************************************************
 * @file   PowerlinkObject.java
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

package org.epsg.openconfigurator.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.epsg.openconfigurator.util.OpenConfiguratorProjectUtils;
import org.epsg.openconfigurator.xmlbinding.xdd.TObject;
import org.epsg.openconfigurator.xmlbinding.xdd.TObjectAccessType;
import org.epsg.openconfigurator.xmlbinding.xdd.TObjectPDOMapping;
import org.jdom2.JDOMException;

/**
 * Wrapper class for a POWERLINK object.
 *
 * @author Ramakrishnan P
 *
 */
public class PowerlinkObject extends AbstractPowerlinkObject {

    /**
     * Object model from the XDC.
     */
    private final TObject object;

    /**
     * Associated Eclipse project.
     */
    private final IProject project;

    /**
     * List of sub-objects available in the node.
     */
    private final List<PowerlinkSubobject> subObjectsList = new ArrayList<PowerlinkSubobject>();

    /**
     * TPDO mappable subobjects list.
     */
    private final List<PowerlinkSubobject> tpdoMappableObjectList = new ArrayList<PowerlinkSubobject>();

    /**
     * RPDO mappable subobjects list.
     */
    private final List<PowerlinkSubobject> rpdoMappableObjectList = new ArrayList<PowerlinkSubobject>();

    /**
     * Object ID in hex without 0x.
     */
    private final String objectIdRaw;

    /**
     * Object ID.
     */
    private final long objectIdL;

    /**
     * Object ID in hex with 0x.
     */
    private final String objectId;

    /**
     * XPath to find this object in the XDC.
     */
    private final String xpath;

    /**
     * Name of the object with ID.
     */
    private final String readableName; // Object name is not modifiable

    /**
     * Datatype in the human readable format.
     */
    private final String dataType;

    /**
     * Flag to indicate that this object is TPDO mappable or not.
     */
    private boolean isTpdoMappable = false;

    /**
     * Flag to indicate that this object is RPDO mappable or not.
     */
    private boolean isRpdoMappable = false;

    /**
     * Constructs a POWERLINK object.
     *
     * @param nodeInstance Node linked with the object.
     * @param object The Object model available in the XDC.
     */
    public PowerlinkObject(Node nodeInstance, TObject object) {
        super(nodeInstance);

        if ((nodeInstance == null) || (object == null)) {
            throw new IllegalArgumentException();
        }

        project = nodeInstance.getProject();

        this.object = object;
        objectIdRaw = DatatypeConverter.printHexBinary(this.object.getIndex());
        objectIdL = Long.parseLong(objectIdRaw, 16);
        objectId = "0x" + objectIdRaw;
        readableName = (this.object.getName() + " (" + objectId + ")");
        xpath = "//plk:Object[@index='" + objectIdRaw + "']";
        if (this.object.getDataType() != null) {
            dataType = ObjectDatatype.getDatatypeName(DatatypeConverter
                    .printHexBinary(this.object.getDataType()));
        } else {
            dataType = "";
        }

        // Calculate the subobjects available in this object.
        for (TObject.SubObject subObject : this.object.getSubObject()) {
            PowerlinkSubobject obj = new PowerlinkSubobject(nodeInstance, this,
                    subObject);
            subObjectsList.add(obj);

            if (obj.isRpdoMappable()) {
                rpdoMappableObjectList.add(obj);
            } else if (obj.isTpdoMappable()) {
                tpdoMappableObjectList.add(obj);
            }
        }

        if (((object.getPDOmapping() == TObjectPDOMapping.DEFAULT)
                || (object.getPDOmapping() == TObjectPDOMapping.OPTIONAL)
                || (object.getPDOmapping() == TObjectPDOMapping.RPDO))) {

            if (object.getUniqueIDRef() != null) {
                isRpdoMappable = true;
            } else {
                if ((object.getAccessType() == TObjectAccessType.RW)
                        || (object.getAccessType() == TObjectAccessType.WO)) {
                    isRpdoMappable = true;
                }
            }

        } else if (((object.getPDOmapping() == TObjectPDOMapping.DEFAULT)
                || (object.getPDOmapping() == TObjectPDOMapping.OPTIONAL)
                || (object.getPDOmapping() == TObjectPDOMapping.TPDO))) {

            if (object.getUniqueIDRef() != null) {
                isTpdoMappable = true;
            } else {
                if ((object.getAccessType() == TObjectAccessType.RO)
                        || (object.getAccessType() == TObjectAccessType.RW)) {
                    isTpdoMappable = true;
                }
            }

        }
    }

    /**
     * Add the force configurations to the project.
     *
     * @param force True to add and false to remove.
     * @param writeToProjectFile True to write the changes to the project file.
     * @throws IOException
     * @throws JDOMException
     */
    public synchronized void forceActualValue(boolean force,
            boolean writeToProjectFile) throws JDOMException, IOException {

        if (writeToProjectFile) {
            OpenConfiguratorProjectUtils.forceActualValue(getNode(), this, null,
                    force);
        }

        org.epsg.openconfigurator.xmlbinding.projectfile.Object forcedObj = new org.epsg.openconfigurator.xmlbinding.projectfile.Object();
        forcedObj.setIndex(getObject().getIndex());
        nodeInstance.forceObjectActualValue(forcedObj, force);
    }

    public String getActualValue() {
        return object.getActualValue();
    }

    public String getDataType() {
        return dataType;
    }

    public String getNetworkId() {
        return project.getName();
    }

    public Node getNode() {
        return nodeInstance;
    }

    public short getNodeId() {
        return nodeInstance.getNodeId();
    }

    public Object getNodeModel() {
        return nodeInstance.getNodeModel();
    }

    public TObject getObject() {
        return object;
    }

    public long getObjectId() {
        return objectIdL;
    }

    public String getObjectIdRaw() {
        return objectIdRaw;
    }

    public String getObjectIndex() {
        return objectId;
    }

    public short getObjectType() {
        return object.getObjectType();
    }

    public IProject getProject() {
        return project;
    }

    public List<PowerlinkSubobject> getRpdoMappableObjectList() {
        return rpdoMappableObjectList;
    }

    /**
     * @param subObjectId The sub-object id.
     * @return The POWERLINK sub-object based on the given sub-object ID.
     */
    public PowerlinkSubobject getSubObject(final byte[] subObjectId) {
        if (subObjectId == null) {
            return null;
        }

        String subobjectIdRaw = DatatypeConverter.printHexBinary(subObjectId);
        short subobjectIdShort = 0;
        try {
            subobjectIdShort = Short.parseShort(subobjectIdRaw, 16);
        } catch (NumberFormatException ex) {
            return null;
        }

        return getSubObject(subobjectIdShort);
    }

    /**
     * The subObject instance for the given ID, null if the subobject ID is not
     * found.
     *
     * @param subObjectId SubObject ID ranges from 0x00 to 0xFE
     * @return The subObject instance.
     */
    public PowerlinkSubobject getSubObject(short subObjectId) {
        for (PowerlinkSubobject subObj : subObjectsList) {
            if (subObj.getSubobjecId() == subObjectId) {
                return subObj;
            }
        }
        return null;
    }

    /**
     * @return The list of subobjects.
     */
    public List<PowerlinkSubobject> getSubObjects() {
        return subObjectsList;
    }

    /**
     * @return The name of the object with ID.
     */
    public String getText() {
        return readableName;
    }

    public List<PowerlinkSubobject> getTpdoMappableObjectList() {
        return tpdoMappableObjectList;
    }

    /**
     * @return The XPath to find this object in the XDC.
     */
    public String getXpath() {
        return xpath;
    }

    public boolean hasRpdoMappableSubObjects() {
        return !rpdoMappableObjectList.isEmpty();
    }

    public boolean hasTpdoMappableSubObjects() {
        return !tpdoMappableObjectList.isEmpty();
    }

    public boolean isObjectForced() {
        return nodeInstance.isObjectIdForced(object.getIndex(), null);
    }

    public boolean isRpdoMappable() {
        return isRpdoMappable;
    }

    public boolean isTpdoMappable() {
        return isTpdoMappable;
    }

    /**
     * Set the actual value to this object.
     *
     * @param actualValue The value to be set.
     * @param writeToXdc Writes the value to the XDC immediately.
     * @throws IOException
     * @throws JDOMException
     */
    public void setActualValue(final String actualValue, boolean writeToXdc)
            throws JDOMException, IOException {

        object.setActualValue(actualValue);

        if (writeToXdc) {
            OpenConfiguratorProjectUtils.updateObjectAttributeValue(getNode(),
                    getObjectIdRaw(), false, StringUtils.EMPTY, actualValue);
        }
    }
}
