package com.tmt.tcs.mcs.mcsHcdPrototype;

/**
 * The enum represents possible status from MCS command operations.
 * 
 * @author E401581.
 *
 */
enum StatusCodes {
    // Success
    Success,

    // Command not valid in current MCS state
    IllegalState,

    // MCS busy executing some other command
    Busy,

    // One or more parameters are out of range
    OutofRange,

    // MCS did not respond in time
    Timeout,

    // MCS failed to execute the command
    Failure,

    // The command or parameter is not supported
    OutofSpec
}
