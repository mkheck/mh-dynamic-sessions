package com.thehecklers.mh_dynamic_sessions;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Conductor {
    @Value("${region:Undefined env var 'region'}")
    private String region;
    @Value("${subscriptionId:Undefined env var 'subscriptionId'}")
    private String subscriptionId;
    @Value("${resourceGroup:Undefined env var 'resourceGroup'}")
    private String resourceGroup;
    @Value("${sessionPoolName:Undefined env var 'sessionPoolName'}")
    private String sessionPoolName;
    @Value("${sessionId:Undefined env var 'sessionId'}")
    private String sessionId;
    @Value("${filename:Undefined env var 'filename'}")
    private String filename;

    private final CodeExecutor executor;
    private final FileUploader uploader;
    private final FileLister lister;
    private final FileDownloader downloader;

    public Conductor(CodeExecutor executor, FileUploader uploader, FileLister lister, FileDownloader downloader) {
        this.executor = executor;
        this.uploader = uploader;
        this.lister = lister;
        this.downloader = downloader;
    }

    @PostConstruct
    public void doThis() {
        System.out.println("Region: " + region);
        System.out.println("SubscriptionId: " + subscriptionId);
        System.out.println("ResourceGroup: " + resourceGroup);
        System.out.println("SessionPoolName: " + sessionPoolName);
        System.out.println("SessionId: " + sessionId);
        System.out.println("Filename: " + filename);

        // Get the token using DefaultAzureCredential
        final String token = getToken();
        final String eUrl = String.format("https://%s.dynamicsessions.io/subscriptions/%s/resourceGroups/%s/sessionPools/%s/code/execute?api-version=2024-02-02-preview&identifier=%s",
                region, subscriptionId, resourceGroup, sessionPoolName, sessionId);
        final String uUrl = String.format("https://%s.dynamicsessions.io/subscriptions/%s/resourceGroups/%s/sessionPools/%s/files/upload?api-version=2024-02-02-preview&identifier=%s",
                region, subscriptionId, resourceGroup, sessionPoolName, sessionId);
        final String lUrl = String.format("https://%s.dynamicsessions.io/subscriptions/%s/resourceGroups/%s/sessionPools/%s/files?api-version=2024-02-02-preview&identifier=%s",
                region, subscriptionId, resourceGroup, sessionPoolName, sessionId);

        try {
            System.out.println("\nRunning ExecuteCode with URL: " + eUrl + "\n");
            executor.execute(eUrl, token);

            System.out.println("\nUploading filename: " + filename + " \nwith URL: " + uUrl + "\n");
            uploader.upload(uUrl, token, filename);

            System.out.println("\nRunning ListFiles with URL: " + lUrl + "\n");
            String gfilename = lister.list(lUrl, token);
            System.out.println("\nFiles found: \n" + gfilename);

            String dlUrl = String.format("https://%s.dynamicsessions.io/subscriptions/%s/resourceGroups/%s/sessionPools/%s/files/content/%s/?api-version=2024-02-02-preview&identifier=%s",
                    region, subscriptionId, resourceGroup, sessionPoolName, gfilename, sessionId);
            System.out.println("\nDownloading file: " + gfilename + " \n with URL: " + dlUrl + "\n");
            downloader.download(dlUrl, token);
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
