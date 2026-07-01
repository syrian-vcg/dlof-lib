package org.dlof.lib.content;

public enum DialogueType {
    SPEECH("speech", "كلام"),
    THOUGHT("thought", "تفكير"),
    NARRATION("narration", "راوٍ"),
    WHISPER("whisper", "همس"),
    SHOUT("shout", "صراخ");

    public final String xmlValue;
    public final String arabicLabel;

    DialogueType(String xmlValue, String arabicLabel) {
        this.xmlValue = xmlValue;
        this.arabicLabel = arabicLabel;
    }

    public static DialogueType fromXml(String value) {
        for (DialogueType d : values()) {
            if (d.xmlValue.equals(value)) return d;
        }
        return SPEECH;
    }
}
