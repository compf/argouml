// Copyright (c) 1996-2002 The Regents of the University of California. All
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

package org.argouml.model.uml.modelmanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.ModelFacade;

import ru.novosoft.uml.foundation.core.MModelElement;
import ru.novosoft.uml.foundation.core.MNamespace;
import ru.novosoft.uml.model_management.MModel;
import ru.novosoft.uml.model_management.MSubsystem;

/**
 * Helper class for UML ModelManagement Package.
 *
 * Current implementation is a placeholder.
 *
 * @since ARGO0.11.2
 * @author Thierry Lach
 * @stereotype singleton
 */
public class ModelManagementHelper {

    /** Don't allow instantiation.
     */
    private ModelManagementHelper() {
    }

    /** Singleton instance.
    */
    private static ModelManagementHelper SINGLETON =
        new ModelManagementHelper();

    /** Singleton instance access method.
     */
    public static ModelManagementHelper getHelper() {
        return SINGLETON;
    }

    /**
     * Returns all subsystems found in the projectbrowser model
     * @return Collection
     */
    public Collection getAllSubSystems() {
        MNamespace model =
            ProjectManager.getManager().getCurrentProject().getModel();
        return getAllSubSystems(model);
    }

    /**
     * Returns all subsystems found in this namespace and in its children
     * @return Collection
     */
    public Collection getAllSubSystems(MNamespace ns) {
        if (ns == null)
            return new ArrayList();
        Iterator it = ns.getOwnedElements().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof MNamespace) {
                list.addAll(getAllSubSystems((MNamespace)o));
            }
            if (o instanceof MSubsystem) {
                list.add(o);
            }

        }
        return list;
    }

    /**
     * Returns all namespaces found in the projectbrowser model
     * @return Collection
     */
    public Collection getAllNamespaces() {
        MNamespace model =
            ProjectManager.getManager().getCurrentProject().getModel();
        return getAllNamespaces(model);
    }

    /**
     * Returns all namespaces found in this namespace and in its children
     * @return Collection
     */
    public Collection getAllNamespaces(MNamespace ns) {
        if (ns == null)
            return new ArrayList();
        Iterator it = ns.getOwnedElements().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof MNamespace) {
                list.add(o);
                list.addAll(getAllNamespaces((MNamespace)o));
            }
        }
        return list;
    }

    /**
     * Returns all modelelements found in this namespace and its children
     * that are of some class kind n the projectbrowser model
     * @return Collection
     */
    public Collection getAllModelElementsOfKind(Class kind) {
        if (kind == null)
            return new ArrayList();
        Project p = ProjectManager.getManager().getCurrentProject();
        MNamespace model = p.getRoot();
        return getAllModelElementsOfKind(model, kind);
    }

    /**
     * Returns all modelelements found in this namespace and its children
     * that are of some class kind.
     * @param ns
     * @param kind
     * @return Collection
     */
    public Collection getAllModelElementsOfKind(Object nsa, Class kind) {
        if (nsa == null || kind == null)
            return new ArrayList();
        if (!ModelFacade.isANamespace(nsa))
            throw new IllegalArgumentException(
                "given argument " + nsa + " is not a namespace");
        Iterator it = ModelFacade.getOwnedElements(nsa).iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof MNamespace) {
                list.addAll(getAllModelElementsOfKind((MNamespace)o, kind));
            }
            if (kind.isAssignableFrom(o.getClass())) {
                list.add(o);
            }
        }
        return list;

    }

    /**
     * Returns all surrounding namespaces of some namespace ns. See
     * section 2.5.3.24 of the UML 1.3 spec for a definition.
     * @param ns
     * @return Collection
     */
    public Collection getAllSurroundingNamespaces(MNamespace ns) {
        Set set = new HashSet();
        set.add(ns);
        if (ns.getNamespace() != null) {
            set.addAll(getAllSurroundingNamespaces(ns.getNamespace()));
        }
        return set;
    }

    /**
     * Returns the name of a namespace.
     * @param namespace
     * @return name
     */
    public String getNamespaceName(Object o) {
        String name = null;
        if (o instanceof MNamespace)
            name = ((MNamespace)o).getName();
        return name;
    }

    /**
     * Returns the namespace of a model element or another namespace.
     * @param object
     * @return namespace
     */
    public Object getNamespace(Object o) {
        if (o instanceof MModelElement)
            return ((MModelElement)o).getNamespace();
        if (o instanceof MNamespace)
            return ((MNamespace)o).getNamespace();
        return null;
    }

    /**
     * Returns the named model element in the namespace, null otherwise.
     * @param namespace
     * @param name of the model element
     * @return model element
     */
    public Object lookupNamespaceFor(Object o, String name) {
        if (o instanceof MNamespace)
            return ((MNamespace)o).lookup(name);
        return null;
    }

    /**
     * Move a modelelement to a new namespace. The way this is currently
     * implemented this means that ALL modelelements that share the same
     * namespace as the element to be moved are moved.
     * TODO: make this into a copy function
     * TODO: make this only move/copy the asked element
     * @param element
     * @param to
     */
    public void moveElement(MModelElement element, MModel to) {
        MModel currentModel = element.getModel();
        if (currentModel != to) {
            if (element.getNamespace() != currentModel) { // handle packages
                moveElement(element.getNamespace(), to);
            } else {
                element.setNamespace(to);
            }
        }
    }

    /**
     * Returns if the object is a model.
     * @param object
     * @return boolean
     */
    public boolean isModel(Object o) {
        return (o instanceof MModel);
    }

    /**
     * Returns if the object is a model element.
     * @param object
     * @return boolean
     */
    public boolean isModelElement(Object o) {
        return (o instanceof MModelElement);
    }
}
