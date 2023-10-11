package com.example.myapp;

import com.amazonaws.auth.AWSCredentials;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.TextDetection;
import java.util.List;



public class App2 {

   public static void main(String[] args) throws Exception {
      
  
    String bucket = "njit-cs-643";//define the S3 bucket for the project and our end index
    String endproc = "-1";

    AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();//define our clients
    final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    FileOutputStream fos = new FileOutputStream(new File("ProjectOutput.txt"));//create stream to print to file
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );//create stream to collect text we need as we go
      
    
    while(true){//contoinously check for new messages

        System.out.println("Accepting SQS messages...");

        try {

        List<Message> messages = sqs.receiveMessage("https://sqs.us-east-1.amazonaws.com/631985668315/testDF6-21-1124").getMessages();
    	
    	    for(Message message : messages) {//iterates for all SQS messages


                boolean textdet = false;//used for formating later

              

                if(endproc.equals(message.getBody())){//events that happen when the '-1' queue is received
                    System.out.println("All photos received and checked.");
                    byte c[] = outputStream.toByteArray( );//format all collected text into byte array
                    fos.write(c);//write byte array to output file
                    outputStream.close();//close streams
                    fos.close();
                    sqs.deleteMessage("https://sqs.us-east-1.amazonaws.com/631985668315/testDF6-21-1124", message.getReceiptHandle());//delete 'end of queue' message so project doesnt immediately stop when we try to run it again
                    System.out.println("File written.");
                    System.exit(1);//end program
                }

                
                
    	        System.out.println("Checking image " + message.getBody() + " for text.");


                DetectTextRequest request = new DetectTextRequest()//create detection request
                .withImage(new Image()
                .withS3Object(new S3Object()
                .withName(message.getBody())
                .withBucket(bucket)));
    	    
                
    
                DetectTextResult result = rekognitionClient.detectText(request);//service detection request
                List<TextDetection> textDetections = result.getTextDetections();

         
                    for (TextDetection text: textDetections) {//iterates for every text detected in image

                        textdet = true;

                        System.out.println("Detected lines and words for " + message.getBody() + "\n " + text.getDetectedText());

                        outputStream.write((text.getDetectedText()).getBytes()); // collect test from images in byte stream
                        outputStream.write(("\t").getBytes());
                        
                        System.out.println();
                    }
                if(textdet){//include image key in output only if image contains text
                        String include = message.getBody();
                        byte[] byteArray = include.getBytes();

                        outputStream.write(byteArray);
                        outputStream.write(("\n").getBytes());

                }

                
                 sqs.deleteMessage("https://sqs.us-east-1.amazonaws.com/631985668315/testDF6-21-1124", message.getReceiptHandle());//delete message after processing to clear queue

                
            }
      } catch(AmazonRekognitionException e) {
         e.printStackTrace();
      }
    }
   }
}