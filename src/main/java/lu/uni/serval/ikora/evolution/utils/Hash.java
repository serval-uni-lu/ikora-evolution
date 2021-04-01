package lu.uni.serval.ikora.evolution.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    private static final Logger logger = LogManager.getLogger(Hash.class);

    private static MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to generate SHA-512 hash generator");
        }
    }

    private Hash() {}

    public static String sha512(String text){
        md.update(text.getBytes());
        return DatatypeConverter.printHexBinary(md.digest());
    }
}
