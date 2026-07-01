package org.dlof.lib.content;

import org.dlof.lib.characters.YimeRoster;

/**
 * محتوى ملف .dlof من نوع domain="characters": يغلّف {@link YimeRoster}
 * كي يُكتب داخل <content><characters>...</characters></content>.
 */
public record CharactersContent(YimeRoster roster) implements DlofContent {
}
