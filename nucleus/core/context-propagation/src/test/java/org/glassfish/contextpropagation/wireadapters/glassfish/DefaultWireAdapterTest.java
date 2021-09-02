/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.glassfish.contextpropagation.wireadapters.glassfish;

//import mockit.Deencapsulation;



public class DefaultWireAdapterTest {
//  ContextMap wcMap;
//
//  @Before
//  public void setup() throws InsufficientCredentialException {
//    BootstrapUtils.reset();
//    BootstrapUtils.bootstrap(new DefaultWireAdapter());
//    BootstrapUtils.populateMap();
//    wcMap = ContextMapHelper.getScopeAwareContextMap();
//  }
//
//  @Test
//  public void testWrite() throws IOException, InterruptedException {
//    ByteArrayOutputStream out = new ByteArrayOutputStream();
//    ContextMapHelper.getScopeAwarePropagator().sendRequest(out, PropagationMode.RMI);
//    byte[] bytes = out.toByteArray();
//    MockLoggerAdapter.debug("length: " + bytes.length + ", "  + Utils.toString(bytes));
//
//    // Move to its own test
//    final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//    new TestableThread() {
//      @Override public void runTest() throws Exception {
//          ContextMap map = ContextMapHelper.getScopeAwareContextMap();
//          assertNull(map.get("one"));
//          ContextMapHelper.getScopeAwarePropagator().receiveRequest(bais);
//          assertNotNull(map.get("one"));
//      }
//    }.startJoinAndCheckForFailures();
//  }
//
//  @Test
//  public void testPropagateOpaque() throws PropertyReadOnlyException, IOException, InsufficientCredentialException {
//    BootstrapUtils.reset();
//    WireAdapter adapter = new WLSWireAdapter();
//    BootstrapUtils.bootstrap(adapter);
//    // Receive data using WLS adaptor but lacking the necessary factory to instantiate the opaque. Make sure the object is opaque
//    PropagationTest.setup();
//    byte[] bytes = PropagationTest.serialize();
//    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//    registerWorkContextFactory();
//    ContextMapPropagator propagator = Utils.getScopeAwarePropagator();
//    propagator.useWireAdapter(adapter);
//    propagator.receiveRequest(bais);
//    ContextMap cm = Utils.getScopeAwareContextMap();
//    assertNotNull(cm.get("workcontext"));
//
//    // Propagate and receive using Default adaptors, but this time the factory is available. Make sure the object is properly instantiated.
//    // Propagate to a process where there is no factory for workcontext
//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    propagator = Utils.getScopeAwarePropagator();
//    adapter = new DefaultWireAdapter();
//    propagator.useWireAdapter(adapter);
//    propagator.sendRequest(baos, PropagationMode.RMI);
//
//    // Receive on a process where the factory is not registered
//    Map<String, SerializableContextFactory> contextFactoriesByContextName =
//        Deencapsulation.getField(WireAdapter.HELPER, "contextFactoriesByContextName");
//    contextFactoriesByContextName.remove("workcontext");
//    BootstrapUtils.reset();
//    adapter = new DefaultWireAdapter();
//    BootstrapUtils.bootstrap(adapter);
//    bytes = baos.toByteArray();
//    bais = new ByteArrayInputStream(bytes);
//    propagator.useWireAdapter(adapter);
//    propagator.receiveRequest(bais);
//    assertNotNull(cm.get("workcontext"));
//    // Then propagate again to a process where the library is registered
//    baos = new ByteArrayOutputStream();
//    propagator = Utils.getScopeAwarePropagator();
//    adapter = new DefaultWireAdapter();
//    propagator.useWireAdapter(adapter);
//    propagator.sendRequest(baos, PropagationMode.RMI);
//    BootstrapUtils.reset();
//    adapter = new DefaultWireAdapter();
//    BootstrapUtils.bootstrap(adapter);
//    bytes = baos.toByteArray();
//    bais = new ByteArrayInputStream(bytes);
//    propagator.useWireAdapter(adapter);
//    registerWorkContextFactory();
//    propagator.receiveRequest(bais);
//    MyWLSContext mwc = cm.get("workcontext");
//    assertNotNull(mwc);
//    assertEquals((long) 200, (long) mwc.l);
//  }
//
//  private void registerWorkContextFactory() {
//    WireAdapter.HELPER.registerContextFactoryForContextNamed("workcontext", null,
//        new SerializableContextFactory() {
//          @Override
//          public WLSContext createInstance() {
//            return new MyWLSContext();
//          }
//    });
//  }
//
//  @Test public void testWithCatalog() throws PropertyReadOnlyException, IOException {
//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    ContextMapPropagator propagator = Utils.getScopeAwarePropagator();
//    WireAdapter adapter = new DefaultWireAdapter();
//    propagator.useWireAdapter(adapter);
//    propagator.sendRequest(baos, PropagationMode.RMI);
//    Catalog catalog = Deencapsulation.getField((AbstractWireAdapter) adapter, "catalog");
//    BootstrapUtils.reset();
//    adapter = new DefaultWireAdapter();
//    BootstrapUtils.bootstrap(adapter);
//    byte[] bytes = baos.toByteArray();
//    MockLoggerAdapter.debug(Utils.toString(bytes));
//    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//    propagator.useWireAdapter(adapter);
//    propagator.receiveRequest(bais);
//    Catalog newCatalog = Deencapsulation.getField(adapter, "catalog");
//    assertEquals(catalog, newCatalog);
//  }
//
//  @Test public void testObjectInputStream() throws IOException {
//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    ObjectOutputStream oos = new ObjectOutputStream(baos);
//    oos.write("Some data".getBytes());
//    oos.flush();
//    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//    ObjectInputStream ois = new ObjectInputStream(bais);
//    assertFalse(ois.markSupported());
//  }

}
