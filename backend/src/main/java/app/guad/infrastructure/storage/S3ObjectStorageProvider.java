package app.guad.infrastructure.storage;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;

@Component
public class S3ObjectStorageProvider implements ObjectStorageProvider {

    private final S3Template s3Template;
    private final String bucketName;

    public S3ObjectStorageProvider(
            S3Template s3Template,
            @Value("${spring.cloud.aws.s3.bucket}") String bucketName) {
        this.s3Template = s3Template;
        this.bucketName = bucketName;
    }

    @Override
    public String upload(String key, InputStream inputStream, String contentType, long contentLength)
            throws IOException {
        ObjectMetadata objectMetadata = ObjectMetadata.builder()
                .contentType(contentType)
                .build();

        s3Template.upload(bucketName, key, inputStream, objectMetadata);
        return key;
    }

    @Override
    public void delete(String key) {
        s3Template.deleteObject(bucketName, key);
    }

    @Override
    public String generatePresignedUrl(String key, Duration expiration) {
        URL signedUrl = s3Template.createSignedGetURL(bucketName, key, expiration);
        return signedUrl.toString();
    }
}
