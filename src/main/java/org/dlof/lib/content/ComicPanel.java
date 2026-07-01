package org.dlof.lib.content;

import java.util.Collections;
import java.util.List;

/** لوحة واحدة من قصة مصورة/مانغا/رواية مصورة (domain=comic). */
public record ComicPanel(
        Integer panelNumber,
        Integer pageNumber,
        String caption,
        List<PanelDialogue> dialogue,
        String imageAttachmentRef,
        String altText,
        Integer panelWidth,
        String backgroundColor
) implements DlofContent {
    public ComicPanel {
        if (dialogue == null) dialogue = Collections.emptyList();
    }
}
