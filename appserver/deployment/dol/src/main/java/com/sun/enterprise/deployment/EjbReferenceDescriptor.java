/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.enterprise.deployment;

import com.sun.enterprise.deployment.types.EjbReference;
import com.sun.enterprise.deployment.util.DOLUtils;

import org.glassfish.api.naming.SimpleJndiName;

/**
 * An object representing a link to another ejb.
 *
 * @author Jerome Dochez
 */
public final class EjbReferenceDescriptor extends EnvironmentProperty implements EjbReference, NamedDescriptor {

    private static final long serialVersionUID = 1L;
    private static final int NULL_HASH_CODE = Integer.valueOf(1).hashCode();

    // In case the reference has been resolved, the ejbDescriptor will
    // be the referenced ejb.
    private EjbDescriptor ejbDescriptor;

    // We need the referring bundle for what ?
    private BundleDescriptor referringBundle;

    // bean type and interfaces names
    private String refType;
    private String refHomeIntf;
    private String refIntf;

    // local-ref or remote-ref
    private boolean isLocal;

    /**
     * holds the ejb-link value associated to this ejb reference before the
     * ejbs were resolved
     */
    private String ejbLink;

    /**
     * Portable lookup name that resolves to this reference's target EJB.
     * NOTE : This value is handled *independently* of the prior V2
     * mappedName/jndi-name logic. It is not eligible as a possible return
     * value from getJndiName() or getValue().  Ejb dependency processing
     * is complicated due to the large number of ways a reference can be
     * resolved, so this is the safest approach to avoiding backward
     * compatibility issues.
     */
    private SimpleJndiName lookupName;

    /**
     * constructs an local or remote ejb reference to the given ejb descriptor,
     * the description and the name of the reference
     *
     * @param name is the name of the reference
     * @param description is a human readable description of the reference
     * @param ejbDescriptor the referenced EJB
     * @param isLocal true if the reference uses the local interfaces
     */
    public EjbReferenceDescriptor(String name, String description, EjbDescriptor ejbDescriptor, boolean isLocal) {
        super(name, "", description);
        this.isLocal = isLocal;
        this.setEjbDescriptor(ejbDescriptor);
    }

    /**
    * Constructs a reference in the exterrnal state.
    */
    public EjbReferenceDescriptor() {
    }

    @Override
    public void setReferringBundleDescriptor(BundleDescriptor referringBundle) {
        this.referringBundle = referringBundle;
    }

    /**
     * Get the referring bundle, i.e. the bundle within which this
     * EJB reference is declared.
     */
    @Override
    public BundleDescriptor getReferringBundleDescriptor() {
        return referringBundle;
    }

    /**
     * Sets the ejb descriptor to which I refer.
     * @param ejbDescriptor the ejb descriptor referenced, null if it is unknown at this time
     */
    @Override
    public void setEjbDescriptor(EjbDescriptor ejbDescriptor) {
        if (this.ejbDescriptor != null) {
            this.ejbDescriptor.removeEjbReferencer(this); // remove previous referencer
        }
        this.ejbDescriptor = ejbDescriptor;
        if (ejbDescriptor != null) {
            ejbDescriptor.addEjbReferencer(this);
            if (isLocal()) {
                if (!ejbDescriptor.isLocalInterfacesSupported() && !ejbDescriptor.isLocalBusinessInterfacesSupported()
                    && !ejbDescriptor.isLocalBean()) {
                    throw new RuntimeException(localStrings.getLocalString(
                        "entreprise.deployment.invalidLocalInterfaceReference",
                        "Trying to set an ejb-local-ref on an EJB while the EJB [{0}] does not define local interfaces",
                        new Object[] {ejbDescriptor.getName()}));
                }
            } else {
                if (!ejbDescriptor.isRemoteInterfacesSupported()
                    && !ejbDescriptor.isRemoteBusinessInterfacesSupported()) {
                    throw new RuntimeException(
                        localStrings.getLocalString("entreprise.deployment.invalidRemoteInterfaceReference",
                            "Trying to set an ejb-ref on an EJB, while the EJB [{0}] does not define remote interfaces",
                            new Object[] {ejbDescriptor.getName()}));
                }
            }
        }
    }

    /**
     * @return true if I know the name of the ejb to which I refer.
     */
    public boolean isLinked() {
        return ejbLink != null;
    }

    /**
     * @return the name of the ejb to which I refer
     */
    @Override
    public String getLinkName() {
        if (ejbDescriptor == null) {
            return ejbLink;
        }
        if (ejbLink != null && !ejbLink.isEmpty()) {
            return ejbLink;
        }
        return ejbDescriptor.getName();
    }

    /**
     * Sets the name of the ejb to which I refer.
     */
    @Override
    public void setLinkName(String linkName) {
        ejbLink = linkName;
    }

    /**
     * return the jndi name of the bean to which I refer.
     */
    @Override
    public SimpleJndiName getJndiName() {
        String jndiName = this.getValue();
        if (isLocal() || (jndiName != null && !jndiName.isEmpty())) {
            // mapped-name has no meaning for the local ejb view. ejb-link
            // should be used to resolve any ambiguities about the target
            // local ejb.
            return SimpleJndiName.of(jndiName);
        }
        return getMappedName();
    }

    /**
     * Sets the jndi name of the bean type which I am referring.
     */
    @Override
    public void setJndiName(SimpleJndiName jndiName) {
        this.setValue(jndiName == null ? null : jndiName.toString());
    }

    @Override
    public boolean hasJndiName() {
        SimpleJndiName name = getJndiName();
        return name != null && !name.isEmpty();
    }

    /**
     * Return the jndi name of the bean to which I refer.
     */
    @Override
    public String getValue() {
        if (ejbDescriptor == null) {
            return super.getValue();
        }
        if (isLocal()) {
            return super.getValue();
        }
        SimpleJndiName jndiName = ejbDescriptor.getJndiName();
        return jndiName == null ? null : jndiName.toString();
    }

    @Override
    public void setLookupName(SimpleJndiName l) {
        lookupName = l;
    }

    @Override
    public SimpleJndiName getLookupName() {
        // FIXME: no empty string
        return lookupName == null ? new SimpleJndiName("") : lookupName;
    }

    @Override
    public boolean hasLookupName() {
        return lookupName != null && !lookupName.isEmpty();
    }


    /**
     * return the ejb to whoch I refer.
     */
    @Override
    public EjbDescriptor getEjbDescriptor() {
        return ejbDescriptor;
    }

    /**
     * @return true if the EJB reference uses the local interfaces of the EJB
     */
    @Override
    public boolean isLocal() {
        return isLocal;
    }

    /**
     * Set whether this EJB Reference uses local interfaces or remote
     * @param local true if the EJB reference use local interfaces
     */
    @Override
    public void setLocal(boolean local) {
        this.isLocal = local;
    }

    /**
    * Retusn the type of the ejb to whioch I refer.
    */

    @Override
    public String getType() {
        if (ejbDescriptor == null) {
            return refType;
        }
        return ejbDescriptor.getType();
    }


    /**
     * Assigns the type of the ejb to whcoih I refer.
     */
    @Override
    public void setType(String type) {
        refType = type;
    }


    @Override
    public String getInjectResourceType() {
        return isEJB30ClientView() ? getEjbInterface() : getEjbHomeInterface();
    }


    @Override
    public void setInjectResourceType(String resourceType) {
        if (isEJB30ClientView()) {
            setEjbInterface(resourceType);
        } else {
            setEjbHomeInterface(resourceType);
        }
    }

    /**
      * @return the home classname of the referee EJB.
      */
    public String getHomeClassName() {
        return refHomeIntf;
    }

    /**
     * Sets the home classname of the bean to whcioh I refer.
     */
    public void setHomeClassName(String homeClassName) {
        refHomeIntf = homeClassName;
    }

    /**
     * @return the bean instance interface classname of the referee EJB.
     */
    public String getBeanClassName() {
        return refIntf;
    }

    /** Sets the bean instance business interface classname of the bean to which I refer.
     * this interface is the local object or the remote interfaces depending if the
     * reference is local or not.
    */
    public void setBeanClassName(String remoteClassName) {
        refIntf = remoteClassName;
    }

    /**
     * Gets the home classname of the referee EJB.
     * @return the class name of the EJB home.
     */
    @Override
    public String getEjbHomeInterface() {
        return getHomeClassName();
    }

    /**
     * Sets the local or remote home classname of the referee EJB.
     * @param homeClassName the class name of the EJB home.
     */
    @Override
    public void setEjbHomeInterface(String homeClassName) {
        setHomeClassName(homeClassName);
    }

    /**
     * Gets the local or remote interface classname of the referee EJB.
     * @return the classname of the EJB remote object.
     */
    @Override
    public String getEjbInterface() {
        return getBeanClassName();
    }
    /**
     * Sets the local or remote bean interface classname of the referee EJB.
     * @param remoteClassName the classname of the EJB remote object.
     */
    @Override
    public void setEjbInterface(String remoteClassName) {
        setBeanClassName(remoteClassName);
    }

    /**
     * @return true if the EJB reference is a 30 client view
     */
    @Override
    public boolean isEJB30ClientView() {
        return (getHomeClassName() == null);
    }

    /**
     * returns a formatted string representing me.
     */
    @Override
    public void print(StringBuffer toStringBuffer) {
        String localVsRemote = isLocal() ? "Local" : "Remote";
        toStringBuffer.append(localVsRemote + " ejb-ref ");
        toStringBuffer.append("name=" + getName());

        if (isEJB30ClientView()) {
            toStringBuffer.append("," + localVsRemote + " 3.x interface =" + getEjbInterface());
        } else {
            toStringBuffer.append("," + localVsRemote + " 2.x home =" + getEjbHomeInterface());
            toStringBuffer.append("," + localVsRemote + " 2.x component interface=" + getEjbInterface());
        }

        if (ejbDescriptor != null) {
            toStringBuffer.append(" resolved to intra-app EJB " + ejbDescriptor.getName() + " in module "
                + ejbDescriptor.getEjbBundleDescriptor().getModuleName());
        }

        toStringBuffer.append(",ejb-link=" + getLinkName());
        toStringBuffer.append(",lookup=" + getLookupName());
        toStringBuffer.append(",mappedName="+getMappedName());
        toStringBuffer.append(",jndi-name=" + getValue());

        toStringBuffer.append(",refType="+getType());
    }

    /* Equality on name. */
    @Override
    public boolean equals(Object object) {
        if (object instanceof EjbReference) {
            EjbReference ejbReference = (EjbReference) object;
            return ejbReference.getName().equals(this.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = NULL_HASH_CODE;
        String name = getName();
        if (name != null) {
            result += name.hashCode();
        }
        return result;
    }

    public boolean isConflict(EjbReferenceDescriptor other) {
        return (getName().equals(other.getName())) &&
            (!(
                DOLUtils.equals(getType(), other.getType()) &&
                DOLUtils.equals(getEjbHomeInterface(), other.getEjbHomeInterface()) &&
                DOLUtils.equals(getEjbInterface(), other.getEjbInterface()) &&
                DOLUtils.equals(getLinkName(), other.getLinkName())
                ) ||
            isConflictResourceGroup(other));
    }
}
