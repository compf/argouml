// $Id$
// Copyright (c) 1996-2003 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

// File: CrConstructorNeeded.java
// Classes: CrConstructorNeeded
// Original Author: jrobbins@ics.uci.edu

package org.argouml.uml.cognitive.critics;

import java.util.Collection;
import java.util.Iterator;

import org.argouml.cognitive.Designer;
import org.argouml.api.model.FacadeManager;
import org.argouml.model.uml.NsumlModelFacade;

/**
 * <p> A critic to detect when a class can never have instances (of itself or
 * any subclasses).</p>
 *
 * <p>The critic will trigger whenever a class has instance variables that are
 * uninitialised and there is no constructor.</p>
 *
 * @see <a href="http://argouml.tigris.org/documentation/snapshots/manual/argouml.html/#s2.ref.critics_constructor_needed">ArgoUML User Manual: Constructor Needed</a>
 */

public class CrConstructorNeeded extends CrUML {

    /**
     * <p>Constructor for the critic.</p>
     *
     * <p>Sets up the resource name, which will allow headline and description
     * to found for the current locale. Provides a design issue category
     * (STORAGE) and adds triggers for metaclasses "behaviouralFeature" and
     * "structuralFeature".</p>
     */

    public CrConstructorNeeded() {

        setResource("CrConstructorNeeded");

        addSupportedDecision(CrUML.decSTORAGE);

        // These may not actually make any difference at present (the code
        // behind addTrigger needs more work).

        addTrigger("behavioralFeature");
        addTrigger("structuralFeature");
    }

    /**
     * <p>The trigger for the critic.</p>
     *
     * <p>First see if we have any instance variables that are not
     * initialised. If not there is no problem. If there are any uninitialised
     * instance variables, then look for a constructor.</p>
     *
     * @param  dm    the {@link java.lang.Object Object} to be checked against
     *               the critic.
     *
     * @param  dsgr  the {@link org.argouml.cognitive.Designer Designer}
     *               creating the model. Not used, this is for future
     *               development of ArgoUML.
     *
     * @return       {@link #PROBLEM_FOUND PROBLEM_FOUND} if the critic is
     *               triggered, otherwise {@link #NO_PROBLEM NO_PROBLEM}.  
     */

    public boolean predicate2(Object dm, Designer dsgr) {

        // Only look at classes
        if (!(FacadeManager.getUmlFacade().isAClass(dm))) {
            return NO_PROBLEM;
        }

	// We don't consider secondary stuff.
	if (!(FacadeManager.getUmlFacade().isPrimaryObject(dm))) 
	    return NO_PROBLEM;

        // Types don't need a constructor.
        if (FacadeManager.getUmlFacade().isType(dm)) {
            return NO_PROBLEM;
        }

        // Check for uninitialised instance variables and
        // constructor.
        Collection operations = FacadeManager.getUmlFacade().getOperations(dm);
        if (operations.isEmpty()) {
            return PROBLEM_FOUND;
        }

        Iterator opers = operations.iterator();

        while (opers.hasNext()) {
            if (FacadeManager.getUmlFacade().isConstructor(opers.next())) {
                // There is a constructor.
                return NO_PROBLEM;
            }
        }

        Iterator attrs = FacadeManager.getUmlFacade().getAttributes(dm).iterator();

        while (attrs.hasNext()) {
            Object attr = attrs.next();

            if (!FacadeManager.getUmlFacade().isInstanceScope(attr))
                continue;

            if (FacadeManager.getUmlFacade().isInitialized(attr))
                continue;

            // We have found one with instance scope that is not initialized.
            return PROBLEM_FOUND;
        }

        // yeah right...we don't have an operation (and thus no 
        return NO_PROBLEM;
    }

} /* end class CrConstructorNeeded */
