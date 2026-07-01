package org.dlof.lib.pkg;

/** يقابل meta.json داخل حزمة .dlofpkg (راجع spec/PACKAGE_FORMATS.md). */
public record PackageMeta(
        String id,
        String title,
        String domain,
        String version,
        String author,
        String language,
        String createdAt,
        String dlofpkgVersion
) {
    public PackageMeta {
        if (version == null) version = "1.0";
        if (dlofpkgVersion == null) dlofpkgVersion = "1.0";
    }

    public String toJson() {
        return new MiniJson()
                .put("id", id)
                .put("title", title)
                .put("domain", domain)
                .put("version", version)
                .put("author", author)
                .put("language", language)
                .put("createdAt", createdAt)
                .put("dlofpkg_version", dlofpkgVersion)
                .toJson();
    }
}
