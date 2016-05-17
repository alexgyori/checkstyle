////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.internal;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.io.Files;
import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.JavadocUtils;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

public class XDocsJavaDocsTest extends BaseCheckTestSupport {
    private static DefaultConfiguration checkConfig;
    private static Checker checker;

    private static String checkName;
    private static List<List<Node>> checkProperties = new ArrayList<>();
    private static Map<String, String> checkPropertyDoc = new HashMap<>();
    private static Map<String, String> checkText = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        checkConfig = new DefaultConfiguration(JavaDocCapture.class.getName());
        checker = createChecker(checkConfig);
    }

    @Test
    public void testAllCheckSectionJavaDocs() throws Throwable {
        final ModuleFactory moduleFactory = TestUtils.getPackageObjectFactory();

        for (Path path : XDocUtil.getXdocsConfigFilePaths(XDocUtil.getXdocsFilePaths())) {
            final File file = path.toFile();
            final String fileName = file.getName();

            if ("config_reporting.xml".equals(fileName)) {
                continue;
            }

            final String input = Files.toString(file, UTF_8);
            final Document document = XmlUtil.getRawXml(fileName, input, input);
            final NodeList sources = document.getElementsByTagName("section");

            for (int position = 0; position < sources.getLength(); position++) {
                final Node section = sources.item(position);
                final String sectionName = section.getAttributes().getNamedItem("name")
                        .getNodeValue();

                if ("Content".equals(sectionName) || "Overview".equals(sectionName)) {
                    continue;
                }
                if (!"AbbreviationAsWordInName".equals(sectionName)) {
                    continue;
                }

                examineCheckSection(moduleFactory, fileName, sectionName, section);
            }
        }
    }

    private static void examineCheckSection(ModuleFactory moduleFactory, String fileName,
            String sectionName, Node section) throws Throwable {
        Object instance = null;

        try {
            instance = moduleFactory.createModule(sectionName);
        }
        catch (CheckstyleException ex) {
            Assert.fail(fileName + " couldn't find class: " + sectionName);
        }

        checkText.clear();
        checkProperties.clear();
        checkPropertyDoc.clear();
        checkName = sectionName;

        for (Node subSection : XmlUtil.getChildrenElements(section)) {
            final String subSectionName = subSection.getAttributes().getNamedItem("name")
                    .getNodeValue();

            switch (subSectionName) {
                case "Description":
                case "Examples":
                    checkText.put(subSectionName, getNodeText(subSection).replace("\r", ""));
                    break;
                case "Properties":
                    populateProperties(subSection);
                    checkText.put(subSectionName, createPropertiesText());
                    break;
                case "Example of Usage":
                    break;
                case "Error Messages":
                    break;
                case "Package":
                    break;
                case "Parent Module":
                    break;
                case "Notes":
                    break;
                case "Rule Description":
                    break;
                default:
                    System.out.println(subSectionName);
                    break;
            }
        }

        final List<File> files = new ArrayList<>();
        files.add(new File(
                "src/main/java/" + instance.getClass().getName().replace(".", "/") + ".java"));

        try {
            checker.process(files);
        }
        catch (Error error) {
            throw error.getCause();
        }
    }

    private static void populateProperties(Node subSection) {
        boolean skip = true;

        for (Node row : XmlUtil.getChildrenElements(XmlUtil.getFirstChildElement(subSection))) {
            if (skip) {
                skip = false;
                continue;
            }

            checkProperties.add(new ArrayList<>(XmlUtil.getChildrenElements(row)));
        }
    }

    private static String createPropertiesText() {
        final StringBuffer result = new StringBuffer();

        for (List<Node> property : checkProperties) {
            final String propertyName = getNodeText(property.get(0));

            result.append("\n<p>\nOption {@code ");
            result.append(propertyName);
            result.append("} - ");

            final String temp = getNodeText(property.get(1));

            result.append(temp);
            checkPropertyDoc.put(propertyName, temp);

            if (propertyName.endsWith("token") || propertyName.endsWith("tokens")) {
                result.append(" Default value is: ");
                result.append(getNodeText(property.get(3)));
                result.append("\n</p>");
            }
            else {
                result.append(" Default value is {@code ");
                result.append(getNodeText(property.get(3)));
                result.append("}.\n</p>");
            }
        }

        return result.toString();
    }

    private static String getNodeText(Node node) {
        final StringBuffer result = new StringBuffer();

        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.TEXT_NODE) {
                for (String temp : child.getTextContent().split("\n")) {
                    final String text = temp.trim();

                    if (text.length() != 0) {
                        if (shouldAppendSpace(result)) {
                            result.append(' ');
                        }

                        result.append(text);
                    }
                }
            }
            else {
                final String name = transformXmlToJavaDocName(child.getNodeName());
                final boolean newLine = "p".equals(name) || "pre".equals(name);
                final boolean sanitize = "pre".equals(name);
                final boolean changeToTag = "code".equals(name);

                if (newLine) {
                    result.append(System.lineSeparator());
                }
                else if (shouldAppendSpace(result)) {
                    result.append(' ');
                }

                if (changeToTag) {
                    result.append("{@");
                    result.append(name);
                    result.append(' ');
                }
                else {
                    result.append('<');
                    result.append(name);
                    result.append(getAttributeText(child.getAttributes()));
                    result.append('>');
                }

                if (newLine) {
                    result.append(System.lineSeparator());
                }

                if (sanitize) {
                    result.append(sanitizeXml(child.getTextContent()));
                }
                else {
                    result.append(getNodeText(child));
                }

                if (newLine) {
                    result.append(System.lineSeparator());
                }

                if (changeToTag) {
                    result.append('}');
                }
                else {
                    result.append("</");
                    result.append(name);
                    result.append('>');
                }
            }
        }

        return result.toString();
    }

    private static boolean shouldAppendSpace(StringBuffer text) {
        if (text.length() == 0) {
            return false;
        }

        final char last = text.charAt(text.length() - 1);

        return !Character.isWhitespace(last);
    }

    private static String transformXmlToJavaDocName(String name) {
        final String result;

        if ("source".equals(name)) {
            result = "pre";
        }
        else {
            result = name;
        }

        return result;
    }

    private static String getAttributeText(NamedNodeMap attributes) {
        final StringBuffer result = new StringBuffer();

        for (int i = 0; i < attributes.getLength(); i++) {
            result.append(' ');

            final Node attribute = attributes.item(i);

            result.append(attribute.getNodeName());
            result.append("=\"");
            result.append(attribute.getNodeValue());
            result.append('"');
        }

        return result.toString();
    }

    private static String sanitizeXml(String nodeValue) {
        return nodeValue.replaceAll("^[\\r\\n\\s]+", "").replaceAll("[\\r\\n\\s]+$", "")
                .replace("<", "&lt;").replace(">", "&gt;");
    }

    private static class JavaDocCapture extends AbstractCheck {
        private int depth;

        @Override
        public boolean isCommentNodesRequired() {
            return true;
        }

        @Override
        public int[] getDefaultTokens() {
            return new int[] {
                TokenTypes.BLOCK_COMMENT_BEGIN,
            };
        }

        @Override
        public void beginTree(DetailAST rootAST) {
            depth = 0;
        }

        @Override
        public void visitToken(DetailAST ast) {
            if (JavadocUtils.isJavadocComment(ast)) {
                final DetailAST node = getParent(ast);

                switch (node.getType()) {
                    case TokenTypes.CLASS_DEF:
                        if (depth == 0) {
                            Assert.assertEquals(checkName + "'s class-level JavaDoc",
                                    checkText.get("Description") + checkText.get("Properties")
                                            + checkText.get("Examples"),
                                    getJavaDocText(ast));
                        }

                        depth++;
                        break;
                    case TokenTypes.METHOD_DEF:
//                        if (depth == 0 && CheckUtils.isSetterMethod(node)) {
//                            final String propertyUpper = node.findFirstToken(TokenTypes.IDENT).getText().substring(3);
//                            final String propertyName = Character.toLowerCase(propertyUpper.charAt(0)) + propertyUpper.substring(1);
//                            final String propertyDoc = checkPropertyDoc.get(propertyName); 
//
//                            if (propertyDoc != null) {
//                                final String javaDoc = getJavaDocText(ast);
//
//                                Assert.assertEquals(checkName
//                                    + "'s class method-level JavaDoc for " + propertyName,
//                                    propertyDoc, javaDoc.substring(0, javaDoc.indexOf(" @param")));
//                            }
//                        }
                        break;
                    case TokenTypes.CTOR_DEF:
                        if (depth == 0) {

                        }
                        break;
                    case TokenTypes.VARIABLE_DEF:
                        final String propertyName = node.findFirstToken(TokenTypes.IDENT).getText();
                        final String propertyDoc = checkPropertyDoc.get(propertyName); 

                        if (propertyDoc != null) {
                            final String javaDoc = getJavaDocText(ast);

                            Assert.assertEquals(checkName + "'s class field-level JavaDoc for "
                                    + propertyName, "<b>Check Property</b> : " + propertyDoc, javaDoc);
                        }
                        break;
                    case TokenTypes.ENUM_DEF:
                    case TokenTypes.ENUM_CONSTANT_DEF:
                        // ignore
                        break;
                    default:
                        System.out.println(
                                TokenUtils.getTokenName(node.getType()) + ":" + ast.getLineNo());
                        break;
                }
            }
        }

        @Override
        public void leaveToken(DetailAST ast) {
            final DetailAST node = getParent(ast);

            if (node.getType() == TokenTypes.CLASS_DEF && JavadocUtils.isJavadocComment(ast)) {
                depth--;
            }
        }

        private static DetailAST getParent(DetailAST node) {
            DetailAST result = node.getParent();
            int type = result.getType();

            while (type == TokenTypes.MODIFIERS || type == TokenTypes.ANNOTATION) {
                result = result.getParent();
                type = result.getType();
            }

            return result;
        }

        public static String getJavaDocText(DetailAST node) {
            final String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<document>\n"
                    + node.getFirstChild().getText().replaceAll("(^|\\r?\\n)\\s*\\* ?", "\n").trim()
                            .replaceAll("@author.*", "")
                    + "\n</document>";

            try {
                return getNodeText(XmlUtil.getRawXml(checkName, text, text).getFirstChild())
                        .replace("\r", "");
            }
            catch (ParserConfigurationException ex) {
                ex.printStackTrace();
                Assert.fail("Exception: " + ex.getClass() + " - " + ex.getMessage());
                return null;
            }
        }
    }
}
