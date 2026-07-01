package org.dlof.lib.demo;

import org.dlof.lib.Dlof;
import org.dlof.lib.characters.*;
import org.dlof.lib.content.*;
import org.dlof.lib.model.*;
import org.dlof.lib.pkg.DlofSeriesBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * مثال تشغيلي كامل (جافا فقط) يوضّح استخدام مكتبة dlof-lib لبناء:
 *   1) قصة مصورة (comic)  — صفحتان مترابطتان بحلقة (loop)
 *   2) مسلسل (series)     — حلقتان + ملف شخصيات (characters)
 *   3) تصدير حزمة .dlofpkg لملف واحد، وحزمة .dlofSeries للمسلسل كاملاً
 *
 * تشغيل بدون Gradle (JDK 17+ فقط):
 *   javac -d out $(find ../src/main/java -name "*.java") $(find . -name "*.java")
 *   java -cp out org.dlof.lib.demo.Main
 */
public final class Main {

    public static void main(String[] args) throws IOException {
        Path outDir = Path.of("demo-output");

        // ── 1) قصة مصورة: صفحتان ─────────────────────────────
        DlofDocument comicP1 = buildComicPage(
                "mycomic-p01", "الصفحة الأولى", null, "p02.dlof", true,
                "سماء المدينة عند الفجر", List.of(
                        new PanelDialogue(null, "سارة", "أخيراً وصلنا إلى مدينة النور!", DialogueType.SPEECH),
                        new PanelDialogue(null, null, "بعد رحلة طويلة عبر الصحراء...", DialogueType.NARRATION)
                ));
        DlofDocument comicP2 = buildComicPage(
                "mycomic-p02", "الصفحة الثانية", "p01.dlof", null, false,
                "سارة تدخل البوابة الكبرى", List.of(
                        new PanelDialogue(null, "عمر", "احذري، البوابة محروسة!", DialogueType.SHOUT),
                        new PanelDialogue(null, "سارة", "لن نتراجع الآن.", DialogueType.SPEECH)
                ));

        Dlof.write(comicP1, outDir.resolve("MyComic/p01.dlof"));
        Dlof.write(comicP2, outDir.resolve("MyComic/p02.dlof"));
        Dlof.packageBuilder(comicP1).buildTo(outDir.resolve("p01.dlofpkg"));

        // ── 2) مسلسل: حلقتان ──────────────────────────────────
        DlofDocument ep01 = buildEpisode("myseries-ep01", 1, 1, "البداية", null, "ep02.dlof", true,
                "تعريف بعالم القصة وأبطالها الرئيسيين في مدينة المستقبل.");
        DlofDocument ep02 = buildEpisode("myseries-ep02", 2, 1, "التطور", "ep01.dlof", null, false,
                "الفريق يكتشف أول أدلة اللغز الكبير.");

        // ملف شخصيات المسلسل (characters.dlof)
        YimeRoster roster = new YimeRoster("myseries-characters", "سلسلتي", List.of(
                Yime.of("sara", "سارة", CharacterRole.PROTAGONIST),
                Yime.of("omar", "عمر", CharacterRole.SUPPORTING)
        ), null);
        DlofDocument charactersDoc = DlofDocument.of(
                "myseries-characters",
                new Metadata("سلسلتي — الشخصيات", Domain.CHARACTERS, "المؤلف", null, null, "ar", List.of()),
                LoopLinks.EMPTY,
                new CharactersContent(roster));

        // series-index.dlof (جذر الحلقة)
        DlofDocument seriesIndex = DlofDocument.of(
                "myseries-index",
                new Metadata("سلسلتي", Domain.SERIES, "المؤلف", null, null, "ar", List.of("سلسلة")),
                new LoopLinks(null, new LinkRef("ep01.dlof", "الحلقة الأولى: البداية"), true),
                new GenericItem("index", "seriesIndex", "فهرس سلسلتي", "seriesIndex"));

        new DlofSeriesBuilder("MySeries")
                .withSeriesIndex(seriesIndex)
                .addEpisode("ep01.dlof", ep01)
                .addEpisode("ep02.dlof", ep02)
                .withCharacters(charactersDoc)
                .withSetTxt("theme=dark\nfont=Cairo-Bold\nreadingMode=comfortable\n")
                .buildTo(outDir.resolve("MySeries.dlofSeries"));

        System.out.println("تم إنشاء الملفات والحزم داخل: " + outDir.toAbsolutePath());

        // ── تحقق سريع: أعد قراءة أحد الملفات المكتوبة ─────────
        DlofDocument roundTrip = Dlof.parse(outDir.resolve("MyComic/p01.dlof"));
        System.out.println("قراءة عكسية ناجحة لـ: " + roundTrip.metadata().title()
                + " (domain=" + roundTrip.metadata().domain().xmlValue + ")");
    }

    private static DlofDocument buildComicPage(String id, String title, String prevRef, String nextRef,
                                                boolean loopRoot, String caption, List<PanelDialogue> dialogue) {
        Metadata metadata = new Metadata(title, Domain.COMIC, "المؤلف", null, null, "ar", List.of("قصة-مصورة"));
        LoopLinks loopLinks = new LoopLinks(
                prevRef != null ? new LinkRef(prevRef) : null,
                nextRef != null ? new LinkRef(nextRef) : null,
                loopRoot);
        ComicPanel content = new ComicPanel(1, 1, caption, dialogue, "img-" + id, caption, 12, "#1A1A2E");
        return DlofDocument.of(id, metadata, loopLinks, content);
    }

    private static DlofDocument buildEpisode(String id, int epNum, int season, String title,
                                              String prevRef, String nextRef, boolean loopRoot, String synopsis) {
        Metadata metadata = new Metadata(title, Domain.SERIES, "المؤلف", null, null, "ar", List.of("سلسلة"));
        LoopLinks loopLinks = new LoopLinks(
                prevRef != null ? new LinkRef(prevRef) : null,
                nextRef != null ? new LinkRef(nextRef) : null,
                loopRoot);
        EpisodeItem content = new EpisodeItem(epNum, season, title, synopsis, synopsis, 1470,
                "سلسلتي", "media/videos/" + id + ".mp4", "2026-01-01", null, null, List.of());
        return DlofDocument.of(id, metadata, loopLinks, content);
    }
}
