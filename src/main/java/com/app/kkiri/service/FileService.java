package com.app.kkiri.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class FileService {

	private final Logger LOGGER = LoggerFactory.getLogger(FileService.class);
	private final String bucketName = "kkiri-bucket";
	private final S3Client s3Client;

	public void uploadFileToS3(String fileName, MultipartFile multipartFile) throws IOException {
		// fileName 은 파일의 전체 경로이다. 예) upload/post/2023/11/10/uuid_post.jpg

		try {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(fileName)
				.acl(ObjectCannedACL.BUCKET_OWNER_FULL_CONTROL)
				.build();

			byte[] bytes = multipartFile.getBytes();

			s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		} catch (AwsServiceException e) {
			throw new RuntimeException(e);
		} catch (SdkClientException e) {
			throw new RuntimeException(e);
		}
	}

	public String getS3ObjectURL(String keyName) {

		String result = null;

		try {
			GetUrlRequest request = GetUrlRequest.builder()
				.bucket(bucketName)
				.key(keyName)
				.build();

			URL url = s3Client.utilities().getUrl(request);

			result = url.toString();

		} catch (S3Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}

		return result;
	}

	public void deleteS3Object(String keyName) {

		ArrayList<ObjectIdentifier> toDelete = new ArrayList<>();
		toDelete.add(ObjectIdentifier.builder()
			.key(keyName)
			.build());

		try {
			DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
				.bucket(bucketName)
				.delete(Delete
					.builder()
					.objects(toDelete)
					.build()
				)
				.build();

			s3Client.deleteObjects(deleteObjectsRequest);
			LOGGER.info("[deleteS3Object()] deleted object key name : {}", keyName);

		} catch (S3Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}

	}
}