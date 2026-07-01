package org.dlof.lib.writer;

import org.dlof.lib.characters.CharacterRelation;
import org.dlof.lib.characters.Yime;
import org.dlof.lib.characters.YimeRoster;
import org.dlof.lib.content.*;
import org.dlof.lib.model.*;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.dlof.lib.writer.XmlUtil.escape;

/**
 * يحوّل {@link DlofDocument} إلى نص XML مطابق لمخطط DLoF (spec/schema/dlof.xsd
 * وامتدادات تطبيق القارئ: series / comic / characters).
 * لا يعتمد على أي مكتبة خارجية — StringBuilder + تهريب يدوي فقط.
 */
public final class DlofWriter {

    private static final String NS = "https://dlof.org/schema/1.0";

    private DlofWriter() {
    }

    public static String toXml(DlofDocument doc) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<documentLoop xmlns=\"").append(NS).append("\" version=\"")
                .append(escape(doc.version())).append("\" id=\"").append(escape(doc.id())).append("\">\n");

        writeMetadata(sb, doc.metadata());
        writeLoopLinks(sb, doc.loopLinks());
        writeContent(sb, doc.content());
        if (!doc.attachments().isEmpty()) writeAttachments(sb, doc.attachments());
        if (doc.template() != null) writeTemplate(sb, doc.template());

        sb.append("</documentLoop>\n");
        return sb.toString();
    }

    public static void writeToFile(DlofDocument doc, Path path) throws IOException {
        Files.createDirectories(path.toAbsolutePath().getParent());
        Files.writeString(path, toXml(doc), StandardCharsets.UTF_8);
    }

    // ── metadata ──────────────────────────────────────────────
    private static void writeMetadata(StringBuilder sb, Metadata m) {
        sb.append("  <metadata>\n");
        tag(sb, 4, "title", m.title());
        tag(sb, 4, "domain", m.domain().xmlValue);
        if (m.author() != null) tag(sb, 4, "author", m.author());
        if (m.createdAt() != null) tag(sb, 4, "createdAt", m.createdAt());
        if (m.updatedAt() != null) tag(sb, 4, "updatedAt", m.updatedAt());
        tag(sb, 4, "language", m.language());
        if (!m.tags().isEmpty()) {
            sb.append("    <tags>\n");
            for (String t : m.tags()) tag(sb, 6, "tag", t);
            sb.append("    </tags>\n");
        }
        sb.append("  </metadata>\n\n");
    }

    // ── loopLinks ─────────────────────────────────────────────
    private static void writeLoopLinks(StringBuilder sb, LoopLinks l) {
        sb.append("  <loopLinks>\n");
        if (l.previous() != null) writeLinkRef(sb, "previous", l.previous());
        if (l.next() != null) writeLinkRef(sb, "next", l.next());
        sb.append("    <loopRoot>").append(l.loopRoot()).append("</loopRoot>\n");
        sb.append("  </loopLinks>\n\n");
    }

    private static void writeLinkRef(StringBuilder sb, String tagName, LinkRef ref) {
        sb.append("    <").append(tagName).append(" ref=\"").append(escape(ref.ref())).append('"');
        if (ref.title() != null) sb.append(" title=\"").append(escape(ref.title())).append('"');
        sb.append("/>\n");
    }

    // ── content ───────────────────────────────────────────────
    private static void writeContent(StringBuilder sb, DlofContent content) {
        sb.append("  <content>\n");
        switch (content) {
            case GenericItem g -> {
                sb.append("    <genericItem");
                if (g.customType() != null) sb.append(" customType=\"").append(escape(g.customType())).append('"');
                sb.append(">\n");
                tag(sb, 6, "type", g.type());
                tag(sb, 6, "element", g.element());
                tag(sb, 6, "body", g.body());
                sb.append("    </genericItem>\n");
            }
            case QaItem q -> {
                sb.append("    <qaItem>\n");
                tag(sb, 6, "question", q.question());
                tag(sb, 6, "answer", q.answer());
                if (q.explanation() != null) tag(sb, 6, "explanation", q.explanation());
                if (q.difficulty() != null) tag(sb, 6, "difficulty", q.difficulty());
                sb.append("    </qaItem>\n");
            }
            case BookChapter c -> {
                sb.append("    <bookChapter>\n");
                if (c.chapterNumber() != null) tag(sb, 6, "chapterNumber", String.valueOf(c.chapterNumber()));
                tag(sb, 6, "chapterTitle", c.chapterTitle());
                tag(sb, 6, "text", c.text());
                if (c.summary() != null) tag(sb, 6, "summary", c.summary());
                sb.append("    </bookChapter>\n");
            }
            case TermDefinition t -> {
                sb.append("    <termDefinition>\n");
                tag(sb, 6, "term", t.term());
                tag(sb, 6, "definition", t.definition());
                if (t.example() != null) tag(sb, 6, "example", t.example());
                sb.append("    </termDefinition>\n");
            }
            case InfoExplain e -> {
                sb.append("    <infoExplain>\n");
                tag(sb, 6, "topic", e.topic());
                tag(sb, 6, "explanation", e.explanation());
                if (e.source() != null) tag(sb, 6, "source", e.source());
                sb.append("    </infoExplain>\n");
            }
            case EpisodeItem ep -> writeEpisode(sb, ep);
            case ComicPanel p -> writeComicPanel(sb, p);
            case CharactersContent cc -> writeCharacters(sb, cc.roster());
        }
        sb.append("  </content>\n\n");
    }

    private static void writeEpisode(StringBuilder sb, EpisodeItem ep) {
        sb.append("    <episodeItem>\n");
        if (ep.episodeNumber() != null) tag(sb, 6, "episodeNumber", String.valueOf(ep.episodeNumber()));
        if (ep.seasonNumber() != null) tag(sb, 6, "seasonNumber", String.valueOf(ep.seasonNumber()));
        tag(sb, 6, "episodeTitle", ep.episodeTitle());
        if (ep.synopsis() != null) tag(sb, 6, "synopsis", ep.synopsis());
        if (ep.durationSeconds() != null) tag(sb, 6, "duration", String.valueOf(ep.durationSeconds()));
        if (ep.seriesTitle() != null) tag(sb, 6, "seriesTitle", ep.seriesTitle());
        if (ep.mediaRef() != null) tag(sb, 6, "mediaRef", ep.mediaRef());
        if (ep.releaseDate() != null) tag(sb, 6, "releaseDate", ep.releaseDate());
        if (ep.rating() != null) tag(sb, 6, "rating", ep.rating());
        if (ep.director() != null) tag(sb, 6, "director", ep.director());
        if (!ep.writers().isEmpty()) {
            sb.append("      <writers>\n");
            for (String w : ep.writers()) tag(sb, 8, "writer", w);
            sb.append("      </writers>\n");
        }
        if (!ep.body().isBlank()) tag(sb, 6, "body", ep.body());
        sb.append("    </episodeItem>\n");
    }

    private static void writeComicPanel(StringBuilder sb, ComicPanel p) {
        sb.append("    <comicPanel");
        if (p.panelWidth() != null) sb.append(" panelWidth=\"").append(p.panelWidth()).append('"');
        if (p.backgroundColor() != null) sb.append(" backgroundColor=\"").append(escape(p.backgroundColor())).append('"');
        sb.append(">\n");
        if (p.pageNumber() != null) tag(sb, 6, "pageNumber", String.valueOf(p.pageNumber()));
        if (p.panelNumber() != null) tag(sb, 6, "panelNumber", String.valueOf(p.panelNumber()));
        if (p.imageAttachmentRef() != null) tag(sb, 6, "imageAttachmentRef", p.imageAttachmentRef());
        if (p.altText() != null) tag(sb, 6, "altText", p.altText());
        if (p.caption() != null) tag(sb, 6, "caption", p.caption());
        if (!p.dialogue().isEmpty()) {
            sb.append("      <dialogue>\n");
            for (PanelDialogue d : p.dialogue()) {
                sb.append("        <line type=\"").append(d.type().xmlValue).append('"');
                if (d.characterId() != null) sb.append(" characterId=\"").append(escape(d.characterId())).append('"');
                if (d.characterName() != null) sb.append(" characterName=\"").append(escape(d.characterName())).append('"');
                sb.append('>').append(escape(d.text())).append("</line>\n");
            }
            sb.append("      </dialogue>\n");
        }
        sb.append("    </comicPanel>\n");
    }

    private static void writeCharacters(StringBuilder sb, YimeRoster roster) {
        sb.append("    <characters seriesTitle=\"").append(escape(roster.seriesTitle())).append('"');
        sb.append(" loopId=\"").append(escape(roster.loopId())).append("\">\n");
        for (Yime y : roster.characters()) {
            sb.append("      <character id=\"").append(escape(y.id())).append('"');
            sb.append(" name=\"").append(escape(y.name())).append('"');
            sb.append(" role=\"").append(y.role().xmlValue).append('"');
            if (y.age() != null) sb.append(" age=\"").append(y.age()).append('"');
            sb.append(" gender=\"").append(y.gender().xmlValue).append('"');
            sb.append(" status=\"").append(y.status().xmlValue).append('"');
            sb.append(">\n");
            if (y.alias() != null) tag(sb, 8, "alias", y.alias());
            if (!y.description().isBlank()) tag(sb, 8, "description", y.description());
            if (y.appearance() != null) tag(sb, 8, "appearance", y.appearance());
            if (y.personality() != null) tag(sb, 8, "personality", y.personality());
            if (y.backstory() != null) tag(sb, 8, "backstory", y.backstory());
            if (y.goals() != null) tag(sb, 8, "goals", y.goals());
            if (y.conflicts() != null) tag(sb, 8, "conflicts", y.conflicts());
            if (!y.relationships().isEmpty()) {
                sb.append("        <relationships>\n");
                for (CharacterRelation r : y.relationships()) {
                    sb.append("          <relation targetId=\"").append(escape(r.targetId())).append('"');
                    sb.append(" type=\"").append(r.type().xmlValue).append('"');
                    if (r.label() != null) sb.append(" label=\"").append(escape(r.label())).append('"');
                    sb.append("/>\n");
                }
                sb.append("        </relationships>\n");
            }
            if (!y.appearsIn().isEmpty()) tag(sb, 8, "appearsIn", String.join(" ", y.appearsIn()));
            if (y.avatarAttachmentRef() != null) tag(sb, 8, "avatarAttachmentRef", y.avatarAttachmentRef());
            sb.append("      </character>\n");
        }
        sb.append("    </characters>\n");
    }

    // ── attachments ───────────────────────────────────────────
    private static void writeAttachments(StringBuilder sb, List<Attachment> attachments) {
        sb.append("  <attachments>\n");
        for (Attachment a : attachments) {
            sb.append("    <attachment id=\"").append(escape(a.id())).append('"');
            sb.append(" fileName=\"").append(escape(a.fileName())).append('"');
            sb.append(" mimeType=\"").append(escape(a.mimeType())).append('"');
            sb.append(" kind=\"").append(a.kind().xmlValue).append('"');
            if (a.sizeBytes() != null) sb.append(" sizeBytes=\"").append(a.sizeBytes()).append('"');
            sb.append(">\n");
            if (a.uri() != null) tag(sb, 6, "uri", a.uri());
            if (a.data() != null) tag(sb, 6, "data", a.data());
            if (a.caption() != null) tag(sb, 6, "caption", a.caption());
            sb.append("    </attachment>\n");
        }
        sb.append("  </attachments>\n\n");
    }

    // ── template ──────────────────────────────────────────────
    private static void writeTemplate(StringBuilder sb, Template t) {
        sb.append("  <template");
        if (t.ref() != null) sb.append(" ref=\"").append(escape(t.ref())).append('"');
        if (t.primaryColor() != null) sb.append(" primaryColor=\"").append(escape(t.primaryColor())).append('"');
        if (t.secondaryColor() != null) sb.append(" secondaryColor=\"").append(escape(t.secondaryColor())).append('"');
        if (t.backgroundColor() != null) sb.append(" backgroundColor=\"").append(escape(t.backgroundColor())).append('"');
        if (t.textColor() != null) sb.append(" textColor=\"").append(escape(t.textColor())).append('"');
        if (t.fontFamily() != null) sb.append(" fontFamily=\"").append(escape(t.fontFamily())).append('"');
        sb.append(" layout=\"").append(t.layout().xmlValue).append('"');
        if (t.headerAttachmentRef() != null) sb.append(" headerAttachmentRef=\"").append(escape(t.headerAttachmentRef())).append('"');
        sb.append("/>\n");
    }

    // ── helper ────────────────────────────────────────────────
    private static void tag(StringBuilder sb, int indent, String name, String value) {
        sb.append(" ".repeat(indent)).append('<').append(name).append('>')
                .append(escape(value)).append("</").append(name).append(">\n");
    }
}
