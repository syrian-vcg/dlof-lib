package org.dlof.lib.characters;

public enum CharacterStatus {
    ALIVE("alive", "حي"),
    DECEASED("deceased", "متوفى"),
    MISSING("missing", "مفقود"),
    UNKNOWN("unknown", "مجهول"),
    INACTIVE("inactive", "خارج الأحداث");

    public final String xmlValue;
    public final String arabicLabel;

    CharacterStatus(String xmlValue, String arabicLabel) {
        this.xmlValue = xmlValue;
        this.arabicLabel = arabicLabel;
    }

    public static CharacterStatus fromXml(String v) {
        for (CharacterStatus s : values()) if (s.xmlValue.equals(v)) return s;
        return ALIVE;
    }
}
