package org.dlof.lib.content;

/** سؤال وإجابة (education). */
public record QaItem(String question, String answer, String explanation, String difficulty) implements DlofContent {
}
