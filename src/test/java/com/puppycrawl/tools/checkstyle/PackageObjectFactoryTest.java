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

package com.puppycrawl.tools.checkstyle;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.checks.naming.ConstantNameCheck;
import com.puppycrawl.tools.checkstyle.internal.CheckUtil;

/**
 * Enter a description of class PackageObjectFactoryTest.java.
 * @author Rick Giles
 */
public class PackageObjectFactoryTest {

    private final PackageObjectFactory factory = new PackageObjectFactory(
            new HashSet<String>(), Thread.currentThread().getContextClassLoader());

    @Test(expected = IllegalArgumentException.class)
    public void testCtorException() {
        new PackageObjectFactory(new HashSet<String>(), null);
    }

    @Test
    public void testMakeObjectFromName()
            throws CheckstyleException {
        final Checker checker =
            (Checker) factory.createModule(
                        "com.puppycrawl.tools.checkstyle.Checker");
        assertNotNull(checker);
    }

    @Test
    public void testMakeCheckFromName()
            throws CheckstyleException {
        final ConstantNameCheck check =
                (ConstantNameCheck) factory.createModule(
                        "com.puppycrawl.tools.checkstyle.checks.naming.ConstantName");
        assertNotNull(check);
    }

    @Test
    public void testMakeCheckFromSimpleName() throws CheckstyleException {
        final ConstantNameCheck check = (ConstantNameCheck) factory.createModule("ConstantName");
        assertNotNull(check);
    }

    @Test
    public void testMakeCheckFromSimpleCheckName() throws CheckstyleException {
        final ConstantNameCheck check = (ConstantNameCheck) factory
                .createModule("ConstantNameCheck");
        assertNotNull(check);
    }

    @Test
    public void testObjectMap() throws IOException {
        final Map<String, String> map = PackageObjectFactory.getObjectMap();
        final Set<Class<?>> modules = CheckUtil.getCheckstyleModules();

        for (Class<?> module : modules) {
            final String name = module.getSimpleName();

            Assert.assertEquals("simple module name must exist in object map", module.getName(),
                    map.get(name));
            if (name.endsWith("Check")) {
                Assert.assertEquals("check name must exist in object map", module.getName(),
                        map.get(name.substring(0, name.length() - 5)));
            }
        }
    }
}
