package org.dlof.lib.characters;

public enum CharacterGender {
    MALE("male", "ذكر"),
    FEMALE("female", "أنثى"),
    NON_BINARY("nonBinary", "غير ثنائي"),
    UNSPECIFIED("unspecified", "غير محدد");

    public final String xmlValue;
    public final String arabicLabel;

    CharacterGender(String xmlValue, String arabicLabel) {
        this.xmlValue = xmlValue;
        this.arabicLabel = arabicLabel;
    }

    public static CharacterGender fromXml(String v) {
        for (CharacterGender g : values()) if (g.xmlValue.equals(v)) return g;
        return UNSPECIFIED;
    }
}
