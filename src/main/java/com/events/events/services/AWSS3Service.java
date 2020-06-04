package com.events.events.services;

import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Service {

    /**
     * Uploads a file(usually an image) to S3 and returns the filename
     * @param multipartFile
     * @return
     */
    String uploadFile(final MultipartFile multipartFile);


    void downloadFile(final String fileName);

    /**
     * Deletes the file given the filename
     * @param fileName
     */
    void deleteFile(final String fileName);
}
