package com.probejs.document.parser.handler;

import com.probejs.document.parser.processor.DocumentHandler;
import java.util.function.BiFunction;

/**
 * accepts one line from documents, and the current state of document reader (DocumentHandler),
 * then return another doc state handler.
 * <p>
 * returned value can be null, if you think this state handler has finished its job.
 */
public interface IMultiHandler extends BiFunction<String, DocumentHandler, IStateHandler<String>> {}
