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

import java.util.SortedSet;

import com.google.common.collect.Sets;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.Filter;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.filters.SuppressElement;
import com.puppycrawl.tools.checkstyle.filters.SuppressionFilter;

public class UnnecessarySuppressionListener implements AuditListener {
    @Override
    public void auditStarted(AuditEvent event) {
        // No need to implement this method in this class
    }

    @Override
    public void auditFinished(AuditEvent event) {
        if (event.getSource() instanceof Checker) {
            final Checker source = (Checker) event.getSource();

            for (Filter filter : ((Checker) event.getSource()).getFilters()) {
                if (filter instanceof SuppressionFilter) {
                    processSuppressionFilter(source, (SuppressionFilter) filter);
                }
            }
        }
    }

    private void processSuppressionFilter(Checker source, SuppressionFilter filter) {
        final SortedSet<LocalizedMessage> errors = Sets.newTreeSet();

        for (Filter suppressFilter : filter.getFilters()) {
            if (suppressFilter instanceof SuppressElement) {
                final SuppressElement element = (SuppressElement) suppressFilter;

                if (!element.isUsed()) {
                    errors.add(new LocalizedMessage(-1, Definitions.CHECKSTYLE_BUNDLE,
                            "suppression.unused", new String[] {
                                    element.toString(),
                            }, null, getClass(), null));
                }
            }
        }

        source.fireErrors(filter.getFile(), errors);
    }

    @Override
    public void fileStarted(AuditEvent event) {
        // No need to implement this method in this class
    }

    @Override
    public void fileFinished(AuditEvent event) {
        // No need to implement this method in this class
    }

    @Override
    public void addError(AuditEvent event) {
        // No need to implement this method in this class
    }

    @Override
    public void addException(AuditEvent event, Throwable throwable) {
        // No need to implement this method in this class
    }
}
