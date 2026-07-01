# dlof-lib

مكتبة **Java / Kotlin** مستقلة (بدون أي اعتماديات خارجية وقت التشغيل — JDK فقط)
لقراءة وكتابة وبناء ملفات وحزم **DLoF** (Document Loop Format):

- `.dlof` — ملف مستند واحد (XML) — تعليم، كتاب، **قصة مصورة (comic)**، **مسلسل/سلسلة (series)**، **شخصيات (characters)**، ومجالات أخرى.
- `.dlofpkg` — حزمة ملف dlof فردي (ZIP: `package.dlof` + `meta.json` + `attachments/`).
- `.dlofSeries` — حزمة سلسلة كاملة (ZIP لمجلد سلسلة كامل: فهرس + حلقات/فصول + شخصيات + خطوط + وسائط).

مبنية لتُستخدم مباشرة داخل `android-app/` الموجود في هذا المستودع (كموديول Gradle منفصل)
أو في أي مشروع JVM آخر (خادم، أداة سطر أوامر، إلخ) دون أي تبعية على Android SDK.

## البنية

```
dlof-lib/
├── build.gradle.kts
├── settings.gradle.kts
├── src/main/java/org/dlof/lib/
│   ├── model/        # DlofDocument, Metadata, LoopLinks, Attachment, Template...
│   ├── content/       # GenericItem, QaItem, BookChapter, EpisodeItem, ComicPanel...
│   ├── characters/     # Yime, YimeRoster, CharacterRole... (تعريف الشخصيات)
│   ├── parser/         # DlofParser — يحلّل XML إلى DlofDocument (يعطّل XXE)
│   ├── writer/         # DlofWriter — يكتب DlofDocument كنص XML
│   ├── pkg/            # DlofPackageBuilder (.dlofpkg) و DlofSeriesBuilder (.dlofSeries)
│   └── Dlof.java       # واجهة موحّدة (facade) لكل ما سبق
├── src/main/kotlin/org/dlof/lib/kt/
│   └── DlofDsl.kt      # طبقة Kotlin مريحة (DSL): comicPanel { }, episode { }, characters { }
├── demo/java/org/dlof/lib/demo/Main.java   # مثال تشغيلي كامل (جافا فقط، بدون Gradle)
└── examples/            # ملفات .dlof جاهزة لقصة مصورة، مسلسل، وشخصيات
```

## الاستخدام من جافا

```java
import org.dlof.lib.Dlof;
import org.dlof.lib.model.*;
import org.dlof.lib.content.*;

// بناء لوحة قصة مصورة
var content = new ComicPanel(1, 1, "صباح هادئ في المدينة",
        List.of(new PanelDialogue(null, "سارة", "أخيراً وصلنا!", DialogueType.SPEECH)),
        "img-p1", "سارة تقف أمام المدينة", 12, "#1A1A2E");

var metadata = new Metadata("الصفحة الأولى", Domain.COMIC, "المؤلف", null, null, "ar", List.of("قصة-مصورة"));
var loopLinks = new LoopLinks(null, new LinkRef("p02.dlof"), true);
var doc = DlofDocument.of("mycomic-p01", metadata, loopLinks, content);

Dlof.write(doc, Path.of("out/p01.dlof"));                       // .dlof
Dlof.packageBuilder(doc).buildTo(Path.of("out/p01.dlofpkg"));   // .dlofpkg

// قراءة ملف موجود
DlofDocument parsed = Dlof.parse(Path.of("out/p01.dlof"));
```

## الاستخدام من Kotlin (DSL)

```kotlin
import org.dlof.lib.kt.*

val ep = episode("ep01", "البداية") {
    seasonNumber = 1
    episodeNumber = 1
    seriesTitle = "سلسلتي"
    synopsis = "تعريف بعالم القصة وأبطالها."
    nextRef = "ep02.dlof"
    loopRoot = true
}

ep.writeTo(Path.of("out/ep01.dlof"))
ep.toDlofPkgBuilder().buildTo(Path.of("out/ep01.dlofpkg"))
```

## بناء حزمة `.dlofSeries` كاملة

```kotlin
seriesPackage("MySeries") {
    withSeriesIndex(seriesIndexDoc)
    addEpisode("ep01.dlof", ep01)
    addEpisode("ep02.dlof", ep02)
    withCharacters(charactersDoc)
    withSetTxt("theme=dark\nfont=Cairo-Bold\n")
}.buildTo(Path.of("out/MySeries.dlofSeries"))
```

## ملاحظات

- التحليل (`DlofParser`) يعطّل DOCTYPE والكيانات الخارجية افتراضياً (حماية من XXE).
- الميزات المتقدمة في `spec/schema/dlof.xsd` (مثل `remoteSync` و`webPublish` و`mediaFolder`)
  غير مدعومة بعد في هذا الإصدار الأول من المكتبة — يمكن إضافتها لاحقاً بنفس النمط.
- لا اعتماديات خارجية وقت التشغيل: XML عبر `javax.xml.parsers` القياسي، والحزم عبر `java.util.zip` القياسي.
