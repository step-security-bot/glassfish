/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 * Copyright (c) 2012, 2020 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.enterprise.config.serverbeans;

import com.sun.enterprise.config.util.ConfigApiLoggerInfo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.api.admin.config.ConfigExtension;
import org.glassfish.api.admin.config.PropertiesDesc;
import org.glassfish.api.admin.config.PropertyDesc;
import org.glassfish.config.support.datatypes.NonNegativeInteger;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.ConfigSupport;
import org.jvnet.hk2.config.Configured;
import org.jvnet.hk2.config.DuckTyped;
import org.jvnet.hk2.config.Element;
import org.jvnet.hk2.config.SingleConfigCode;
import org.jvnet.hk2.config.TransactionFailure;
import org.jvnet.hk2.config.types.Property;
import org.jvnet.hk2.config.types.PropertyBag;

@Configured
public interface HttpService extends ConfigBeanProxy, PropertyBag, ConfigExtension {

    /**
     * Gets the value of the accessLog property.
     *
     * @return possible object is {@link AccessLog }
     */
    @Element
    @NotNull
    AccessLog getAccessLog();

    /**
     * Sets the value of the accessLog property.
     *
     * @param value allowed object is {@link AccessLog }
     */
    void setAccessLog(AccessLog value) throws PropertyVetoException;

    /**
     * Gets the value of the virtualServer property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the
     * virtualServer property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getVirtualServer().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list {@link VirtualServer }
     */
    @Element(required = true)
    List<VirtualServer> getVirtualServer();

    @DuckTyped
    VirtualServer getVirtualServerByName(String name);

    @DuckTyped
    List<String> getNonAdminVirtualServerList();

    @Attribute(defaultValue = "false")
    @Pattern(regexp = "(false|true|on|off)", message = "Valid values: true|false")
    String getAccessLoggingEnabled();

    void setAccessLoggingEnabled(String enabled);

    /**
     * If true, single sign-on is enabled by default for all web applications on all virtual servers on this server instance
     * that are configured for the same realm. If false, single sign-on is disabled by default for all virtual servers, and
     * users must authenticate separately to every application on each virtual server. The sso-enabled property setting of
     * the virtual-server element can override this setting for an individual virtual server or inherit the value by using
     * "inherit."
     *
     * @return possible object is {@link String }
     */
    @Attribute(defaultValue = "false")
    @Pattern(regexp = "(true|false|on|off)", message = "Valid values: true|false")
    String getSsoEnabled();

    /**
     * Sets the value of the sso-enabled property.
     *
     * @param value allowed object is {@link String }
     */
    void setSsoEnabled(String value);

    class Duck {
        private static final Logger logger = ConfigApiLoggerInfo.getLogger();

        public static VirtualServer getVirtualServerByName(HttpService target, String name) {
            for (VirtualServer v : target.getVirtualServer()) {
                if (v.getId().equals(name)) {
                    return v;
                }
            }
            return null;
        }

        public static List<String> getNonAdminVirtualServerList(HttpService target) {
            List<String> nonAdminVSList = new ArrayList<>();
            for (VirtualServer v : target.getVirtualServer()) {
                if (!v.getId().equals("__asadmin")) {
                    nonAdminVSList.add(v.getId());
                }
            }
            return Collections.unmodifiableList(nonAdminVSList);
        }

        public static HttpService createDefaultConfig(Config c) {
            final Config param = c;
            try {

                ConfigSupport.apply(new SingleConfigCode<Config>() {
                    @Override
                    public Object run(Config param) throws PropertyVetoException, TransactionFailure {
                        HttpService httpService = param.createChild(HttpService.class);
                        AccessLog accessLog = httpService.createChild(AccessLog.class);
                        List<VirtualServer> vsList = httpService.getVirtualServer();
                        httpService.setAccessLog(accessLog);

                        VirtualServer vs = httpService.createChild(VirtualServer.class);
                        vs.setId("server");
                        vs.setNetworkListeners("http-listener-1,http-listener-2");

                        VirtualServer vs1 = httpService.createChild(VirtualServer.class);
                        vs1.setId("__asadmin");
                        vs1.setNetworkListeners("admin-listener");

                        vsList.add(vs);
                        vsList.add(vs1);
                        param.getContainers().add(httpService);
                        return httpService;
                    }
                }, param);
            } catch (TransactionFailure ex) {
                // Will use the BG logging infrastrucre... And probably some exception type?
                logger.log(Level.INFO, ConfigApiLoggerInfo.unableToCreateHttpServiceConfig, ex);
            }
            return param.getExtensionByType(HttpService.class);
        }
    }

    @Override
    @PropertiesDesc(props = {
            @PropertyDesc(name = "monitoring-cache-enabled", defaultValue = "true", dataType = Boolean.class, description = "Enables the monitoring cache"),

            @PropertyDesc(name = "monitoring-cache-refresh-in-millis", defaultValue = "5000", dataType = NonNegativeInteger.class, description = "Specifies the interval between refreshes of the monitoring cache"),

            @PropertyDesc(name = "ssl-cache-entries", defaultValue = "10000", dataType = NonNegativeInteger.class, description = "Specifies the number of SSL sessions to be cached"),

            @PropertyDesc(name = "ssl3-session-timeout", defaultValue = "86400", dataType = NonNegativeInteger.class, description = "Specifies the interval at which SSL3 sessions are cached"),

            @PropertyDesc(name = "ssl-session-timeout", defaultValue = "100", dataType = NonNegativeInteger.class, description = "Specifies the interval at which SSL2 sessions are cached"),

            @PropertyDesc(name = "recycle-objects", defaultValue = "true", dataType = Boolean.class, description = "Whether to recycle internal objects instead of using the VM garbage collector"),

            @PropertyDesc(name = "reader-threads", defaultValue = "0", dataType = NonNegativeInteger.class, description = "Specifies the number of reader threads, which read bytes from the non-blocking socket"),

            @PropertyDesc(name = "acceptor-queue-length", defaultValue = "4096", dataType = NonNegativeInteger.class, description = "Specifies the length of the acceptor thread queue. Once full, connections are rejected"),

            @PropertyDesc(name = "reader-queue-length", defaultValue = "4096", dataType = NonNegativeInteger.class, description = "Specifies the length of the reader thread queue. Once full, connections are rejected"),

            @PropertyDesc(name = "use-nio-direct-bytebuffer", defaultValue = "true", dataType = Boolean.class, description = "Controls whether the NIO direct ByteBuffer is used. In a limited resource environment, "
                    + "it might be faster to use non-direct Java's ByteBuffer by setting a value of false"),

            @PropertyDesc(name = "authPassthroughEnabled", defaultValue = "false", dataType = Boolean.class, description = "Indicates that the http-listeners receive traffic from an SSL-terminating proxy server, "
                    + "which is responsible for forwarding any information about the original client request (such as client "
                    + "IP address, SSL keysize, and authenticated client certificate chain) to the HTTP listeners using custom request headers. "
                    + "Each  subelement can override this setting for itself"),
            /**
             * Specifies the fully qualified class name of a custom implementation of the com.sun.appserv.ProxyHandler abstract
             * class, which allows a back-end application server instance to retrieve information about the original client request
             * that was intercepted by an SSL-terminating proxy server (for example, a load balancer). An implementation of this
             * abstract class inspects a given request for the custom request headers through which the proxy server communicates
             * the information about the original client request to the Enterprise Server instance, and returns that information to
             * its caller. The default implementation reads the client IP address from an HTTP request header named Proxy-ip, the
             * SSL keysize from an HTTP request header named Proxy-keysize, and the SSL client certificate chain from an HTTP
             * request header named Proxy-auth-cert. The Proxy-auth-cert value must contain the BASE-64 encoded client certificate
             * chain without the BEGIN CERTIFICATE and END CERTIFICATE boundaries and with \n replaced with % d% a. Only used if
             * authPassthroughEnabled is set to true. Each “http-listener” on page 37 subelement can override the setting for
             * itself.
             */

            @PropertyDesc(name = "proxyHandler", defaultValue = "com.sun.enterprise.web.web.ProxyHandlerImpl", description = "Specifies the fully qualified class name of a custom implementation of com.sun.appserv.ProxyHandler. "
                    + "Only used if authPassthroughEnabled is set to true. Each http-listener can override the setting for itself"),

            @PropertyDesc(name = "bufferSize", defaultValue = "4096", dataType = NonNegativeInteger.class, description = "Size in bytes of the buffer to be provided for input streams created by HTTP listeners"),

            @PropertyDesc(name = "connectionTimeout", defaultValue = "30", dataType = NonNegativeInteger.class, description = "Number of seconds HTTP listeners wait, after accepting a connection, for the request URI line to be presented"),

            @PropertyDesc(name = "maxKeepAliveRequests", defaultValue = "250", dataType = NonNegativeInteger.class, description = "Maximum number of HTTP requests that can be pipelined until the connection is closed by the server. "
                    + "Set this property to 1 to disable HTTP/1.0 keep-alive, as well as HTTP/1.1 keep-alive and pipelining"),

            @PropertyDesc(name = "traceEnabled", defaultValue = "true", dataType = Boolean.class, description = "Enables the TRACE operation. Set  to false to make the server less susceptible to cross-site scripting attacks"),

            @PropertyDesc(name = "accessLoggingEnabled", defaultValue = "false", dataType = Boolean.class, description = "Controls access logging for all virtual-server that do not specify this property"),

            @PropertyDesc(name = "disableUploadTimeout", defaultValue = "true", dataType = Boolean.class, description = "If false, the connection for a servlet that reads bytes slowly is closed after the 'connectionUploadTimeout' is reached"),

            @PropertyDesc(name = "connectionUploadTimeout", defaultValue = "5", dataType = NonNegativeInteger.class, description = "Specifies the timeout for uploads. Applicable only if 'disableUploadTimeout' is set to false"),

            @PropertyDesc(name = "uriEncoding", defaultValue = "UTF-8", description = "Specifies the character set used to decode the request URIs received on http-listeners that "
                    + "do not define this property. Must be a valid IANA character set name") })
    @Element("property")
    List<Property> getProperty();
}
