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

package com.puppycrawl.tools.checkstyle.filefilters;

import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.BeforeExecutionFileFilter;
import com.puppycrawl.tools.checkstyle.api.BeforeExecutionFileFilterEvent;
import com.puppycrawl.tools.checkstyle.utils.CommonUtils;

/**
 * <p>
 * This filter accepts BeforeExecutionFileFilterEvents according to file name.
 * </p>
 * @author Richard Veach
 */
public final class BeforeExecutionExclusionFileFilter extends AutomaticBean
        implements BeforeExecutionFileFilter {

    /** Filename of exclusion. */
    private Pattern fileName;

    /**
     * Sets regular expression of the file to exclude.
     * @param fileName regular expression of the exluded file.
     */
    public void setFileName(String fileName) {
        this.fileName = CommonUtils.createPattern(fileName);
    }

    @Override
    public boolean accept(BeforeExecutionFileFilterEvent event) {
        return fileName == null || !fileName.matcher(event.getFileName()).find();
    }
}
