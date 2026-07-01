package org.dlof.lib.parser;

/** استثناء يُرمى عند فشل تحليل ملف .dlof (بنية غير صالحة، عنصر مفقود، نوع غير مدعوم...). */
public class DlofParseException extends RuntimeException {
    public DlofParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
