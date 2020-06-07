package com.example.image.repo.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.image.repo.service.AmazonS3ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class AmazonS3ClientServiceImpl implements AmazonS3ClientService {

    private String awS3AudioBucket;
    private AmazonS3 amazonS3;

    @Autowired
    public AmazonS3ClientServiceImpl(Region region, AWSCredentialsProvider awsCredentialsProvider, String awsS3AudioBucket){
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(region.getName()).build();
        this.awS3AudioBucket = awsS3AudioBucket;
    }

    /* These requests are made asynchronous.
       The main thread can continue to serve the user request while a background thread handles this */

    @Async
    public void uploadFileToS3Bucket(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        try{
            File file = new File(fileName);
            FileOutputStream outputStram = new FileOutputStream(file);
            outputStram.write(multipartFile.getBytes());
            this.amazonS3.putObject(new PutObjectRequest(this.awS3AudioBucket, fileName, file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AmazonServiceException e){
            e.printStackTrace();
        }
    }

    @Async
    public void deleteFileFromS3Bucket(String fileName) {
        try{
            amazonS3.deleteObject(new DeleteObjectRequest(awS3AudioBucket, fileName));
        }catch (AmazonServiceException e){
            e.printStackTrace();
        }
    }
}