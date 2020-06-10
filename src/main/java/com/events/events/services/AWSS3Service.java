package com.events.events.services;

import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface AWSS3Service {

    /**
     * Uploads a file(usually an image) to S3 and returns the filename
     * @param multipartFile
     * @return
     */
    String uploadFile(final MultipartFile multipartFile, String folder);


    byte[] downloadFile(final String fileName);

    /**
     * Deletes the file given the filename
     * @param fileName
     */
    void deleteFile(final String fileName);

    /**
     * Returns a signed URL given the file name
     * @param fileName
     * @return
     */
    URL getPreSignedUrl(String fileName);
}
