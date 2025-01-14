package io.deffun.arragis;

import io.deffun.arragis.gservices.GDocsService;
import io.deffun.arragis.gservices.GDriveFile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocsService {
    private final GDocsService gDocsService;

    public DocsService(GDocsService gDocsService) {
        this.gDocsService = gDocsService;
    }

    public List<GDriveFile> listGDocs(String accessToken) {
        return gDocsService.listFiles(accessToken);
    }

    public void loadGDocs(List<String> ids, String accessToken) {
        for (String id : ids) {
            gDocsService.loadFile(id, accessToken);
        }
    }
}
