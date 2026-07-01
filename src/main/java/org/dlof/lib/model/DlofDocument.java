package org.dlof.lib.model;

import org.dlof.lib.content.DlofContent;

import java.util.Collections;
import java.util.List;

/** يمثّل ملف DLoF كاملاً (.dlof) — عنصر <documentLoop> بعد التحليل أو قبل الكتابة. */
public record DlofDocument(
        String id,
        String version,
        Metadata metadata,
        LoopLinks loopLinks,
        DlofContent content,
        List<Attachment> attachments,
        Template template
) {
    public DlofDocument {
        if (version == null) version = "1.0";
        if (loopLinks == null) loopLinks = LoopLinks.EMPTY;
        if (attachments == null) attachments = Collections.emptyList();
    }

    /** بناء مختصر بدون مرفقات ولا قالب. */
    public static DlofDocument of(String id, Metadata metadata, LoopLinks loopLinks, DlofContent content) {
        return new DlofDocument(id, "1.0", metadata, loopLinks, content, Collections.emptyList(), null);
    }
}
