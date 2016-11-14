package com.tmt.tcs.mcs.tcsToMcsEvents;

import com.tmt.tcs.mcs.protos.TcsMcsEventsProtos.TcsPositionDemandEvent;

/**
 * @author E401581
 *     {@link TcsEvent} implementation for position demand event. 
 */
public final class PositionDemandEvent extends TcsEvent {

  /**
   * Config property name for positiondemand event identifier.
   */
  private static final String POSITION_DEMAND_EVENT_NAME_KEY = "positiondemandeventname";
  
  /**
   * Azimuth for demanded position.
   */
  private double az = 0.0;
  
  /**
   * Elevation for demanded position.
   */
  private double el = 0.0;
  
  /**
   * Time for demanded position.
   */
  private double time = 2222.0;
  
  /**
   * Singleton instance of PositionDemandEvent.
   */
  private static PositionDemandEvent instance = null;
  
  /**
   * Initializes new instance of PositionDemandEvent class.
   */
  private PositionDemandEvent() {
    super(POSITION_DEMAND_EVENT_NAME_KEY);
  }
  
  /**
   * Gets singleton instance of PositionDemandEvent class.
   * @return object of PositionDemandEvent.
   */
  public static PositionDemandEvent getInstance() {
    if (instance == null) {
      instance = new PositionDemandEvent();
    }
    
    return instance;
  }
  
  /**
   * Sets azimuth for the demanded position.
   * @param az azimuth in double.
   */
  public void setAzimuth(double az) {
    this.az = az;
  }
  
  /**
   * Sets elevation for the demanded position.
   * @param el elevation in double.
   */
  public void setElevation(double el) {
    this.el = el;
  }
  
  /**
   * Sets time for the demanded position.
   * @param time in double.
   */
  public void setTime(double time) {
    this.time = time;
  }

  /* (non-Javadoc)
   * @see com.tmt.tcs.mcs.tcsToMcsEvents.TcsEvent#encode()
   */
  @Override
  public byte[] encode() {
    // Create proto object for position demand
    TcsPositionDemandEvent tcsPositionDemandEvent = TcsPositionDemandEvent
        .newBuilder().setAzimuth(az).setElevation(el).setTime(time).build();
    
    return tcsPositionDemandEvent.toByteArray();    
  }
}