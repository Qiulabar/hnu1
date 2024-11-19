package com.smtpmail.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import java.io.InputStream;
import java.util.UUID;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aliyun.oss")
@Data
public class OSSUtil {

	private String endpoint;

	private String bucketName;

	private String accessKeyId;

	private String accessKeySecret;

	/**
	 * 保存文件到OSS
	 * @param is：文件输入流
	 * @param userPrefix：username去除@lunangangster.store后的前缀，如1462749490
	 * @param originalFilename：文件的初始文件名
	 * @return 返回文件访问URL
	 */
	public String saveObject(InputStream is, String userPrefix, String originalFilename) {
		// UUID
		String uuid = UUID.randomUUID().toString();
		// 保存到OSS中的完整路径
		String objectName = "avatar/" + userPrefix + "/" + uuid + FileUtil.getFileSuffix(originalFilename);

		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

		try {
			// 创建PutObject请求。
			ossClient.putObject(bucketName, objectName, is);
			// https://{buckedName}.{endpoint}/{objectName}
			return "https://" + bucketName + "." + endpoint + "/" + objectName;
		} catch (OSSException oe) {
			System.out.println("Caught an OSSException, which means your request made it to OSS, "
					+ "but was rejected with an error response for some reason.");
			System.out.println("Error Message:" + oe.getErrorMessage());
			System.out.println("Error Code:" + oe.getErrorCode());
			System.out.println("Request ID:" + oe.getRequestId());
			System.out.println("Host ID:" + oe.getHostId());
		} catch (ClientException ce) {
			System.out.println("Caught an ClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with OSS, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message:" + ce.getMessage());
		} finally {
			if (ossClient != null) {
				ossClient.shutdown();
			}
		}
		return null;
	}
}
