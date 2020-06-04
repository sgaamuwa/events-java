package com.events.events.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class AWSS3ServiceImpl implements AWSS3Service{

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3ServiceImpl.class);

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    @Async
    public String uploadFile(MultipartFile multipartFile) {
        LOGGER.info("Uploading File to S3 in progress");
        String fileName = null;
        try{
            final File file = convertMultiPartFileToFile(multipartFile);
            fileName = uploadFileToS3Bucket(bucketName, file);
            LOGGER.info("Upload complete");
            file.delete();
        }catch(AmazonServiceException exception){
            LOGGER.info("File upload is failed.");
            LOGGER.error("Error while uploading file = ", exception.getMessage());
        }
        return fileName;
    }

    @Override
    @Async
    public void downloadFile(String fileName) {

    }

    @Override
    @Async
    public void deleteFile(String fileName) {

    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        // create a new file with the same name as the multipart file
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            // write the contents of the multipart file to the new file
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException exception) {
            LOGGER.error("Error converting the multi-part file to file= ", exception.getMessage());
        }
        return file;
    }

    private String uploadFileToS3Bucket(final String bucketName, final File file) {
        final String uniqueFileName = LocalDateTime.now() + "_" + file.getName();
        LOGGER.info("Uploading file with name= " + uniqueFileName);
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
        amazonS3.putObject(putObjectRequest);
        return uniqueFileName;
    }
}
