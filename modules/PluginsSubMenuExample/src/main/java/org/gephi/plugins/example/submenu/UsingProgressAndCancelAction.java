/*
Copyright 2008-2011 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
 */
package org.gephi.plugins.example.submenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.gephi.utils.longtask.api.LongTaskExecutor;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

/**
 * Example of an action that executes a long task with progress management.
 * <p>
 * The action uses {@link LongTaskExecutor} tu execute a {@link LongTask} process.
 * 
 * @author Mathieu Bastian
 */
@ActionID(category = "File",
id = "org.gephi.desktop.filters.UsingProgressAndCancelAction")
@ActionRegistration(displayName = "#CTL_UsingProgressAndCancelAction")
@ActionReferences({
    @ActionReference(path = "Menu/Plugins", position = 7000)
})
@Messages("CTL_UsingProgressAndCancelAction=Test progress and cancel")
public final class UsingProgressAndCancelAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        LongTaskExecutor executor = new LongTaskExecutor(true);
        LongTaskExample longTaskExample = new LongTaskExample();
        executor.execute(longTaskExample, longTaskExample, "Task...", null);
    }

    private static class LongTaskExample implements LongTask, Runnable {

        private ProgressTicket progressTicket;
        private boolean cancelled;

        @Override
        public void run() {
            int waitSeconds = 5;
            Progress.start(progressTicket, waitSeconds);
            for (int i = 0; i < waitSeconds && !cancelled; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
                Progress.progress(progressTicket);
            }
            Progress.finish(progressTicket);
        }

        @Override
        public boolean cancel() {
            cancelled = true;
            return true;
        }

        @Override
        public void setProgressTicket(ProgressTicket pt) {
            this.progressTicket = pt;
        }
    }
}