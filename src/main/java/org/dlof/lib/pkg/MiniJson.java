package org.dlof.lib.pkg;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * كاتب JSON صغير جداً بدون أي اعتماديات خارجية — يكفي لملفات meta.json
 * البسيطة (سلاسل نصية فقط). ليس محلّلاً عاماً؛ فقط أداة كتابة (writer).
 */
public final class MiniJson {
    private final Map<String, String> fields = new LinkedHashMap<>();

    public MiniJson put(String key, String value) {
        if (value != null) fields.put(key, value);
        return this;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{\n");
        int i = 0;
        int n = fields.size();
        for (Map.Entry<String, String> e : fields.entrySet()) {
            sb.append("  \"").append(escape(e.getKey())).append("\": \"").append(escape(e.getValue())).append('"');
            if (++i < n) sb.append(',');
            sb.append('\n');
        }
        sb.append("}\n");
        return sb.toString();
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
