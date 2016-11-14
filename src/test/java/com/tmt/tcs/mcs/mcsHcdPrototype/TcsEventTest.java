package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.tmt.tcs.mcs.tcsToMcsEvents.PositionDemandEvent;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.internal.util.reflection.Whitebox;

/**
 * Unit test for {@link TcsEventTest} class.
 * @author E401581
 *
 */
public class TcsEventTest {

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link com.tmt.tcs.mcs.tcsToMcsEvents.TcsEvent#Tcsevent(java.lang.String)}.
   */
  @Test
  public final void testTcsevent() {
    PositionDemandEvent instance = PositionDemandEvent.getInstance();
    assertTrue(Whitebox.getInternalState(instance, "eventName") != null);
  }

  /**
   * Test method for {@link com.tmt.tcs.mcs.tcsToMcsEvents.TcsEvent#getEventName()}.
   */
  @Test
  public final void testGetEventName() {
    PositionDemandEvent instance = PositionDemandEvent.getInstance();
    String eventname = (String)Whitebox.getInternalState(instance, "eventName");
    assertTrue(instance.getEventName().compareTo(eventname) == 0);
  }

}
