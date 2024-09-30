package zzzank.probejs.lang.typescript.refer;

import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public final class Reference {
    public final ImportInfo info;
    public final String deduped;

    public String getImport() {
        return info.toImport(deduped);
    }
}
