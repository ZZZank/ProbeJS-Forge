package com.probejs.rewrite.doc.type.typescript;

import com.probejs.rewrite.doc.type.DocType;

/**
 * aka TypeOr, "string | number"
 *
 * @author ZZZank
 */
public record TypeUnion(DocType left, DocType right) implements TSType {
}
