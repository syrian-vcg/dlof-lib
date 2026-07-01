package org.dlof.lib.parser;

import org.dlof.lib.characters.*;
import org.dlof.lib.content.*;
import org.dlof.lib.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * يحلّل نص XML لملف .dlof إلى {@link DlofDocument} كائن جافا كامل.
 * يعطّل الكيانات الخارجية (XXE) افتراضياً لأسباب أمنية.
 */
public final class DlofParser {

    private DlofParser() {
    }

    public static DlofDocument parse(Path path) throws IOException, DlofParseException {
        return parse(Files.readString(path, StandardCharsets.UTF_8));
    }

    public static DlofDocument parse(String xml) throws DlofParseException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            Element root = doc.getDocumentElement();

            String id = root.getAttribute("id");
            String version = root.hasAttribute("version") ? root.getAttribute("version") : "1.0";

            Metadata metadata = parseMetadata(child(root, "metadata"));
            LoopLinks loopLinks = parseLoopLinks(child(root, "loopLinks"));
            DlofContent content = parseContent(child(root, "content"));
            List<Attachment> attachments = parseAttachments(child(root, "attachments"));
            Template template = parseTemplate(child(root, "template"));

            return new DlofDocument(id, version, metadata, loopLinks, content, attachments, template);
        } catch (DlofParseException e) {
            throw e;
        } catch (Exception e) {
            throw new DlofParseException("تعذّر تحليل ملف dlof: " + e.getMessage(), e);
        }
    }

    // ── metadata ──────────────────────────────────────────────
    private static Metadata parseMetadata(Element el) {
        if (el == null) throw new DlofParseException("عنصر <metadata> مفقود", null);
        String title = text(child(el, "title"));
        Domain domain = Domain.fromXml(text(child(el, "domain")));
        String author = textOrNull(child(el, "author"));
        String createdAt = textOrNull(child(el, "createdAt"));
        String updatedAt = textOrNull(child(el, "updatedAt"));
        String language = textOrDefault(child(el, "language"), "ar");
        List<String> tags = new ArrayList<>();
        Element tagsEl = child(el, "tags");
        if (tagsEl != null) {
            for (Element t : children(tagsEl, "tag")) tags.add(text(t));
        }
        return new Metadata(title, domain, author, createdAt, updatedAt, language, tags);
    }

    // ── loopLinks ─────────────────────────────────────────────
    private static LoopLinks parseLoopLinks(Element el) {
        if (el == null) return LoopLinks.EMPTY;
        LinkRef previous = parseLinkRef(child(el, "previous"));
        LinkRef next = parseLinkRef(child(el, "next"));
        boolean loopRoot = Boolean.parseBoolean(textOrDefault(child(el, "loopRoot"), "false"));
        return new LoopLinks(previous, next, loopRoot);
    }

    private static LinkRef parseLinkRef(Element el) {
        if (el == null) return null;
        String ref = el.getAttribute("ref");
        String title = el.hasAttribute("title") ? el.getAttribute("title") : null;
        return new LinkRef(ref, title);
    }

    // ── content ───────────────────────────────────────────────
    private static DlofContent parseContent(Element contentEl) {
        if (contentEl == null) throw new DlofParseException("عنصر <content> مفقود", null);
        Element first = firstElementChild(contentEl);
        if (first == null) throw new DlofParseException("عنصر <content> فارغ", null);

        return switch (first.getLocalName() != null ? first.getLocalName() : first.getNodeName()) {
            case "genericItem" -> new GenericItem(
                    text(child(first, "type")), text(child(first, "element")), text(child(first, "body")),
                    first.hasAttribute("customType") ? first.getAttribute("customType") : null);
            case "qaItem" -> new QaItem(
                    text(child(first, "question")), text(child(first, "answer")),
                    textOrNull(child(first, "explanation")), textOrNull(child(first, "difficulty")));
            case "bookChapter" -> new BookChapter(
                    intOrNull(child(first, "chapterNumber")), text(child(first, "chapterTitle")),
                    text(child(first, "text")), textOrNull(child(first, "summary")));
            case "termDefinition" -> new TermDefinition(
                    text(child(first, "term")), text(child(first, "definition")), textOrNull(child(first, "example")));
            case "infoExplain" -> new InfoExplain(
                    text(child(first, "topic")), text(child(first, "explanation")), textOrNull(child(first, "source")));
            case "episodeItem" -> parseEpisode(first);
            case "comicPanel" -> parseComicPanel(first);
            case "characters" -> new CharactersContent(parseCharacters(first));
            default -> throw new DlofParseException("نوع محتوى غير مدعوم: " + first.getNodeName(), null);
        };
    }

    private static EpisodeItem parseEpisode(Element el) {
        List<String> writers = new ArrayList<>();
        Element writersEl = child(el, "writers");
        if (writersEl != null) for (Element w : children(writersEl, "writer")) writers.add(text(w));
        return new EpisodeItem(
                intOrNull(child(el, "episodeNumber")),
                intOrNull(child(el, "seasonNumber")),
                text(child(el, "episodeTitle")),
                textOrNull(child(el, "synopsis")),
                textOrDefault(child(el, "body"), ""),
                intOrNull(child(el, "duration")),
                textOrNull(child(el, "seriesTitle")),
                textOrNull(child(el, "mediaRef")),
                textOrNull(child(el, "releaseDate")),
                textOrNull(child(el, "rating")),
                textOrNull(child(el, "director")),
                writers);
    }

    private static ComicPanel parseComicPanel(Element el) {
        List<PanelDialogue> lines = new ArrayList<>();
        Element dialogueEl = child(el, "dialogue");
        if (dialogueEl != null) {
            for (Element line : children(dialogueEl, "line")) {
                lines.add(new PanelDialogue(
                        line.hasAttribute("characterId") ? line.getAttribute("characterId") : null,
                        line.hasAttribute("characterName") ? line.getAttribute("characterName") : null,
                        text(line),
                        DialogueType.fromXml(line.hasAttribute("type") ? line.getAttribute("type") : "speech")));
            }
        }
        return new ComicPanel(
                intOrNull(child(el, "panelNumber")),
                intOrNull(child(el, "pageNumber")),
                textOrNull(child(el, "caption")),
                lines,
                textOrNull(child(el, "imageAttachmentRef")),
                textOrNull(child(el, "altText")),
                el.hasAttribute("panelWidth") ? Integer.parseInt(el.getAttribute("panelWidth")) : null,
                el.hasAttribute("backgroundColor") ? el.getAttribute("backgroundColor") : null);
    }

    private static YimeRoster parseCharacters(Element el) {
        String seriesTitle = el.hasAttribute("seriesTitle") ? el.getAttribute("seriesTitle") : "";
        String loopId = el.hasAttribute("loopId") ? el.getAttribute("loopId") : "";
        List<Yime> characters = new ArrayList<>();
        for (Element c : children(el, "character")) {
            List<CharacterRelation> relations = new ArrayList<>();
            Element relsEl = child(c, "relationships");
            if (relsEl != null) {
                for (Element r : children(relsEl, "relation")) {
                    relations.add(new CharacterRelation(
                            r.getAttribute("targetId"),
                            RelationType.fromXml(r.hasAttribute("type") ? r.getAttribute("type") : "acquaintance"),
                            r.hasAttribute("label") ? r.getAttribute("label") : null));
                }
            }
            List<String> appearsIn = new ArrayList<>();
            String appearsInText = textOrNull(child(c, "appearsIn"));
            if (appearsInText != null && !appearsInText.isBlank()) {
                appearsIn.addAll(Arrays.asList(appearsInText.trim().split("\\s+")));
            }
            characters.add(new Yime(
                    c.getAttribute("id"),
                    c.getAttribute("name"),
                    CharacterRole.fromXml(c.hasAttribute("role") ? c.getAttribute("role") : "supporting"),
                    textOrNull(child(c, "alias")),
                    c.hasAttribute("age") ? Integer.parseInt(c.getAttribute("age")) : null,
                    CharacterGender.fromXml(c.hasAttribute("gender") ? c.getAttribute("gender") : "unspecified"),
                    textOrDefault(child(c, "description"), ""),
                    textOrNull(child(c, "appearance")),
                    textOrNull(child(c, "personality")),
                    textOrNull(child(c, "backstory")),
                    textOrNull(child(c, "goals")),
                    textOrNull(child(c, "conflicts")),
                    relations,
                    appearsIn,
                    textOrNull(child(c, "avatarAttachmentRef")),
                    null,
                    List.of(),
                    null,
                    CharacterStatus.fromXml(c.hasAttribute("status") ? c.getAttribute("status") : "alive")));
        }
        return new YimeRoster(loopId, seriesTitle, characters, null);
    }

    // ── attachments ───────────────────────────────────────────
    private static List<Attachment> parseAttachments(Element el) {
        List<Attachment> result = new ArrayList<>();
        if (el == null) return result;
        for (Element a : children(el, "attachment")) {
            result.add(new Attachment(
                    a.getAttribute("id"), a.getAttribute("fileName"), a.getAttribute("mimeType"),
                    AttachmentKind.fromXml(a.getAttribute("kind")),
                    textOrNull(child(a, "data")), textOrNull(child(a, "uri")),
                    a.hasAttribute("sizeBytes") ? Long.parseLong(a.getAttribute("sizeBytes")) : null,
                    textOrNull(child(a, "caption"))));
        }
        return result;
    }

    // ── template ──────────────────────────────────────────────
    private static Template parseTemplate(Element el) {
        if (el == null) return null;
        return new Template(
                attrOrNull(el, "ref"), attrOrNull(el, "primaryColor"), attrOrNull(el, "secondaryColor"),
                attrOrNull(el, "backgroundColor"), attrOrNull(el, "textColor"), attrOrNull(el, "fontFamily"),
                TemplateLayout.fromXml(el.hasAttribute("layout") ? el.getAttribute("layout") : "standard"),
                attrOrNull(el, "headerAttachmentRef"));
    }

    // ── DOM helpers ───────────────────────────────────────────
    private static Element child(Element parent, String name) {
        if (parent == null) return null;
        NodeList list = parent.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && localName(n).equals(name)) return (Element) n;
        }
        return null;
    }

    private static List<Element> children(Element parent, String name) {
        List<Element> out = new ArrayList<>();
        if (parent == null) return out;
        NodeList list = parent.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && localName(n).equals(name)) out.add((Element) n);
        }
        return out;
    }

    private static Element firstElementChild(Element parent) {
        NodeList list = parent.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) return (Element) n;
        }
        return null;
    }

    private static String localName(Node n) {
        return n.getLocalName() != null ? n.getLocalName() : n.getNodeName();
    }

    private static String text(Element el) {
        return el == null ? "" : el.getTextContent().trim();
    }

    private static String textOrNull(Element el) {
        if (el == null) return null;
        String t = el.getTextContent().trim();
        return t.isEmpty() ? null : t;
    }

    private static String textOrDefault(Element el, String def) {
        String t = textOrNull(el);
        return t != null ? t : def;
    }

    private static Integer intOrNull(Element el) {
        String t = textOrNull(el);
        return t == null ? null : Integer.parseInt(t);
    }

    private static String attrOrNull(Element el, String name) {
        return el.hasAttribute(name) ? el.getAttribute(name) : null;
    }
}
