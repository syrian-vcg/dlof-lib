package org.dlof.lib.characters;

import java.util.Collections;
import java.util.List;

/**
 * شخصية واحدة (Character/Yime) داخل حلقة سردية — مسلسل، قصة مصورة، أو أي محتوى سردي.
 * تُجمَع قائمة من Yime داخل YimeRoster لتمثيل عالم القصة كاملاً.
 */
public record Yime(
        String id,
        String name,
        CharacterRole role,
        String alias,
        Integer age,
        CharacterGender gender,
        String description,
        String appearance,
        String personality,
        String backstory,
        String goals,
        String conflicts,
        List<CharacterRelation> relationships,
        List<String> appearsIn,
        String avatarAttachmentRef,
        String avatarBase64,
        List<String> tags,
        String authorNotes,
        CharacterStatus status
) {
    public Yime {
        if (role == null) role = CharacterRole.SUPPORTING;
        if (gender == null) gender = CharacterGender.UNSPECIFIED;
        if (description == null) description = "";
        if (relationships == null) relationships = Collections.emptyList();
        if (appearsIn == null) appearsIn = Collections.emptyList();
        if (tags == null) tags = Collections.emptyList();
        if (status == null) status = CharacterStatus.ALIVE;
    }

    /** بناء مختصر: معرّف، اسم، دور فقط — بقية الحقول اختيارية عبر withX(...) لاحقاً. */
    public static Yime of(String id, String name, CharacterRole role) {
        return new Yime(id, name, role, null, null, CharacterGender.UNSPECIFIED, "", null, null,
                null, null, null, Collections.emptyList(), Collections.emptyList(), null, null,
                Collections.emptyList(), null, CharacterStatus.ALIVE);
    }
}
