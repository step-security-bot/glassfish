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

package org.glassfish.contextpropagation.internal;

public class SimpleMapTest {
//  SimpleMap sm;
//  static RecordingLoggerAdapter logger;
//
//  public static class LifeCycleEventRecorder implements ContextLifecycle {
//    StackTraceElement lastElement;
//    Object lastArg;
//
//    @Override
//    public void contextChanged(Object replacementContext) {
//      set(Thread.currentThread().getStackTrace(), replacementContext);
//    }
//
//    @Override
//    public void contextAdded() {
//      set(Thread.currentThread().getStackTrace(), null);
//    }
//
//    @Override
//    public void contextRemoved() {
//      set(Thread.currentThread().getStackTrace(), null);
//    }
//
//    @Override
//    public ViewCapable contextToPropagate() {
//      set(Thread.currentThread().getStackTrace(), null);
//      return this;
//    }
//
//    void set(StackTraceElement[] trace, Object arg) {
//      lastElement = trace[1];
//      lastArg = arg;
//    }
//
//    void verify(String methodName, Object arg) {
//      assertEquals(methodName, lastElement.getMethodName());
//      assertEquals(arg, lastArg);
//      lastElement = null;
//      lastArg = null;
//    }
//
//  }
//
//  private static final LifeCycleEventRecorder LIFE_CYCLE_CONTEXT = new LifeCycleEventRecorder();
//  static final Entry DUMMY_ENTRY = createEntry(LIFE_CYCLE_CONTEXT, PropagationMode.defaultSet(), ContextType.OPAQUE);
//
//  private static Entry createEntry(Object context, EnumSet<PropagationMode> propModes, ContextType ct) {
//    return new Entry(context, propModes, ct) {
//      void validate() {}
//    };
//  }
//
//  @BeforeClass
//  public static void setupClass() {
//    logger = new RecordingLoggerAdapter();
//    BootstrapUtils.reset();
//    ContextBootstrap.configure(logger,
//        new DefaultWireAdapter(), new MockThreadLocalAccessor(),
//        new MockContextAccessController(), "guid");
//  }
//
//  @Before
//  public void setup() {
//    sm = new SimpleMap();
//    sm.put("foo", createEntry("fooString", PropagationMode.defaultSet(), ContextType.STRING));
//  }
//
//  @Test
//  public void testGetEntry() {
//    Entry entry = sm.getEntry("foo");
//    assertEquals("fooString", entry.getValue());
//    logger.verify(Level.DEBUG, null, MessageID.OPERATION, new Object[] {"getEntry", "foo", entry} );
//  }
//
//  @Test(expected=java.lang.IllegalArgumentException.class)
//  public void testGetEntryWithNullKey() {
//    sm.getEntry(null); // Does not log if fails validation
//  }
//
//  @Test
//  public void testGet() {
//    assertEquals("fooString", sm.get("foo"));
//    logger.verify(Level.DEBUG, null, MessageID.OPERATION, new Object[] {"get", "foo", "fooString"} );
//  }
//
//  @Test(expected=java.lang.IllegalArgumentException.class)
//  public void testGetWithNullKey() {
//    sm.get(null); // Does not log if validate fails
//  }
//
//  @Test
//  public void testPutWhereNoBefore() {
//    Entry e = sm.put("new key", DUMMY_ENTRY);
//    assertNull(e);
//    logger.verify(Level.DEBUG, null, MessageID.PUT, new Object[] {"new key", DUMMY_ENTRY.value, null} );
//    LIFE_CYCLE_CONTEXT.verify("contextAdded", null);
//  }
//
//  @Test
//  public void testPutReplace() {
//    LifeCycleEventRecorder oldRecorder = new LifeCycleEventRecorder();
//    String fooString = sm.put("foo", createEntry(oldRecorder, PropagationMode.defaultSet(), ContextType.OPAQUE));
//    assertEquals("fooString", fooString);
//    logger.verify(Level.DEBUG, null, MessageID.PUT, new Object[] {"foo", oldRecorder, "fooString"} );
//    LifeCycleEventRecorder oldValue = sm.put("foo", DUMMY_ENTRY);
//    assertEquals(oldRecorder, oldValue);
//    oldRecorder.verify("contextChanged", LIFE_CYCLE_CONTEXT); // oldRecoder finds out about the new value
//    LIFE_CYCLE_CONTEXT.verify("contextAdded", null);
//  }
//
//  @Test(expected=java.lang.IllegalArgumentException.class)
//  public void testPutWithNullKey() {
//    sm.put(null, DUMMY_ENTRY);
//  }
//
//  @Test(expected=java.lang.IllegalArgumentException.class)
//  public void testPutWithNullEntry() {
//    sm.put("dummy key", null);
//  }
//
//  @Test(expected=java.lang.IllegalArgumentException.class)
//  public void testPutWithNullValue() {
//    sm.put("dummy key", createEntry(null, PropagationMode.defaultSet(), ContextType.ATOMICINTEGER));
//  }
//
//  @Test(expected=java.lang.IllegalArgumentException.class)
//  public void testPutWithInvalidEntry() {
//    sm.put("dummy key", new Entry(null, PropagationMode.defaultSet(), ContextType.ATOMICINTEGER) {
//      void validate() { throw new IllegalStateException(); }
//    });
//  }
//
//  @Test
//  public void testRemove() {
//    sm.put("removeMe", createEntry(LIFE_CYCLE_CONTEXT, PropagationMode.defaultSet(), ContextType.STRING));
//    Object removeMe = sm.remove("removeMe");
//    assertEquals(LIFE_CYCLE_CONTEXT, removeMe);
//    logger.verify(Level.DEBUG, null, MessageID.OPERATION, new Object[] {"remove", "removeMe", LIFE_CYCLE_CONTEXT} );
//    LIFE_CYCLE_CONTEXT.verify("contextRemoved", null);
//  }
//
//  @Test
//  public void testRemoveNoneExistent() {
//    String removeMe = sm.remove("removeMe");
//    assertEquals(null, removeMe);
//  }
//
//  @Test
//  public void testEmptyIterator() {
//    SimpleMap emptyMap = new SimpleMap();
//    Iterator<?> iter = emptyMap.iterator(null, null);
//    assertFalse(iter.hasNext());
//  }
//
//  @Test
//  public void testIteratorFiltersAll() {
//    sm.put("dummy", DUMMY_ENTRY);
//    Iterator<Map.Entry<String, Entry>> iter = sm.iterator(new Filter() {
//      @Override
//      public boolean keep(java.util.Map.Entry<String, Entry> mapEntry,
//          PropagationMode mode) {
//        return false;
//      }
//    }, PropagationMode.JMS_QUEUE);
//    assertFalse(iter.hasNext());
//  }
//
//  @SuppressWarnings("serial")
//  @Test
//  public void testIteratorFilterNone() {
//    sm.put("dummy", DUMMY_ENTRY);
//    Iterator<Map.Entry<String, Entry>> iter = sm.iterator(new Filter() {
//      @Override
//      public boolean keep(java.util.Map.Entry<String, Entry> mapEntry,
//          PropagationMode mode) {
//        return true;
//      }
//    }, PropagationMode.JMS_QUEUE);
//    int count = 0;
//    HashSet<String> keys = new HashSet<String>();
//    while (iter.hasNext()) {
//      keys.add(iter.next().getKey());
//      count++;
//    }
//    assertEquals(2, count);
//    assertEquals(new HashSet<String>() {{add("foo"); add("dummy");}}, keys);
//  }
//
//  @Test
//  public void testIteratorRemove() {
//    sm.put("dummy", DUMMY_ENTRY);
//    Iterator<Map.Entry<String, Entry>> iter = sm.iterator(new Filter() {
//      @Override
//      public boolean keep(java.util.Map.Entry<String, Entry> mapEntry,
//          PropagationMode mode) {
//        return true;
//      }
//    }, PropagationMode.JMS_QUEUE);
//    assertEquals(2, sm.map.size());
//    assertNotNull(iter.next());
//    iter.remove();
//    assertEquals(1, sm.map.size());
//    assertNotNull(iter.next());
//    iter.remove();
//    assertEquals(0, sm.map.size());
//    int exceptionCount = 0;
//    try {
//      iter.next();
//    } catch (NoSuchElementException nsee) {exceptionCount++;}
//    assertEquals("Expected NoSuchElementException after the last element was retrieved", 1, exceptionCount);
//    try {
//      iter.remove();
//    } catch (IllegalStateException ise) {exceptionCount++;}
//    assertEquals("Expected IllegalStateException on last remove call", 2, exceptionCount);
//  }

}
