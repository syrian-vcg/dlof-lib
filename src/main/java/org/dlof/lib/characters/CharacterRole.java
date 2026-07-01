package org.dlof.lib.characters;

public enum CharacterRole {
    PROTAGONIST("protagonist", "بطل رئيسي"),
    ANTAGONIST("antagonist", "شرير / خصم"),
    SUPPORTING("supporting", "شخصية داعمة"),
    MENTOR("mentor", "المرشد"),
    COMIC_RELIEF("comicRelief", "الشخصية الكوميدية"),
    LOVE_INTEREST("loveInterest", "الاهتمام العاطفي"),
    MYSTERY("mystery", "شخصية غامضة"),
    NARRATOR("narrator", "الراوي"),
    MINOR("minor", "شخصية ثانوية");

    public final String xmlValue;
    public final String arabicLabel;

    CharacterRole(String xmlValue, String arabicLabel) {
        this.xmlValue = xmlValue;
        this.arabicLabel = arabicLabel;
    }

    public static CharacterRole fromXml(String v) {
        for (CharacterRole r : values()) if (r.xmlValue.equals(v)) return r;
        return SUPPORTING;
    }
}
