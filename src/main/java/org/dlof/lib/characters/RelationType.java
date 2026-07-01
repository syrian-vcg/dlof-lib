package org.dlof.lib.characters;

public enum RelationType {
    ALLY("ally", "حليف"),
    ENEMY("enemy", "عدو"),
    FAMILY("family", "عائلة"),
    FRIEND("friend", "صديق"),
    RIVAL("rival", "منافس"),
    MENTOR("mentor", "مرشد"),
    STUDENT("student", "تلميذ"),
    LOVE("love", "علاقة عاطفية"),
    ACQUAINTANCE("acquaintance", "معرفة");

    public final String xmlValue;
    public final String arabicLabel;

    RelationType(String xmlValue, String arabicLabel) {
        this.xmlValue = xmlValue;
        this.arabicLabel = arabicLabel;
    }

    public static RelationType fromXml(String v) {
        for (RelationType r : values()) if (r.xmlValue.equals(v)) return r;
        return ACQUAINTANCE;
    }
}
