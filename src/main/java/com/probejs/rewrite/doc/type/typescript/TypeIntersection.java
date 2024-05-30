package com.probejs.rewrite.doc.type.typescript;

import com.github.bsideup.jabel.Desugar;
import com.probejs.rewrite.doc.type.DocType;

/**
 * aka TypeAnd, "string & number"
 * @author ZZZank
 */
@Desugar
public record TypeIntersection(DocType left, DocType right) implements TSType {
}
