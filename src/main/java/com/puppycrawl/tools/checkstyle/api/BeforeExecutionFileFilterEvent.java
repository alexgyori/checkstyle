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

package com.puppycrawl.tools.checkstyle.api;

import java.util.EventObject;

/**
 * Raw event for before execution file filters.
 *
 * @author Richard Veach
 */
public final class BeforeExecutionFileFilterEvent
    extends EventObject {
    /** Record a version. */
    private static final long serialVersionUID = -233636949207963418L;
    /** Filename event associated with. **/
    private final String fileName;

    /**
     * Creates a new {@code AuditEvent} instance.
     * @param src source of the event
     * @param fileName file associated with the event
     */
    public BeforeExecutionFileFilterEvent(Object src, String fileName) {
        super(src);
        this.fileName = fileName;
    }

    /**
     * @return the file name currently being audited or null if there is
     *     no relation to a file.
     */
    public String getFileName() {
        return fileName;
    }
}
