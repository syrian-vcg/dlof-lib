package org.dlof.lib.content;

/** سطر حوار واحد داخل لوحة قصة مصورة (comicPanel). */
public record PanelDialogue(String characterId, String characterName, String text, DialogueType type) {
    public PanelDialogue {
        if (type == null) type = DialogueType.SPEECH;
    }

    public PanelDialogue(String characterName, String text) {
        this(null, characterName, text, DialogueType.SPEECH);
    }
}
