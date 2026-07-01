package org.dlof.lib.model;

/**
 * مرفق (صورة/فيديو/ملف) داخل .dlof — إمّا مضمَّن base64 عبر data
 * أو بإشارة خارجية عبر uri (مثل مسار داخل مجلد media/).
 */
public record Attachment(
        String id,
        String fileName,
        String mimeType,
        AttachmentKind kind,
        String data,
        String uri,
        Long sizeBytes,
        String caption
) {
}
