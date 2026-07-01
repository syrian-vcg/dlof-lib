package org.dlof.lib;

import org.dlof.lib.model.DlofDocument;
import org.dlof.lib.parser.DlofParser;
import org.dlof.lib.pkg.DlofPackageBuilder;
import org.dlof.lib.pkg.DlofSeriesBuilder;
import org.dlof.lib.writer.DlofWriter;

import java.io.IOException;
import java.nio.file.Path;

/**
 * نقطة دخول موحّدة لمكتبة dlof — اقرأ، اكتب، وابنِ حزم .dlofpkg / .dlofSeries
 * من جافا أو كوتلن بدون أي اعتماديات خارجية (JDK فقط).
 *
 * أمثلة:
 * <pre>{@code
 * DlofDocument doc = Dlof.parse(Path.of("ep01.dlof"));
 * Dlof.write(doc, Path.of("out/ep01.dlof"));
 *
 * new DlofPackageBuilder(doc).buildTo(Path.of("out/ep01.dlofpkg"));
 * }</pre>
 */
public final class Dlof {
    private Dlof() {
    }

    public static DlofDocument parse(Path path) throws IOException {
        return DlofParser.parse(path);
    }

    public static DlofDocument parse(String xml) {
        return DlofParser.parse(xml);
    }

    public static String toXml(DlofDocument document) {
        return DlofWriter.toXml(document);
    }

    public static void write(DlofDocument document, Path outputPath) throws IOException {
        DlofWriter.writeToFile(document, outputPath);
    }

    public static DlofPackageBuilder packageBuilder(DlofDocument document) {
        return new DlofPackageBuilder(document);
    }

    public static DlofSeriesBuilder seriesBuilder(String seriesFolderName) {
        return new DlofSeriesBuilder(seriesFolderName);
    }
}
