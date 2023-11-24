package com.app.kkiri.service;

import static com.app.kkiri.global.exception.ExceptionCode.*;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.app.kkiri.global.exception.ImageException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {

	private final Logger LOGGER = LoggerFactory.getLogger(FileService.class);
	private final AmazonS3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public void uploadFileToS3(String fileName, MultipartFile multipartFile) {
		LOGGER.info("[uploadFileToS3()] param fileName : {}", fileName);
		LOGGER.info("[uploadFileToS3()] param multipartFile.getName() : {}", multipartFile.getName());
		LOGGER.info("[uploadFileToS3()] param multipartFile.getSize() : {}", (double)multipartFile.getSize()/1024/1024 + "MB");

		try {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType(multipartFile.getContentType());
			objectMetadata.setContentLength(multipartFile.getSize());
			amazonS3Client.putObject(bucket, fileName, multipartFile.getInputStream(), objectMetadata);
		} catch (IOException ex) {
			throw new ImageException(FAIL_TO_UPLOAD_IMAGE);
		}
	}

	public String getS3ObjectURL(String keyName) {
		LOGGER.info("[getS3ObjectURL()] param keyName : {}", keyName);

		try {
			String imgUrl = amazonS3Client.getUrl(bucket, keyName).toString();
			LOGGER.info("[getS3ObjectURL()] imgUrl : {}", imgUrl);

			return imgUrl;
		} catch (Exception e) {
			throw new ImageException(FAIL_TO_GET_IMAGE_URL);
		}
	}

	public void deleteS3Object(String keyName) {
		LOGGER.info("[deleteS3Object()] param keyName : {}", keyName);

		try {
			amazonS3Client.deleteObject(bucket, keyName);
		} catch (Exception e) {
			throw new ImageException(FAIL_TO_GET_IMAGE_URL);
		}
	}
}