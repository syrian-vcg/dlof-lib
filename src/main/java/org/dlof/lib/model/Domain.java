package org.dlof.lib.model;

/** المجالات المدعومة في DLoF — يقابل enum Domain في تطبيق Kotlin الأصلي. */
public enum Domain {
    EDUCATION("education", "تعليم"),
    BOOK("book", "كتاب"),
    INFO_APP("infoApp", "تطبيق معلومات"),
    INFO_LOOP("infoLoop", "حلقة معلومات"),
    RECIPE("recipe", "وصفة طعام"),
    JOURNAL("journal", "يوميات"),
    SERIES("series", "مسلسل / سلسلة"),
    COMIC("comic", "قصة مصورة"),
    PODCAST("podcast", "بودكاست"),
    CHARACTERS("characters", "شخصيات"),
    CUSTOM("custom", "مخصص");

    public final String xmlValue;
    public final String arabicLabel;

    Domain(String xmlValue, String arabicLabel) {
        this.xmlValue = xmlValue;
        this.arabicLabel = arabicLabel;
    }

    public static Domain fromXml(String value) {
        for (Domain d : values()) {
            if (d.xmlValue.equals(value)) return d;
        }
        return CUSTOM;
    }
}
