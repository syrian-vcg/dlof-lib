package org.dlof.lib.model;

/** روابط الحلقة الذاتية: السابق، التالي، وهل هذا الملف جذر الحلقة. */
public record LoopLinks(LinkRef previous, LinkRef next, boolean loopRoot) {
    public static final LoopLinks EMPTY = new LoopLinks(null, null, false);
}
