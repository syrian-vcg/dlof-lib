package org.dlof.lib.model;

import java.util.List;
import java.util.Collections;

/** البيانات الوصفية لملف .dlof: العنوان، المجال، المؤلف، الوسوم... */
public record Metadata(
        String title,
        Domain domain,
        String author,
        String createdAt,
        String updatedAt,
        String language,
        List<String> tags
) {
    public Metadata {
        if (language == null) language = "ar";
        if (tags == null) tags = Collections.emptyList();
    }

    public Metadata(String title, Domain domain) {
        this(title, domain, null, null, null, "ar", Collections.emptyList());
    }
}
