package com.probejs.rewrite.doc.type.typescript;

import com.probejs.rewrite.doc.type.DocType;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * @author ZZZank
 */
@AllArgsConstructor
public class TypeObject implements TSType {
    private final Map<String, ? extends DocType> map;
}
