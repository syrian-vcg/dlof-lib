package org.dlof.lib.content;

/**
 * واجهة علامة (marker interface) لكل أنواع محتوى .dlof المدعومة:
 * GenericItem, QaItem, BookChapter, TermDefinition, InfoExplain,
 * EpisodeItem (مسلسل/سلسلة)، ComicPanel (قصة مصورة).
 */
public sealed interface DlofContent
        permits GenericItem, QaItem, BookChapter, TermDefinition, InfoExplain, EpisodeItem, ComicPanel, CharactersContent {
}
