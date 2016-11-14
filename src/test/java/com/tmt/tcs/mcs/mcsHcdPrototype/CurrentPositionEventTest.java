package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.tmt.tcs.mcs.mcsToTcsEvents.CurrentPositionEvent;
import com.tmt.tcs.mcs.protos.TcsMcsEventsProtos.McsCurrentPositionEvent;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.internal.util.reflection.Whitebox;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

/**
 * Provides unit test for testing {@link CurrentPositionEvent} class. 
 * @author E401581.
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(McsCurrentPositionEvent.class )
public class CurrentPositionEventTest extends PowerMockTestCase {
  
  /**
   * Name of current position event.
   */
  private static final String CURRENT_POSITION_EVENT_NAME = "mcs.currentposition";

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() {
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
   * Test method for {@link com.tmt.tcs.mcs.mcsToTcsEvents.CurrentPositionEvent
   * #CurrentPositionEvent(com.tmt.tcs.mcs.protos.TcsMcsEventsProtos.McsCurrentPositionEvent)}.
   */
  @Test
  public final void testCurrentpositionevent() {
    //Send current position event    
    McsCurrentPositionEvent mockEvent = PowerMockito.mock(McsCurrentPositionEvent.class);        
    
    CurrentPositionEvent positionEvent = new CurrentPositionEvent(mockEvent);
    String eventName = (String)Whitebox.getInternalState(positionEvent, "eventName"); 
    if (eventName == null) {
      assertTrue(false);
      return;
    }
    
    if (Whitebox.getInternalState(positionEvent, "message") == null) {
      assertTrue(false);
      return;
    }
    
    if (eventName.compareTo(CURRENT_POSITION_EVENT_NAME) == 0) {
      assertTrue(true);
    }    
  }

}

