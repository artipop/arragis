package io.deffun.arragis.gservices;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.docs.v1.model.StructuralElement;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.util.InMemoryResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Service
public class GDocsService {
    private static final String G_DOC_MIMETYPE = "application/vnd.google-apps.document";

    private final VectorStore vectorStore;
    private final String applicationName;
    private final HttpTransport httpTransport;
    private final JsonFactory jsonFactory;

    public GDocsService(VectorStore vectorStore, @Value("google.application-name") String applicationName,
                        HttpTransport httpTransport, JsonFactory jsonFactory) {
        this.vectorStore = vectorStore;
        this.applicationName = applicationName;
        this.httpTransport = httpTransport;
        this.jsonFactory = jsonFactory;
    }

    public List<GDriveFile> listFiles(String accessToken) {
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod())
                .setAccessToken(accessToken);

        Drive drive = new Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(applicationName)
                .build();

        try {
            FileList execute = drive.files().list()
                    // filter docs only
                    .setQ("mimeType='%s'".formatted(G_DOC_MIMETYPE))
                    // for paging
//                    .setPageSize(10)
//                    .setFields("nextPageToken,files(id,name,kind,mimeType)")
                    .execute();
            List<File> files = execute.getFiles();
            return files.stream()
                    // URL – https://docs.google.com/document/d/${id}/edit?usp=sharing
                    .map(f -> new GDriveFile(f.getId(), f.getName()))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void loadFile(String documentId, String accessToken) {
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod())
                .setAccessToken(accessToken);

        Docs docs = new Docs.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(applicationName)
                .build();

        try {
            Document document = docs.documents().get(documentId)
                    .execute();
            List<StructuralElement> content = document.getBody().getContent();
            String stringContent = GDocsReader.readStructuralElements(content);
            Resource resource = new InMemoryResource(stringContent);
            TextReader textReader = new TextReader(resource);
            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
            this.vectorStore.accept(tokenTextSplitter.apply(textReader.get()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
