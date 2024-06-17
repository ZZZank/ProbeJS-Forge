package com.probejs.document;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class DocumentProperty implements IConcrete {
    protected DocComment comment;

    @Override
    public void acceptDeco(List<IDecorative> decorates) {
        for (IDecorative decorative : decorates) {
            if (decorative instanceof DocComment) {
                this.comment = (DocComment) decorative;
                break;
            }
        }
    }
}
