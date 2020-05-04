/*
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

package com.sun.enterprise.v3.admin;

import java.util.*;
import java.util.logging.Logger;

import com.sun.enterprise.config.modularity.ConfigModularityUtils;
import com.sun.enterprise.config.modularity.GetSetModularityHelper;
import com.sun.enterprise.config.serverbeans.Domain;
import com.sun.enterprise.util.LocalStringManagerImpl;
import com.sun.enterprise.v3.common.PropsFileActionReporter;
import java.util.logging.Level;
import org.glassfish.api.ActionReport;
import org.glassfish.api.Param;
import org.glassfish.api.admin.*;
import org.glassfish.flashlight.MonitoringRuntimeDataRegistry;
import org.glassfish.internal.api.Target;
import jakarta.inject.Inject;
import org.glassfish.api.admin.AccessRequired.AccessCheck;

import org.jvnet.hk2.annotations.Optional;

import org.jvnet.hk2.annotations.Service;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.kernel.KernelLoggerInfo;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Dom;
import org.jvnet.hk2.config.types.Property;


/**
 * User: Jerome Dochez
 * Date: Jul 10, 2008
 * Time: 12:17:26 AM
 */
@Service(name = "get")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@ExecuteOn({RuntimeType.DAS, RuntimeType.INSTANCE})
@RestEndpoints({
    @RestEndpoint(configBean=Domain.class,
        opType=RestEndpoint.OpType.GET, 
        path="get", 
        description="Get")
})
public class GetCommand extends V2DottedNameSupport implements AdminCommand,
        AdminCommandSecurity.Preauthorization,
        AdminCommandSecurity.AccessCheckProvider {
    @Inject
    private MonitoringReporter mr;
    @Inject
    private Domain domain;
    @Inject
    private ServerEnvironment serverEnv;
    @Inject
    private Target targetService;
    @Inject
    private ServiceLocator habitat;
    @Param(optional = true, defaultValue = "false", shortName = "m")
    private Boolean monitor;
    @Param(optional = true, defaultValue = "false", shortName = "c")
    private Boolean aggregateDataOnly;
    @Param(primary = true)
    private String pattern;
    @Inject @Optional
    private MonitoringRuntimeDataRegistry mrdr;
    final private static LocalStringManagerImpl localStrings =
            new LocalStringManagerImpl(GetCommand.class);

    private ActionReport report;
    
    private List<Map.Entry> matchingNodesSorted;
    
    private String prefix;

    @Inject
    @Optional
    GetSetModularityHelper modularityHelper;

    @Override
    public boolean preAuthorization(AdminCommandContext context) {
        if (monitor) {
            return preAuthorizationForMonitoring(context);
        } else {
            return preAuthorizationForNonMonitoring(context);
        }
    }
    
    private boolean preAuthorizationForMonitoring(final AdminCommandContext context) {
        mr.prepareGet(context, pattern, aggregateDataOnly);
        return true;
    }
    
    private boolean preAuthorizationForNonMonitoring(final AdminCommandContext context) {
        report = context.getActionReport();

        /* Issue 5918 Used in ManifestManager to keep output sorted */
        try {
            PropsFileActionReporter reporter = (PropsFileActionReporter) report;
            reporter.useMainChildrenAttribute(true);
        }
        catch (ClassCastException e) {
            // ignore this is not a manifest output.
        }

        matchingNodesSorted = findSortedMatchingNodes();
        return matchingNodesSorted != null;
    }

    @Override
    public Collection<? extends AccessCheck> getAccessChecks() {
        if (monitor) {
            return mr.getAccessChecksForGet();
        } else {
            return getAccessChecksForNonMonitoring();
        }
    }
    
    private Collection<? extends AccessCheck> getAccessChecksForNonMonitoring() {
        final Collection<AccessCheck> accessChecks = new ArrayList<AccessCheck>();
        for (Map.Entry entry : matchingNodesSorted) {
            accessChecks.add(new AccessCheck(AccessRequired.Util.resourceNameFromDom((Dom)entry.getKey()), "read"));
        }
        return accessChecks;
    }
    
    @Override
    public void execute(AdminCommandContext context) {

        if (monitor) {
            getMonitorAttributes(context);
            //String old = report.getMessage();
            //String append = "\nXXXXXXXXX\n" + mr.toString();
            //report.setMessage(old == null ? append : old + append);
            return;
        }


        boolean foundMatch = false;
        for (Map.Entry<Dom, String> node : matchingNodesSorted) {
            // if we get more of these special cases, we should switch to a Renderer pattern
            if (Property.class.getName().equals(node.getKey().model.targetTypeName)) {
                // special display for properties...
                if (matches(node.getValue(), pattern)) {
                    ActionReport.MessagePart part = report.getTopMessagePart().addChild();
                    part.setChildrenType("DottedName");
                    part.setMessage(prefix + node.getValue() + "=" + node.getKey().attribute("value"));
                    foundMatch = true;
                }
            }
            else {
                Map<String, String> attributes = getNodeAttributes(node.getKey(), pattern);
                TreeMap<String, String> attributesSorted = new TreeMap(attributes);
                for (Map.Entry<String, String> name : attributesSorted.entrySet()) {
                    String finalDottedName = node.getValue() + "." + name.getKey();
                    if (matches(finalDottedName, pattern)) {
                        ActionReport.MessagePart part = report.getTopMessagePart().addChild();
                        part.setChildrenType("DottedName");
                        part.setMessage(prefix + node.getValue() + "." + name.getKey() + "=" + name.getValue());
                        foundMatch = true;
                    }
                }
            }
        }
        if (!foundMatch) {
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            //report.setMessage("No object found matching " + pattern);
            report.setMessage(localStrings.getLocalString("admin.get.path.notfound", "Dotted name path {0} not found.", prefix + pattern));

        }
    }

    private void getMonitorAttributes(AdminCommandContext ctxt) {
        
        Logger l = KernelLoggerInfo.getLogger();
        if (l.isLoggable(Level.FINE)) {
            l.log(Level.FINE, "Get Command: {0}", mr.toString());
        }
        mr.execute();
    }
    
    private List<Map.Entry> findSortedMatchingNodes() {

        if (!monitor) {
            if (modularityHelper != null) {
                modularityHelper.getLocationForDottedName(pattern);
            }
        }
        // check for logging patterns
        if (pattern.contains(".log-service")) {
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            report.setMessage(localStrings.getLocalString("admin.get.invalid.logservice.command", "For getting log levels/attributes use list-log-levels/list-log-attributes command."));
            return null;
        }

        //check for incomplete dotted name
        if (!pattern.equals("*")) {
            if (pattern.lastIndexOf(".") == -1 || pattern.lastIndexOf(".") == (pattern.length() - 1)) {
                report.setActionExitCode(ActionReport.ExitCode.FAILURE);
                //report.setMessage("Missing expected dotted name part");
                report.setMessage(localStrings.getLocalString("missing.dotted.name", "Missing expected dotted name part"));
                return null;
            }
        }

        // first let's get the parent for this pattern.
        TreeNode[] parentNodes = getAliasedParent(domain, pattern);

        // reset the pattern.
        prefix = "";
        if(!pattern.startsWith(parentNodes[0].relativeName)) {
            prefix = pattern.substring(0, pattern.indexOf(parentNodes[0].relativeName));
            pattern = parentNodes[0].relativeName;
        } else {
            pattern = parentNodes[0].relativeName;
        }

        Map<Dom, String> matchingNodes;
        Map<Dom, String> dottedNames = new HashMap<Dom, String>();
        for (TreeNode parentNode : parentNodes) {
            dottedNames.putAll(getAllDottedNodes(parentNode.node));
            if (parentNode.name.equals("")) {
                dottedNames.put(parentNode.node, "domain");
            }
        }
        matchingNodes = getMatchingNodes(dottedNames, pattern);
        if (matchingNodes.isEmpty() && pattern.lastIndexOf('.') != -1) {
            // it's possible the user is just looking for an attribute, let's remove the
            // last element from the pattern.
            matchingNodes = getMatchingNodes(dottedNames, pattern.substring(0, pattern.lastIndexOf(".")));
        }
        
        //No matches found - report the failure and return
        if (matchingNodes.isEmpty()) {
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            //report.setMessage("Dotted name path \"" + prefix + pattern + "\" not found.");
            report.setMessage(localStrings.getLocalString("admin.get.path.notfound", "Dotted name path {0} not found.", prefix + pattern));
            return null;
        }

        List<Map.Entry> result = sortNodesByDottedName(matchingNodes);
        result = applyOverrideRules(result);
        return result;
    }
}
