package org.dlof.lib.content;

import java.util.Collections;
import java.util.List;

/** حلقة واحدة من مسلسل/بودكاست/سلسلة فيديو (domain=series). */
public record EpisodeItem(
        Integer episodeNumber,
        Integer seasonNumber,
        String episodeTitle,
        String synopsis,
        String body,
        Integer durationSeconds,
        String seriesTitle,
        String mediaRef,
        String releaseDate,
        String rating,
        String director,
        List<String> writers
) implements DlofContent {
    public EpisodeItem {
        if (body == null) body = "";
        if (writers == null) writers = Collections.emptyList();
    }
}
