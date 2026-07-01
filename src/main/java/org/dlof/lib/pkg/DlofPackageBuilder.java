package org.dlof.lib.pkg;

import org.dlof.lib.model.DlofDocument;
import org.dlof.lib.writer.DlofWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * يبني حزمة ملف dlof فردي بامتداد .dlofpkg:
 * package.dlof + meta.json + attachments/ (اختياري)
 * راجع spec/PACKAGE_FORMATS.md.
 */
public final class DlofPackageBuilder {

    private final DlofDocument document;
    private PackageMeta meta;
    private Map<String, byte[]> attachments = Map.of();

    public DlofPackageBuilder(DlofDocument document) {
        this.document = document;
        this.meta = new PackageMeta(document.id(), document.metadata().title(),
                document.metadata().domain().xmlValue, document.version(), document.metadata().author(),
                document.metadata().language(), document.metadata().createdAt(), "1.0");
    }

    public DlofPackageBuilder withMeta(PackageMeta meta) {
        this.meta = meta;
        return this;
    }

    /** ملفات إضافية توضع داخل attachments/ (اسم الملف -> محتواه الخام). */
    public DlofPackageBuilder withAttachmentFiles(Map<String, byte[]> attachments) {
        this.attachments = attachments;
        return this;
    }

    public void buildTo(Path outputDlofpkg) throws IOException {
        Files.createDirectories(outputDlofpkg.toAbsolutePath().getParent());
        try (OutputStream fos = Files.newOutputStream(outputDlofpkg);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            zos.putNextEntry(new ZipEntry("package.dlof"));
            zos.write(DlofWriter.toXml(document).getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("meta.json"));
            zos.write(meta.toJson().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            for (Map.Entry<String, byte[]> e : attachments.entrySet()) {
                zos.putNextEntry(new ZipEntry("attachments/" + e.getKey()));
                zos.write(e.getValue());
                zos.closeEntry();
            }
        }
    }
}
