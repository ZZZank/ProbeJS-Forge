package com.probejs.document.event;

import com.probejs.document.DocClass;
import com.probejs.document.DocTypeAssign;
import com.probejs.document.type.DocType;
import dev.latvian.kubejs.event.EventJS;
import lombok.AllArgsConstructor;

/**
 * @author ZZZank
 */
@AllArgsConstructor
public class DocAddEvent extends EventJS {

    public DocClass targetClass(String name) {
        return null;
    }

    public DocTypeAssign addType(String name, DocType assignTo) {
        return new DocTypeAssign(name, assignTo);
    }
}
