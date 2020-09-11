package nl.utwente.di.security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;

public class Encryption {

    private String sessionPassword;
    private SecretKeySpec key;

    /**
     * Creates a new encryption object by creating a salt, which is then used to create the cookies.
     */
    public Encryption() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890;";
        StringBuilder salt = new StringBuilder();
        SecureRandom srnd = new SecureRandom();
        while (salt.length() < 20) {
            int index = (int) (srnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        this.sessionPassword = salt.toString();
    }

    /**
     * Encripts the message using AES encryption and iterates how many times the encryption should be done.
     * @param message the user wants to encrypt.
     * @return the encrypted message.
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidParameterSpecException
     * @throws UnsupportedEncodingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public String encrypt(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, InvalidKeyException {
        this.key = createSeceretKey(this.sessionPassword);
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(message.getBytes("UTF-8"));
        byte[] iv = ivParameterSpec.getIV();
        String encodedIV = Base64.getEncoder().encodeToString(iv).replace("==", "");
        String encodedCryptoText = Base64.getEncoder().encodeToString(cryptoText).replace("==", "");
        return new String(Base64.getEncoder().encode((encodedIV + ":" + encodedCryptoText).getBytes()));
    }

    /**
     * Decrypts the message.
     * @param string the message the user wants to decrypt.
     * @return the decrypted message.
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws UnsupportedEncodingException
     */
    public String decrypt(String string) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        string = new String (Base64.getDecoder().decode(string));
        String iv = string.split(":")[0].concat("==");
        String message = string.split(":")[1].concat("==");
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Base64.getDecoder().decode(iv)));
        return new String(pbeCipher.doFinal(Base64.getDecoder().decode(message)), "UTF-8");
    }

    /**
     * Creates a secret key from the salt which is used to encrypt the message.
     * @param sessionPassword which is the salt.
     * @return the secret key generated.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private SecretKeySpec createSeceretKey(String sessionPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] password = sessionPassword.toCharArray();
        if (password == null) {
            throw new IllegalArgumentException("");
        }
        byte[] salt = new String("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789").getBytes();
        int iteration = 100000;
        int keyLength = 128;
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iteration, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    /**
     * This one is just used for testing.
     * @param args
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws InvalidParameterSpecException
     * @throws InvalidAlgorithmParameterException
     */
    public static void main(String[] args) throws NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException, InvalidParameterSpecException, InvalidAlgorithmParameterException {
        Encryption e = new Encryption();
        String mesaj = "Un text anume";
        mesaj = e.encrypt(mesaj);
        System.out.println(mesaj);
        Encryption e1 = new Encryption();
        System.out.println(e.decrypt(mesaj));
    }

}
