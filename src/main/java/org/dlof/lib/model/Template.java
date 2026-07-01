package org.dlof.lib.model;

/** قالب تصميم مستقل لكل ملف .dlof: ألوان، خط، وتخطيط عرض. */
public record Template(
        String ref,
        String primaryColor,
        String secondaryColor,
        String backgroundColor,
        String textColor,
        String fontFamily,
        TemplateLayout layout,
        String headerAttachmentRef
) {
    public Template {
        if (layout == null) layout = TemplateLayout.STANDARD;
    }
}
