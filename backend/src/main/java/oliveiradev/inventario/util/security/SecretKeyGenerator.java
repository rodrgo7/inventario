package oliveiradev.inventario.util.security;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    private static final int DEFAULT_KEY_LENGTH_BYTES = 64; // 512 bits for HS512

    public static String generateKey(int keyLengthBytes) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[keyLengthBytes];
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    public static String generateKey() {
        return generateKey(DEFAULT_KEY_LENGTH_BYTES);
    }

    public static void main(String[] args) {
        String base64Key = generateKey();
        
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Chave Secreta JWT Gerada (Base64):");
        System.out.println(base64Key);
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Copie esta chave e cole no seu application.yaml (app.jwt.secret)");
        System.out.println("Lembre-se: esta chave é para o algoritmo HS512 (" + (DEFAULT_KEY_LENGTH_BYTES * 8) + " bits).");
    }
}