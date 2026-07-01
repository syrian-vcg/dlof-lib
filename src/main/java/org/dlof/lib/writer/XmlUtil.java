package org.dlof.lib.writer;

/** أدوات تهريب (escaping) نصوص XML بسيطة بدون أي اعتماديات خارجية. */
public final class XmlUtil {
    private XmlUtil() {
    }

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&apos;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String escapeAttr(String s) {
        return escape(s);
    }
}
