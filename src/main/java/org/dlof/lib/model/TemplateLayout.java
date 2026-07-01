package org.dlof.lib.model;

public enum TemplateLayout {
    STANDARD("standard", "عادي"),
    CARD("card", "بطاقة"),
    MAGAZINE("magazine", "مجلة"),
    MINIMAL("minimal", "مبسّط");

    public final String xmlValue;
    public final String arabicLabel;

    TemplateLayout(String xmlValue, String arabicLabel) {
        this.xmlValue = xmlValue;
        this.arabicLabel = arabicLabel;
    }

    public static TemplateLayout fromXml(String value) {
        for (TemplateLayout t : values()) {
            if (t.xmlValue.equals(value)) return t;
        }
        return STANDARD;
    }
}
