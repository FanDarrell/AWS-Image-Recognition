package com.example.myapp;

import com.amazonaws.auth.AWSCredentials;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;

import java.util.List;


public class App1 {
    public static void main(String[] args) {
    	

        String bucket_name = "njit-cs-643";//Initializing S3 bucket for this project, as well as other variables
        String key_name = "0.jpg";
        String check = "Car";
        String endproc = "-1"; 

        
       
        AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();//Establishing credentials and clients for all AWS services needed
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        AmazonRekognitionClient rekognitionClient = new AmazonRekognitionClient(credentials)
                    .withRegion(RegionUtils.getRegion("us-east-1"));
        
        ListObjectsV2Result result = s3.listObjectsV2(bucket_name);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) { //For loop iterates through all objects in S3 bucket
            
            
            
            key_name = os.getKey();// Key name updates to current bucket object
             


        try {
        	DetectLabelsRequest request = new DetectLabelsRequest()//Label detection initialized for each bucket object
                    .withImage(new Image().withS3Object(new S3Object().withName(key_name).withBucket(bucket_name)))
                    .withMaxLabels(10)
                    .withMinConfidence(90F);//only accept labels over 90% confidence like assignment specifies

            

            DetectLabelsResult result2 = rekognitionClient.detectLabels(request);//Labels received 
           

            
            for (Label label : result2.getLabels()) {//Iterates through each label, checks if any of them is car
                
                String target = label.getName();
                boolean retval1 = target.equals(check);
                
             
                if(retval1){//If a label generated is "car", we send the key name to the SQS
            	    SendMessageRequest send_msg_request = new SendMessageRequest()//if it is send index to queue
    	                .withQueueUrl("https://sqs.us-east-1.amazonaws.com/631985668315/testDF6-21-1124")
    	                .withMessageBody(key_name);

                         System.out.println("Car detected in bucket object: " + key_name + ", sending to SQS queue.");

    	            sqs.sendMessage(send_msg_request);

                    
                }
                
                       
            }
            

            System.out.println("\n");
            
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } 
        
        }




        SendMessageRequest send_msg_request = new SendMessageRequest()//after we've looked at all images send -1
    	                .withQueueUrl("https://sqs.us-east-1.amazonaws.com/631985668315/testDF6-21-1124")
    	                .withMessageBody(endproc)
    	                .withDelaySeconds(5);

                        

    	            sqs.sendMessage(send_msg_request);

        System.out.println("All Done!");
        
    }
}


