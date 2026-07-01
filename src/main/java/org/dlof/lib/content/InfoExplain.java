package org.dlof.lib.content;

/** شرح معلومة (infoApp). */
public record InfoExplain(String topic, String explanation, String source) implements DlofContent {
}
