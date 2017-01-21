package com.example.grigorii.mindthegap.utility.exceptions;

/**
 * Created by grigorii on 11/06/16.
 * Exception to be thrown when parsing of branches is impossible
 */
public class MalformedLatLonSequenceException extends Throwable {
    public MalformedLatLonSequenceException() {
    }

    public MalformedLatLonSequenceException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }
}
