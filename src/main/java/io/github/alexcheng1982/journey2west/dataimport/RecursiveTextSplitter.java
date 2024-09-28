package io.github.alexcheng1982.journey2west.dataimport;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import java.util.List;
import org.springframework.ai.transformer.splitter.TextSplitter;

public class RecursiveTextSplitter extends TextSplitter {

  private final int maxSegmentSize;
  private final int maxOverlapSize;

  public RecursiveTextSplitter(int maxSegmentSize, int maxOverlapSize) {
    this.maxSegmentSize = maxSegmentSize;
    this.maxOverlapSize = maxOverlapSize;
  }

  @Override
  protected List<String> splitText(String text) {
    var splitter = DocumentSplitters.recursive(maxSegmentSize, maxOverlapSize);
    return splitter.split(new Document(text))
        .stream()
        .map(TextSegment::text)
        .distinct()
        .toList();
  }
}
