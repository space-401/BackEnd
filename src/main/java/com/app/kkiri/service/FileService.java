package com.app.kkiri.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {

	private final AmazonS3 amazonS3;
	private final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	@Value("${aws.kms.key-id}")
	private String kmsKeyId;

	public void uploadFile(MultipartFile multipartFile, String fileName) throws IOException {

		// fileName은 파일 전체 경로가 포함된 파일 이름이다.
		// 2023/11/09/uuid_dog.jpg

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());

		// 파일을 S3에 저장한다.
		amazonS3.putObject(bucketName, fileName, multipartFile.getInputStream(), metadata);

		// 파일이 저장된 URL을 return 한다.
		// return amazonS3.getUrl(bucket, fileName).toString();
	}

	public ResponseEntity<UrlResource> downloadFile(String fileName) throws UnsupportedEncodingException {

		// 파일 전체 경로가 포함된 파일 이름을 통해 파일을 가져온다.
		UrlResource urlResource = new UrlResource(amazonS3.getUrl(bucketName, fileName));

		// 파일의 원본 이름 추출
		// dog.jpg
		String originalFileName = urlResource.getFilename();
		originalFileName = originalFileName.substring(originalFileName.indexOf("_") + 1);

		// String contentDisposition = "attachment;filename=" + new String(originalFilename.getBytes("UTF-8"), "ISO-8859-1");
		String contentDisposition = "attachment; filename=\"" +  originalFileName + "\"";

		// header에 CONTENT_DISPOSITION 설정을 통해 클릭 시 다운로드 진행
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
			.body(urlResource);
	}

	public String displayFile(String fileName) {
		return amazonS3.getUrl(bucketName, fileName).toString();
	}

	public void deleteFile(String fileName) {
		amazonS3.deleteObject(bucketName, fileName);
	}

	public String decrypt(String objectKey) throws Exception {

		try {
			AWSKMS kmsClient = AWSKMSClientBuilder.standard()
				.withRegion(Regions.AP_NORTHEAST_2)
				.build();

			String s3Object = amazonS3.getObjectAsString(bucketName, objectKey);
			LOGGER.info("[decrypt()] s3Object : {}", s3Object);

			DecryptRequest request = new DecryptRequest();
			request.withCiphertextBlob(ByteBuffer.wrap(Base64.decodeBase64(s3Object)));
			request.withKeyId(kmsKeyId);
			request.withEncryptionAlgorithm(EncryptionAlgorithmSpec.RSAES_OAEP_SHA_256);

			ByteBuffer plainText = kmsClient.decrypt(request).getPlaintext();
			LOGGER.info("[decrypt()] plainText : {}", plainText);

			return new String(plainText.array());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

}