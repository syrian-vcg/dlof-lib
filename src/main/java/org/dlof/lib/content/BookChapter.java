package org.dlof.lib.content;

/** فصل كتاب (book). */
public record BookChapter(Integer chapterNumber, String chapterTitle, String text, String summary) implements DlofContent {
}
