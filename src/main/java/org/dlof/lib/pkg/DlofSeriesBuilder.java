package org.dlof.lib.pkg;

import org.dlof.lib.model.DlofDocument;
import org.dlof.lib.writer.DlofWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * يبني حزمة سلسلة كاملة بامتداد .dlofSeries (مسلسل أو قصة مصورة):
 * <pre>
 * MySeries.dlofSeries
 * └── MySeries/
 *     ├── series-index.dlof   (loopRoot=true)
 *     ├── ep01.dlof / ch01.dlof ...
 *     ├── characters.dlof      (اختياري)
 *     ├── set.txt               (اختياري)
 *     ├── fonts/*.ttf           (اختياري)
 *     └── media/...             (اختياري)
 * </pre>
 * راجع spec/PACKAGE_FORMATS.md.
 */
public final class DlofSeriesBuilder {

    private final String seriesFolderName;
    private DlofDocument seriesIndex;
    private final Map<String, DlofDocument> episodes = new LinkedHashMap<>(); // fileName -> doc
    private DlofDocument characters;
    private String setTxt;
    private final Map<String, byte[]> extraFiles = new LinkedHashMap<>(); // relative path -> bytes

    public DlofSeriesBuilder(String seriesFolderName) {
        this.seriesFolderName = seriesFolderName;
    }

    public DlofSeriesBuilder withSeriesIndex(DlofDocument doc) {
        this.seriesIndex = doc;
        return this;
    }

    public DlofSeriesBuilder addEpisode(String fileName, DlofDocument doc) {
        this.episodes.put(fileName, doc);
        return this;
    }

    public DlofSeriesBuilder withCharacters(DlofDocument charactersDoc) {
        this.characters = charactersDoc;
        return this;
    }

    public DlofSeriesBuilder withSetTxt(String setTxtContent) {
        this.setTxt = setTxtContent;
        return this;
    }

    /** ملف إضافي (خط، صورة، فيديو...) بمسار نسبي داخل مجلد السلسلة، مثل "fonts/Cairo-Bold.ttf". */
    public DlofSeriesBuilder addFile(String relativePath, byte[] content) {
        this.extraFiles.put(relativePath, content);
        return this;
    }

    public void buildTo(Path outputDlofSeries) throws IOException {
        Files.createDirectories(outputDlofSeries.toAbsolutePath().getParent());
        try (OutputStream fos = Files.newOutputStream(outputDlofSeries);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            if (seriesIndex != null) writeXmlEntry(zos, "series-index.dlof", seriesIndex);
            for (Map.Entry<String, DlofDocument> e : episodes.entrySet()) {
                writeXmlEntry(zos, e.getKey(), e.getValue());
            }
            if (characters != null) writeXmlEntry(zos, "characters.dlof", characters);
            if (setTxt != null) {
                zos.putNextEntry(new ZipEntry(seriesFolderName + "/set.txt"));
                zos.write(setTxt.getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
            for (Map.Entry<String, byte[]> e : extraFiles.entrySet()) {
                zos.putNextEntry(new ZipEntry(seriesFolderName + "/" + e.getKey()));
                zos.write(e.getValue());
                zos.closeEntry();
            }
        }
    }

    private void writeXmlEntry(ZipOutputStream zos, String fileName, DlofDocument doc) throws IOException {
        zos.putNextEntry(new ZipEntry(seriesFolderName + "/" + fileName));
        zos.write(DlofWriter.toXml(doc).getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    /** أداة مستقلة: ضغط مجلد كامل موجود على القرص إلى ملف .dlofSeries (مطابق لـ `zip -r`). */
    public static void zipDirectory(Path sourceDir, Path outputZip) throws IOException {
        Files.createDirectories(outputZip.toAbsolutePath().getParent());
        try (OutputStream fos = Files.newOutputStream(outputZip);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            Path root = sourceDir.getParent() != null ? sourceDir.getParent() : sourceDir;
            Files.walk(sourceDir).filter(Files::isRegularFile).forEach(p -> {
                try {
                    String entryName = root.relativize(p).toString().replace('\\', '/');
                    zos.putNextEntry(new ZipEntry(entryName));
                    zos.write(Files.readAllBytes(p));
                    zos.closeEntry();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }
}
