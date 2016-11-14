package com.tmt.tcs.mcs.mcsToTcsEvents;

import com.tmt.tcs.mcs.protos.TcsMcsEventsProtos.McsCurrentPositionEvent;

/**
 * @author E401581.
 *     {@link McsEvent} implementation for current position event received from MCS.
 */
public final class CurrentPositionEvent extends McsEvent {
  
  /**
   * Config property name for TCS events topic name.
   */
  private static final String CURRENT_POSITION_EVENT_NAME_KEY = "currentpositioneventname";

  /**
   * @param mcsCurrentPositionEvent {@link McsCurrentPositionEvent}.
   */
  public CurrentPositionEvent(McsCurrentPositionEvent mcsCurrentPositionEvent) {
    super(mcsCurrentPositionEvent, CURRENT_POSITION_EVENT_NAME_KEY);
  }
}