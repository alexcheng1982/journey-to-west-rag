package io.github.alexcheng1982.journey2west.dataimport;


import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;

public class DataImportJob implements CommandLineRunner {

  private final VectorStore vectorStore;
  private final EmbeddingModel embeddingModel;

  private static final Logger LOGGER = LoggerFactory.getLogger(
      DataImportJob.class);

  private final Path markerFile = Paths.get(System.getProperty("user.home"),
      ".journey2west_data_import");

  public DataImportJob(VectorStore vectorStore,
      EmbeddingModel embeddingModel) {
    this.vectorStore = vectorStore;
    this.embeddingModel = embeddingModel;
  }

  @Override
  public void run(String... args) {
    LOGGER.info("Running data import");
    if (Files.exists(markerFile)) {
      LOGGER.info("Marker file {} exists, skipping",
          markerFile.toAbsolutePath());
      return;
    }
    try {
      importDocuments();
    } catch (Exception e) {
      LOGGER.error("Failed to import documents", e);
    }
  }

  private void importDocuments() throws IOException {
    var inputResource = new ClassPathResource("/journey2west/partial.txt");
    var reader = new TextReader(inputResource);
    var splitter = new RecursiveTextSplitter(500, 100);
    var docs = splitter.split(reader.read());
    int count = 0;
    for (List<Document> documents : Lists.partition(docs, 10)) {
      LOGGER.info("#{}, Saving {} docs", count++, documents.size());
      vectorStore.add(documents);
    }
    Files.write(markerFile, new byte[0], StandardOpenOption.CREATE_NEW);
    LOGGER.info("Saved {} docs to vector store", docs.size());
  }
}
