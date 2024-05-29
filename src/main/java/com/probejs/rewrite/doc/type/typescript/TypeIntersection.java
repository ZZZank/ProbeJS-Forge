package com.probejs.rewrite.doc.type.typescript;

import com.probejs.rewrite.doc.type.DocType;

/**
 * aka TypeAnd, "string & number"
 * @author ZZZank
 */
public record TypeIntersection(DocType left, DocType right) implements TSType {
}
