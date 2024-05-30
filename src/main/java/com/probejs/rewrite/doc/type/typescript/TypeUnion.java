package com.probejs.rewrite.doc.type.typescript;

import com.github.bsideup.jabel.Desugar;
import com.probejs.rewrite.doc.type.DocType;

/**
 * aka TypeOr, "string | number"
 *
 * @author ZZZank
 */
@Desugar
public record TypeUnion(DocType left, DocType right) implements TSType {
}
