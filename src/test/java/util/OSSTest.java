package util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.smtpmail.start.SmtpMailApplication;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SmtpMailApplication.class)
public class OSSTest {

	/**
	 * 上传文件
	 */
	@Test
	public void test01() throws FileNotFoundException {
		// username去除@lunangangster.store后的前缀
		String userPrefix = "1462749490";
		// UUID
		String uuid = UUID.randomUUID().toString();
		// Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
		String endpoint = "https://oss-cn-beijing.aliyuncs.com";
		// 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
		String accessKeyId = "LTAI5t5vGP7CA59LTCCjeSjN";
		String accessKeySecret = "gWjzOIjttaFT8LiEiJRurFbu6GHCML";
		// 填写Bucket名称，例如examplebucket。
		String bucketName = "smtp-mail-kid";
		// 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
		// 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
		// 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
		String filePath= "C:\\Users\\DELL\\Pictures\\Saved Pictures\\kid.jpg";
		String objectName = "avatar/" + userPrefix + "/" + uuid + ".jpg";

		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

		try {
			InputStream inputStream = new FileInputStream(filePath);
			// 创建PutObject请求。
			ossClient.putObject(bucketName, objectName, inputStream);
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
	}
}
