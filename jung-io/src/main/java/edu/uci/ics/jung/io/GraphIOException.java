/*
 * Copyright (c) 2008, the JUNG Project and the Regents of the University
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */

package edu.uci.ics.jung.io;

/**
 * Exception thrown when IO errors occur when reading/writing graphs.
 *
 * @author Nathan Mittler - nathan.mittler@gmail.com
 */
public class GraphIOException extends Exception {

    private static final long serialVersionUID = 3773882099782535606L;

    public GraphIOException() {
        super();
    }

    public GraphIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphIOException(String message) {
        super(message);
    }

    public GraphIOException(Throwable cause) {
        super(cause);
    }

}
