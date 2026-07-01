package org.dlof.lib.characters;

/** علاقة بين شخصيتين: targetId يشير إلى Yime.id شخصية أخرى في نفس الحلقة. */
public record CharacterRelation(String targetId, RelationType type, String label) {
    public CharacterRelation {
        if (type == null) type = RelationType.ACQUAINTANCE;
    }
}
