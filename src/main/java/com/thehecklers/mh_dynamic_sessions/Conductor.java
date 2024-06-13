package com.thehecklers.mh_dynamic_sessions;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class Conductor {
    private final Dynaprops props;
    private final CodeExecutor executor;
    private final FileUploader uploader;
    private final FileLister lister;
    private final FileDownloader downloader;

    public Conductor(Dynaprops props, CodeExecutor executor, FileUploader uploader, FileLister lister, FileDownloader downloader) {
        this.props = props;
        this.executor = executor;
        this.uploader = uploader;
        this.lister = lister;
        this.downloader = downloader;
    }

    @PostConstruct
    public void doThis() {
        // Get the token using DefaultAzureCredential
        final String token = getToken();

        // Build base URL, create variations required for various operations
        final String baseUrl = String.format("https://%s.dynamicsessions.io/subscriptions/%s/resourceGroups/%s/sessionPools/%s",
                props.region(), props.subscriptionId(), props.resourceGroup(), props.sessionPoolName());

        final String eUrl = baseUrl.concat(
                String.format("/code/execute?api-version=2024-02-02-preview&identifier=%s",
                        props.sessionId()));
        final String uUrl = baseUrl.concat(
                String.format("/files/upload?api-version=2024-02-02-preview&identifier=%s",
                        props.sessionId()));
        final String lUrl = baseUrl.concat(
                String.format("/files?api-version=2024-02-02-preview&identifier=%s",
                        props.sessionId()));

        try {
            System.out.println("\nRunning ExecuteCode with URL: " + eUrl + "\n");
            System.out.println(executor.execute(eUrl, token));

            System.out.println("\nUploading filename: " + props.filename() + " \nwith URL: " + uUrl + "\n");
            System.out.println(uploader.upload(uUrl, token, props.filename()));

            System.out.println("\nRetrieving list of files with URL: " + lUrl + "\n");
            final String gfilename = lister.list(lUrl, token);
            System.out.println("Files found:\n" + gfilename);

            final String dlUrl = baseUrl.concat(
                    String.format("/files/content/%s/?api-version=2024-02-02-preview&identifier=%s",
                            gfilename, props.sessionId()));
            System.out.println("\nDownloading file: " + gfilename + " \n with URL: " + dlUrl + "\n");
            System.out.println(downloader.download(dlUrl, token));
        } catch (Exception e) {
            throw new RuntimeException("Something has gone terribly wrong.", e);
        }
    }

    private String getToken() {
        DefaultAzureCredential credential = new DefaultAzureCredentialBuilder().build();
        TokenRequestContext requestContext = new TokenRequestContext().addScopes("https://dynamicsessions.io/.default");
        return credential.getToken(requestContext).block().getToken();
    }
}
