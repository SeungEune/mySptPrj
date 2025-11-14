package biz.util;

import egovframework.com.cmm.service.EgovProperties;
import org.egovframe.rte.fdl.cryptography.EgovPasswordEncoder;
import org.egovframe.rte.fdl.cryptography.impl.EgovARIACryptoServiceImpl;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * DB 접속 정보 암호화 유틸리티
 * 개발자가 DB 접속 정보를 암호화하여 properties 파일에 사용할 수 있도록 도움을 주는 클래스
 *
 * @author 공통서비스 개발팀
 * @since 2024.12.19
 * @version 1.0
 */
public class DbPropertyEncryptor {

    public static void main(String[] args) throws Exception {

        // 새로운 암호화 키
        String plainPassword = EgovProperties.getProperty("Globals.crypto.algoritm");

        // SHA-256으로 해시 생성
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(plainPassword.getBytes("UTF-8"));
        String hashedPassword = Base64.getEncoder().encodeToString(hash);

        System.out.println("=== 암호화 키 해시 생성 ===");
        System.out.println("원본 키: " + plainPassword);
        System.out.println("해시된 키: " + hashedPassword);
        System.out.println();


        // 암호화 서비스 설정 (EgovConfigAppCommon과 동일한 설정 사용)
        EgovPasswordEncoder passwordEncoder = new EgovPasswordEncoder();
        passwordEncoder.setAlgorithm("SHA-256");
        // "spatialt" 키의 SHA-256 해시
        passwordEncoder.setHashedPassword("wpp6OF9MWa6jXV0/IvhupCSEf7sgr3Gn5U37vKuvHhQ=");

        EgovARIACryptoServiceImpl cryptoService = new EgovARIACryptoServiceImpl();
        cryptoService.setPasswordEncoder(passwordEncoder);
        cryptoService.setBlockSize(1024);

        // 개발 환경 DB 접속 정보
        String[] devPlainTexts = {
            "org.postgresql.Driver",
            "jdbc:postgresql://localhost:port/database",
            "user",
            "password",
        };
        
        System.out.println("=== 개발환경 암호화된 DB 접속 정보 ===");
        for (int i = 0; i < devPlainTexts.length; i++) {
            String plainText = devPlainTexts[i];
            byte[] encrypted = cryptoService.encrypt(plainText.getBytes("UTF-8"), plainPassword);
            String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);
            System.out.println("평문: " + plainText);
            System.out.println("암호화: " + encryptedBase64);
            System.out.println();
        }

        System.out.println("=== Properties 파일 적용 예시 (개발환경) ===");
        byte[] encryptedDriver = cryptoService.encrypt(devPlainTexts[0].getBytes("UTF-8"), plainPassword);
        byte[] encryptedUrl = cryptoService.encrypt(devPlainTexts[1].getBytes("UTF-8"), plainPassword);
        byte[] encryptedUserName = cryptoService.encrypt(devPlainTexts[2].getBytes("UTF-8"), plainPassword);
        byte[] encryptedPassword = cryptoService.encrypt(devPlainTexts[3].getBytes("UTF-8"), plainPassword);
        
        System.out.println("Globals.postgres.DriverClassName=" + Base64.getEncoder().encodeToString(encryptedDriver));
        System.out.println("Globals.postgres.Url=" + Base64.getEncoder().encodeToString(encryptedUrl));
        System.out.println("Globals.postgres.UserName=" + Base64.getEncoder().encodeToString(encryptedUserName));
        System.out.println("Globals.postgres.Password=" + Base64.getEncoder().encodeToString(encryptedPassword));
    }


}
