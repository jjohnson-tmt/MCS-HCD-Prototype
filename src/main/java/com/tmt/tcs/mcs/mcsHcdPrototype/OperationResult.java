package com.tmt.tcs.mcs.mcsHcdPrototype;

/**
 * Represents return type for MCSHCD methods.
 * 
 * @author vkhairnar. Represents result for all MCS related operations.
 */
class OperationResult {
  /**
   * Contains error message returned by MCS in case of error.
   */
  private String errorInfo;

  /**
   * Contains TAI timestamp when the command was processed and response was
   * generated at MCS.
   */
  private double timeWhenProcessed;

  /**
   * Contains command execution status returned by MCS.
   */
  private StatusCodes status;

  /**
   * Constructor for the class.
   * 
   * @param error
   *            string error returned by MCS in error_state field of response.
   * @param processedTime
   *            TAI time returned by MCS in time field of response.
   * @param code
   *            One of the StatusCodes enum value.
   */
  OperationResult(String error, double processedTime, StatusCodes code) {
    errorInfo = error;
    timeWhenProcessed = processedTime;
    status = code;
  }

  /**
   * Gets errorInfo as returned by MCS.
   * 
   * @return errorInfo as String.
   */
  public String getErrorInfo() {
    return errorInfo;
  }

  /**
   * Gets timeWhenProcessed as returned by MCS.
   * 
   * @return time when processed by MCS as double.
   */
  public double getTimeWhenProcessed() {
    return timeWhenProcessed;
  }

  /**
   * Gets status.
   * 
   * @return status codes for communication with MCS.
   */
  public StatusCodes getStatus() {
    return status;
  }
}
