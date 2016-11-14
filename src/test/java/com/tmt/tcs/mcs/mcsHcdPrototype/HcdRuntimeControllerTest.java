package com.tmt.tcs.mcs.mcsHcdPrototype;

import com.tmt.tcs.mcs.mcsHcdPrototype.HcdRuntimeController;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import static org.junit.Assert.assertTrue;

/**
 * @author E401581
 *     Unit test for {@link HcdRuntimeController} class.
 */
public class HcdRuntimeControllerTest {
  
  /**
   * @throws java.lang.Exception Exception during setup.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception Exception during cleanup.
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception Exception during setup.
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception Exception during cleanup.
   */
  @After
  public void tearDown() throws Exception {
  }
  
  /**
   * Test method for {@link com.tmt.tcs.mcs.mcsHcdPrototype.HcdRuntimeController#Hcdcontroller()}.
   */
  @Test
  public final void testHcdcontroller() {
    HcdRuntimeController controller = new HcdRuntimeController();
    if (Whitebox.getInternalState(controller, "cmdListener") == null) {
      assertTrue(false);
      return;
    }
    
    if (Whitebox.getInternalState(controller, "evtListener") == null) {
      assertTrue(false);
      return;
    }
    
    if (Whitebox.getInternalState(controller, "evtPublisher") == null) {
      assertTrue(false);
      return;
    }
    
    if (Whitebox.getInternalState(controller, "mcsListener") == null) {
      assertTrue(false);
      return;
    }
    
    assertTrue(true);
  }
  
  /**
   * Test method for {@link com.tmt.tcs.mcs.mcsHcdPrototype.HcdRuntimeController#setState()}.
   */
  @Test
  public final void testSetState() {
    HcdRuntimeController controller = new HcdRuntimeController();
    
    final long polltime = 10;
    boolean state = true;
    controller.setState(state);
    if (((Boolean)Whitebox.getInternalState(controller, "isFollowing")).booleanValue() != state) {
      assertTrue(false);
      return;
    }
    
    if (((Long)Whitebox.getInternalState(controller, "pollTime")).longValue() != polltime) {
      assertTrue(false);
      return;
    }
    
    assertTrue(true);    
  }
  
  /**
   * Test method for {@link com.tmt.tcs.mcs.mcsHcdPrototype.HcdRuntimeController#stop()}.
   */
  @Test
  public final void testStop() {
    HcdRuntimeController controller = new HcdRuntimeController();
    controller.stop();
    
    assertTrue(((Boolean)Whitebox.getInternalState(controller, "proceed")).booleanValue() == false);
  }

}
