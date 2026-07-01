package org.dlof.lib.characters;

import java.util.Collections;
import java.util.List;

/**
 * فهرس الشخصيات الكامل لسلسلة أو قصة مصورة.
 * يُخزَّن عادةً في ملف .dlof منفصل من نوع domain="characters"
 * كمحتوى GenericItem بـ customType="characters".
 */
public record YimeRoster(String loopId, String seriesTitle, List<Yime> characters, String relationshipMapNotes) {
    public YimeRoster {
        if (characters == null) characters = Collections.emptyList();
    }
}
