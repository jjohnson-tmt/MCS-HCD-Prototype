package com.tmt.tcs.mcs.mcsHcdPrototype;

import org.zeromq.ZMQ;

import java.util.logging.Logger;

/**
 * Runtime container for all the HCD components.
 * The container is responsible for instantiating all the components,
 * control the sequence of execution between them and maintain its lifecycle.
 * @author vkhairnar.
 */
public final class HcdRuntimeController implements Runnable {
  
  /**
   * Logger instance.
   */
  private static final Logger logger = Logger
      .getLogger(HcdRuntimeController.class.getName());  
  
  /**
   * Instance of {@link CommandListener}, listening to commands from MCS assembly.
   */
  private CommandListener cmdListener = null;
  
  /**
   * Instance of {@link EventListener}, listening to events from MCS assembly.
   */
  private EventListener evtListener = null;
  
  /**
   * Instance of {@link EventPublisher}, publishing mcs events to mcs assembly.
   */
  private EventPublisher evtPublisher = null;
      
  /**
   * Instance of {@link McsEventListener}, listening to mcs events.
   */
  private McsEventListener mcsListener = null;
  
  /**
   * Flag to exit from run.
   */
  private volatile boolean proceed = true;
  
  /**
   * Flag represents if MCS is in following state.
   */
  private boolean isFollowing = false;
  
  /**
   * Poll time for checking commands, events to be executed
   * from MCS Assembly and check for events from MCS.
   */
  private long pollTime = 10;
        
  /**
   * Initializes new instance of {@link HcdRuntimeController}.   
   */
  public HcdRuntimeController() {
    cmdListener = TcsCommandListener.getInstance();
    evtListener = TcsEventListener.getInstance();
    evtPublisher = TcsEventPublisher.getInstance();
    mcsListener = McsEventListeningProvider.getInstance();        
    
  }
  
  /**
   * Report state of MCS.
   * @param isFollowing notifies if mcs is in following state or not.
   */
  public void setState(boolean isFollowing) {
    this.isFollowing = isFollowing;
    pollTime = 10;
  }
  
  /**
   * Exits the run method.
   */
  public void stop() {
    proceed = false;
  }    
  
  /**
   * Provides implementation for controlling various HCD components.
   */
  public void run() {
    // TODO Auto-generated method stub
    logger.info("run method called.");   
    //initialize all components first
    ZMQ.Context context = ZMQ.context(1);
    CommandExecutor.getInstance().setContext(context);
    McsEventListeningProvider.getInstance().setContext(context);
    TcsCommandListener.getInstance().setCallback(this);
    
    //call init
    CommandExecutor.getInstance().init();
    cmdListener.init();
    evtListener.init();
    evtPublisher.init();
    mcsListener.init();
    
    //call start
    CommandExecutor.getInstance().start();
    cmdListener.start();
    evtListener.start();
    evtPublisher.start();
    mcsListener.start();

    while ((!Thread.currentThread().isInterrupted()) && proceed) {      
      // Send position demand
      logger.info("calling listenforHcdCommand.");
      cmdListener.listenforHcdCommand();
      if (isFollowing) {
        logger.info("calling listenforEvents.");
        evtListener.listenforEvents();  
      }
      
      logger.info("calling listenforMcsEvent.");
      mcsListener.listenforMcsEvent();      
      logger.info("calling publishEvent.");
      evtPublisher.publishEvent(null);
      logger.info("Going into sleep.");
      try {
        Thread.sleep(pollTime);
      } catch (InterruptedException ex) {
        // TODO Auto-generated catch block
        ex.printStackTrace();
        logger.severe(ex.getMessage());
      }

    }
    
    cmdListener.stop();
    evtListener.stop();
    evtPublisher.stop();
    mcsListener.stop();
    CommandExecutor.getInstance().stop();
    
    if (context != null) {
      context.term();
    }

  }

}
