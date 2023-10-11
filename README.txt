This is the compilation and run instructions for the two associated programs.

1. Log into the AWS academy page and navigate to "Learner Lab" under the 'modules' section of the course.

2. Click the 'Start Lab' button and wait the for the red circle next to 'AWS' to turn green, then right click on that 'AWS' and open it in a new tab. This is the AWS management console.

3. Before leaving this tab, click on "AWS details" and next to 'AWS CLI' click the "show" button

4. Copy and past the keys in the text box in a text file named 'credentials' in new subfolder called .aws in a directory you'll remember on your local machine

5. Navigate to the AWS concsole, search for EC2, and click the launch instance button

6. Name the instance if you'd like, select the amazon linux AWI, pick a key pair or create a new one and save the associated 'key-name-here'.pem in the same directory that houses your .aws folder. Configure your security group so that only your IP address can access the instance

7. from the directory that houses your pem key and your .aws folder with your credentials in it, ssh into your instance



COMPLETE STEPS 8-17 FOR BOTH INSTANCES



8. $ sudo yum install java-devel

9. $ sudo yum install maven

10. $ mvn -B archetype:generate \
 -DarchetypeGroupId=software.amazon.awssdk \
 -DarchetypeArtifactId=archetype-lambda -Dservice=s3 -Dservice=sQS -Dservice=rekognition -Dregion=US_EAST_1 \
 -DarchetypeVersion=2.20.88 \
 -DgroupId=com.example.myapp \
 -DartifactId=myapp

11. Create an .aws folder on your instance home, in it put a text file named 'credentials' with the same credentials you copied from the LearnerLab earlier

12. navigate into the myapp folder

13. In the myapp/src/ subdirectory, delete the test subdirectory, and any java files in /src/main/java/com/example/myapp/, place App1/2 on their respective instances

14. Copy this into your pom.xml file in your myapp subdirectoy:

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example.myapp</groupId>
    <artifactId>myapp</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <maven.shade.plugin.version>3.2.1</maven.shade.plugin.version>
        <maven.compiler.plugin.version>3.6.1</maven.compiler.plugin.version>
        <exec-maven-plugin.version>1.6.0</exec-maven-plugin.version>
        <aws.java.sdk.version>2.20.88</aws.java.sdk.version>
        <aws.lambda.java.version>1.2.0</aws.lambda.java.version>
        <junit5.version>5.8.1</junit5.version>
    </properties>

     <dependencies>
  <dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-s3</artifactId>
    <version>1.12.490</version>
  </dependency>
  <dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-rekognition</artifactId>
    <version>1.12.490</version>
  </dependency>
    <dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-sqs</artifactId>
    <version>1.12.490</version>
  </dependency>
</dependencies>

       
 
  

    <build>
        <plugins>
             <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <version>2.4</version>
            <configuration>
                <archive>
                    <index>true</index>
                    <manifest>
                        <mainClass>com.example.myapp.App1/2</mainClass>
                    </manifest>
                </archive>
            </configuration>
        </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>${maven.shade.plugin.version}</version>
                <configuration>
                    <createDependencyReducedPom>false</createDependencyReducedPom>
                    <finalName>myapp</finalName>
                    <filters>
                        <filter>
                            <artifact>*:*</artifact>
                            <excludes>
                                <!-- Suppress module-info.class warning-->
                                <exclude>module-info.class</exclude>
                            </excludes>
                        </filter>
                    </filters>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>

15. Make sure to change all mentions of App1/2 to the one the matches the java file on your instance

16. navigate to your myapp subdirectory

17. $ mvn package

18. On the instance with App2, run it first by entering this:

$ java -jar target/myapp.jar

19. Once it's running and the terminal indicates it's waiting for SQS messages, on the terminal that is home to App1, run the same command

20. You have now successfully run the program, inspect the ProjectOutput.txt file in the myapp directory on instance 2 for the output of what indexes have text, and what the text detected was.
