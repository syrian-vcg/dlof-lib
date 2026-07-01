package org.dlof.lib.content;

/** بنية عامة مرنة لأي استخدام لم يُحدَّد له نوع فرعي، أو لتمثيل customType مثل "characters". */
public record GenericItem(String type, String element, String body, String customType) implements DlofContent {
}
