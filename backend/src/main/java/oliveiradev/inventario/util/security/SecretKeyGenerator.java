package oliveiradev.inventario.util.security;
import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {

    public static void main(String[] args) {
        int keyLengthBytes = 64;

        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[keyLengthBytes];
        secureRandom.nextBytes(key);

        String base64Key = Base64.getEncoder().encodeToString(key);

        System.out.println("--------------------------------------------------------------------");
        System.out.println("Chave Secreta JWT Gerada (Base64):");
        System.out.println(base64Key);
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Copie esta chave e cole no seu application.yaml (app.jwt.secret)");
        System.out.println("Lembre-se: esta chave é para o algoritmo HS512 (" + (keyLengthBytes * 8) + " bits).");
    }
}