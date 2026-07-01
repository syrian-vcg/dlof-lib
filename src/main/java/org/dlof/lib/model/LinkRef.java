package org.dlof.lib.model;

/** مرجع رابط داخل الحلقة (previous/next): اسم الملف + عنوان اختياري. */
public record LinkRef(String ref, String title) {
    public LinkRef(String ref) {
        this(ref, null);
    }
}
