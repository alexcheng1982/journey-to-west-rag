package io.github.alexcheng1982.journey2west;

import io.github.alexcheng1982.journey2west.dataimport.DataImportJob;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;
import org.springframework.ai.autoconfigure.ollama.OllamaConnectionDetails;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfiguration {

  @Bean
  public DataImportJob dataImportJob(VectorStore vectorStore,
      EmbeddingModel embeddingModel) {
    return new DataImportJob(vectorStore, embeddingModel);
  }

  @Bean
  public QuestionAnswerAdvisor questionAnswerAdvisor(VectorStore vectorStore) {
    return new QuestionAnswerAdvisor(vectorStore,
        SearchRequest.defaults().withTopK(1));
  }

  @Bean
  public SimpleLoggerAdvisor simpleLoggerAdvisor() {
    return new SimpleLoggerAdvisor();
  }

  @Bean
  public OllamaApi ollamaApi(OllamaConnectionDetails connectionDetails) {
    JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(
        HttpClient.newHttpClient(),
        Executors.newThreadPerTaskExecutor(Thread.ofVirtual()
            .name("ollama-http-client", 1)
            .factory()));
    requestFactory.setReadTimeout(Duration.ofMinutes(3));
    var restClientBuilder = RestClient.builder().requestFactory(requestFactory);
    return new OllamaApi(connectionDetails.getBaseUrl(), restClientBuilder,
        WebClient.builder());
  }
}
