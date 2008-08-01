/*
 * Copyright (c) 2008, the JUNG Project and the Regents of the University
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */

package edu.uci.ics.jung.io.graphml.parser;

import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import edu.uci.ics.jung.io.GraphIOException;

/**
 * Base class for element parsers - provides some minimal functionality.
 * 
 * @author Nathan Mittler - nathan.mittler@gmail.com
 */
public abstract class AbstractElementParser implements ElementParser {

    final private ElementParserRegistry parserRegistry;
    protected AbstractElementParser(ElementParserRegistry parserRegistry) {
        this.parserRegistry = parserRegistry;
    }
    
    public ElementParserRegistry getParserRegistry() {
        return this.parserRegistry;
    }
    
    public ElementParser getParser(String localName) {
        return parserRegistry.getParser(localName);
    }
    
    public ElementParser getUnknownParser() {
        return parserRegistry.getUnknownElementParser();
    }
    
    protected void verifyMatch(StartElement start, EndElement end)
            throws GraphIOException {

        String startName = start.getName().getLocalPart();
        String endName = end.getName().getLocalPart();
        if (!startName.equals(endName)) {
            throw new GraphIOException(
                    "Failed parsing document: Start/end tag mismatch! "
                            + "StartTag:" + startName + ", EndTag: "
                            + endName);
        }
    }
}
