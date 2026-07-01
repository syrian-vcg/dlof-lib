package org.dlof.lib.kt

import org.dlof.lib.characters.*
import org.dlof.lib.content.*
import org.dlof.lib.model.*
import org.dlof.lib.pkg.DlofPackageBuilder
import org.dlof.lib.pkg.DlofSeriesBuilder
import org.dlof.lib.writer.DlofWriter
import java.nio.file.Path

/**
 * طبقة Kotlin مريحة (DSL) فوق مكتبة dlof الأساسية المكتوبة بجافا.
 * تستخدم builders بأسلوب Kotlin idiomatic (apply-style) لبناء
 * ملفات .dlof لقصص مصورة (comic) ومسلسلات (series) وملفات شخصيات (characters)
 * دون الحاجة للتعامل مباشرة مع الـ records الطويلة في جافا.
 */

// ── قصة مصورة (comic) ───────────────────────────────────────────────

class ComicPanelBuilder(private val panelId: String, private val title: String) {
    var author: String? = null
    var pageNumber: Int? = null
    var panelNumber: Int? = null
    var caption: String? = null
    var imageAttachmentRef: String? = null
    var altText: String? = null
    var panelWidth: Int? = null
    var backgroundColor: String? = null
    var previousRef: String? = null
    var nextRef: String? = null
    var loopRoot: Boolean = false
    var tags: MutableList<String> = mutableListOf()

    private val lines: MutableList<PanelDialogue> = mutableListOf()

    fun speech(character: String, text: String) {
        lines.add(PanelDialogue(null, character, text, DialogueType.SPEECH))
    }

    fun thought(character: String, text: String) {
        lines.add(PanelDialogue(null, character, text, DialogueType.THOUGHT))
    }

    fun narration(text: String) {
        lines.add(PanelDialogue(null, null, text, DialogueType.NARRATION))
    }

    fun build(): DlofDocument {
        val metadata = Metadata(title, Domain.COMIC, author, null, null, "ar", tags)
        val loopLinks = LoopLinks(
            previousRef?.let { LinkRef(it) },
            nextRef?.let { LinkRef(it) },
            loopRoot
        )
        val content = ComicPanel(
            panelNumber, pageNumber, caption, lines.toList(),
            imageAttachmentRef, altText, panelWidth, backgroundColor
        )
        return DlofDocument(panelId, "1.0", metadata, loopLinks, content, emptyList(), null)
    }
}

/** بناء ملف .dlof للوحة قصة مصورة واحدة بأسلوب DSL: comicPanel("ch01-p01", "..") { ... } */
fun comicPanel(id: String, title: String, block: ComicPanelBuilder.() -> Unit): DlofDocument =
    ComicPanelBuilder(id, title).apply(block).build()

// ── حلقة مسلسل (series episode) ─────────────────────────────────────

class EpisodeBuilder(private val episodeId: String, private val title: String) {
    var author: String? = null
    var episodeNumber: Int? = null
    var seasonNumber: Int? = null
    var synopsis: String? = null
    var body: String = ""
    var durationSeconds: Int? = null
    var seriesTitle: String? = null
    var mediaRef: String? = null
    var releaseDate: String? = null
    var previousRef: String? = null
    var nextRef: String? = null
    var loopRoot: Boolean = false
    var tags: MutableList<String> = mutableListOf()

    fun build(): DlofDocument {
        val metadata = Metadata(title, Domain.SERIES, author, null, null, "ar", tags)
        val loopLinks = LoopLinks(
            previousRef?.let { LinkRef(it) },
            nextRef?.let { LinkRef(it) },
            loopRoot
        )
        val content = EpisodeItem(
            episodeNumber, seasonNumber, title, synopsis, body, durationSeconds,
            seriesTitle, mediaRef, releaseDate, null, null, emptyList()
        )
        return DlofDocument(episodeId, "1.0", metadata, loopLinks, content, emptyList(), null)
    }
}

/** بناء ملف .dlof لحلقة مسلسل بأسلوب DSL: episode("ep01", "البداية") { ... } */
fun episode(id: String, title: String, block: EpisodeBuilder.() -> Unit): DlofDocument =
    EpisodeBuilder(episodeId = id, title = title).apply(block).build()

// ── ملف تعريف الشخصيات (characters / Yime roster) ───────────────────

class CharacterBuilder(private val id: String, private val name: String) {
    var role: CharacterRole = CharacterRole.SUPPORTING
    var alias: String? = null
    var age: Int? = null
    var gender: CharacterGender = CharacterGender.UNSPECIFIED
    var description: String = ""
    var appearance: String? = null
    var personality: String? = null
    var backstory: String? = null
    var goals: String? = null
    var status: CharacterStatus = CharacterStatus.ALIVE
    var appearsIn: MutableList<String> = mutableListOf()
    private val relations: MutableList<CharacterRelation> = mutableListOf()

    fun relation(targetId: String, type: RelationType, label: String? = null) {
        relations.add(CharacterRelation(targetId, type, label))
    }

    fun build(): Yime = Yime(
        id, name, role, alias, age, gender, description, appearance, personality,
        backstory, goals, null, relations.toList(), appearsIn.toList(), null, null,
        emptyList(), null, status
    )
}

class CharactersDocBuilder(private val docId: String, private val seriesTitle: String) {
    var author: String? = null
    var previousRef: String? = null
    var nextRef: String? = null
    private val characters: MutableList<Yime> = mutableListOf()

    fun character(id: String, name: String, block: CharacterBuilder.() -> Unit) {
        characters.add(CharacterBuilder(id, name).apply(block).build())
    }

    fun build(): DlofDocument {
        val metadata = Metadata(seriesTitle + " — الشخصيات", Domain.CHARACTERS, author, null, null, "ar", emptyList())
        val loopLinks = LoopLinks(previousRef?.let { LinkRef(it) }, nextRef?.let { LinkRef(it) }, false)
        val roster = YimeRoster(docId, seriesTitle, characters.toList(), null)
        val content = CharactersContent(roster)
        return DlofDocument(docId, "1.0", metadata, loopLinks, content, emptyList(), null)
    }
}

/** بناء ملف characters.dlof بأسلوب DSL: characters("myseries-characters", "سلسلتي") { character(...) { } } */
fun characters(id: String, seriesTitle: String, block: CharactersDocBuilder.() -> Unit): DlofDocument =
    CharactersDocBuilder(id, seriesTitle).apply(block).build()

// ── امتدادات مريحة ───────────────────────────────────────────────

fun DlofDocument.toXmlString(): String = DlofWriter.toXml(this)

fun DlofDocument.writeTo(path: Path) = DlofWriter.writeToFile(this, path)

fun DlofDocument.toDlofPkgBuilder(): DlofPackageBuilder = DlofPackageBuilder(this)

fun seriesPackage(folderName: String, block: DlofSeriesBuilder.() -> Unit): DlofSeriesBuilder =
    DlofSeriesBuilder(folderName).apply(block)
