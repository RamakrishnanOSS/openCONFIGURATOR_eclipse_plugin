/*******************************************************************************
 * @file   JDomUtil.java
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

package org.epsg.openconfigurator.xmloperation;

import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

/**
 * Util class for JDOM2 operations.
 *
 * @author Ramakrishnan P
 *
 */
public class JDomUtil {

    /**
     * Adds the new element under the xpath specified available in the document.
     *
     * @param doc The file instance.
     * @param xpath The parent path.
     * @param namespace Namespace of the element.
     * @param newElement The new element to be added.
     */
    public static void addNewElement(Document doc, String xpath,
            Namespace namespace, Element newElement) {
        addNewElement(doc, getXPathExpressionElement(xpath, namespace),
                newElement);
    }

    /**
     * Adds the new element under the xpath specified available in the document.
     *
     * @param doc The file instance.
     * @param xpath The parent path.
     * @param newElement The new element to be added.
     */
    public static void addNewElement(Document doc,
            XPathExpression<Element> xpath, Element newElement) {

        List<Element> elementsList = xpath.evaluate(doc);
        System.out.println("Size" + elementsList.size());
        for (Element element : elementsList) {
            newElement.setNamespace(element.getNamespace());
            element.addContent(newElement);
        }
    }

    /**
     * Adds the new element under the xpath specified in a specified position
     * available in the document.
     *
     * @param doc The file instance.
     * @param xpath The parent path.
     * @param newElement The new element to be added.
     * @param position The position of the new element.
     */
    public static void addNewElement(Document doc,
            XPathExpression<Element> xpath, Element newElement, int position) {

        if (position < 1) {
            System.err
                    .print("Error invalid position:" + position + " minimum 1");
            return;
        }

        List<Element> elementsList = xpath.evaluate(doc);
        System.out.println("Size" + elementsList.size());
        if (elementsList.size() <= position) {
            Element parentElement = elementsList.get(0);
            newElement.setNamespace(parentElement.getNamespace());
            parentElement.addContent(position, newElement);
        } else {
            System.err.print(
                    "Error Xpath does not evaluate elements in the list");
            return;
        }
    }

    public static XPathExpression<Element> getXPathExpressionElement(
            String xpathValue, Namespace namespace) {
        XPathBuilder<Element> elementBuilder = new XPathBuilder<Element>(
                xpathValue, Filters.element());
        elementBuilder.setNamespace(namespace);
        return elementBuilder.compileWith(XPathFactory.instance());
    }

    /**
     * Removes the attribute from the document.
     *
     * @param document The file instance.
     * @param xpath The parent path.
     * @param namespace The namespace of the attribute.
     * @param attributeName The attribute name to be removed.
     */
    public static void removeAttribute(Document document, String xpath,
            Namespace namespace, String attributeName) {
        JDomUtil.removeAttribute(document,
                getXPathExpressionElement(xpath, namespace), attributeName);
    }

    /**
     * Removes the attribute from the document.
     *
     * @param document The file instance.
     * @param xpathExpr The parent path.
     * @param attributeName The attribute name to be removed.
     */
    public static void removeAttribute(Document document,
            XPathExpression<Element> xpathExpr, String attributeName) {
        Element emt = xpathExpr.evaluateFirst(document);
        if (emt != null) {
            emt.removeAttribute(attributeName);
        } else {
            System.err.println(xpathExpr.getExpression() + "Element null");
        }
    }

    /**
     * Removes the specified Xpath element.
     *
     * @param doc The file instance.
     * @param xpath The path of the element to be removed.
     * @param namespace The namespace of the element to be removed.
     */
    public static void removeElement(Document doc, String xpath,
            Namespace namespace) {
        removeElement(doc, getXPathExpressionElement(xpath, namespace));
    }

    /**
     * Removes the specified Xpath element.
     *
     * @param doc The file instance.
     * @param xpath The path of the element to be removed.
     */
    public static void removeElement(Document doc,
            XPathExpression<Element> xpath) {
        List<Element> elementsList = xpath.evaluate(doc);
        System.out.println("xpath:" + xpath.getExpression() + " Size"
                + elementsList.size());
        for (Element element : elementsList) {
            element.detach();
        }
    }

    /**
     * Add/update attribute for the specified Xpath element.
     *
     * @param doc The file instance.
     * @param xpathValue The path of the element to be updated.
     * @param namespace The namespace of the new attribute.
     * @param newAttribute The new attribute to be added or updated.
     */
    public static void setAttribute(Document doc, String xpathValue,
            Namespace namespace, Attribute newAttribute) {
        setAttribute(doc, getXPathExpressionElement(xpathValue, namespace),
                newAttribute);
    }

    /**
     * Add/update attribute for the specified Xpath element.
     *
     * @param doc The file instance.
     * @param xpath The path of the element to be updated.
     * @param newAttribute The new attribute to be added or updated.
     */
    public static void setAttribute(Document doc,
            XPathExpression<Element> xpath, Attribute newAttribute) {

        List<Element> elementsList = xpath.evaluate(doc);
        for (Element emt : elementsList) {
            emt.setAttribute(newAttribute);
        }
    }

    /**
     * Update attribute value in the given Xpath element.
     *
     * @param doc The file instance.
     * @param xpath The path of the element to be updated.
     * @param namespace The namespace of the new attribute.
     * @param newAttribute The new attribute to be added or updated.
     */
    public static void updateAttribute(Document doc, String xpath,
            Namespace namespace, Attribute newAttribute) {
        XPathExpression<Element> expr = getXPathExpressionElement(xpath,
                namespace);
        updateAttribute(doc, expr, newAttribute);
    }

    /**
     * Update attribute value in the given Xpath element.
     *
     * @param document The file instance.
     * @param xpathExpr The path of the element to be updated.
     * @param newAttribute The new attribute to be added or updated.
     */
    public static void updateAttribute(Document document,
            XPathExpression<Element> xpathExpr, Attribute newAttribute) {

        Element emt = xpathExpr.evaluateFirst(document);
        if (emt != null) {
            emt.getAttributes().add(newAttribute);
        } else {
            System.err.println(xpathExpr.getExpression() + "Element null");
        }
    }
}