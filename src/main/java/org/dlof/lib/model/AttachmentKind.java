package org.dlof.lib.model;

public enum AttachmentKind {
    IMAGE("image", "صورة"),
    VIDEO("video", "فيديو"),
    FILE("file", "ملف"),
    SUBTITLE("subtitle", "ترجمة");

    public final String xmlValue;
    public final String arabicLabel;

    AttachmentKind(String xmlValue, String arabicLabel) {
        this.xmlValue = xmlValue;
        this.arabicLabel = arabicLabel;
    }

    public static AttachmentKind fromXml(String value) {
        for (AttachmentKind k : values()) {
            if (k.xmlValue.equals(value)) return k;
        }
        return FILE;
    }

    public static AttachmentKind fromMimeType(String mime) {
        if (mime == null) return FILE;
        if (mime.startsWith("image/")) return IMAGE;
        if (mime.startsWith("video/")) return VIDEO;
        if (mime.equals("text/srt") || mime.equals("text/vtt") || mime.equals("application/x-subrip")) return SUBTITLE;
        return FILE;
    }
}
