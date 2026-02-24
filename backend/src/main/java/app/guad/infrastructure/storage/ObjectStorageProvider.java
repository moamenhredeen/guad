package app.guad.infrastructure.storage;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

public interface ObjectStorageProvider {
    /**
     * Uploads a file to object storage
     * 
     * @param key           The unique key/path for the file in storage
     * @param inputStream   The file input stream
     * @param contentType   The MIME type of the file
     * @param contentLength The size of the file in bytes
     * @return The key/path where the file was stored
     * @throws IOException if upload fails
     */
    String upload(String key, InputStream inputStream, String contentType, long contentLength) throws IOException;

    /**
     * Deletes a file from object storage
     * 
     * @param key The key/path of the file to delete
     */
    void delete(String key);

    /**
     * Generates a presigned URL for downloading a file
     * 
     * @param key        The key/path of the file
     * @param expiration The duration until the URL expires
     * @return The presigned URL
     */
    String generatePresignedUrl(String key, Duration expiration);
}
