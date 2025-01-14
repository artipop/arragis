package io.deffun.arragis;

import io.deffun.arragis.gservices.GDriveFile;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/docs")
public class DocsController {
    private final DocsService docsService;

    public DocsController(DocsService docsService) {
        this.docsService = docsService;
    }

    @GetMapping("/gdocs")
    public List<GDriveFile> listGDocs(
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient
    ) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        return docsService.listGDocs(accessToken);
    }

    @PostMapping("/gdocs/load")
    @ResponseStatus(HttpStatus.CREATED)
    public void loadGDocs(
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
            @RequestBody List<String> body
    ) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        docsService.loadGDocs(body, accessToken);
    }
}
