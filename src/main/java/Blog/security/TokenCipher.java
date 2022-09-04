package Blog.security;


import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AeadFactory;

import javax.xml.bind.DatatypeConverter;

public class TokenCipher {

    public TokenCipher() throws Exception {
        AeadConfig.register();
    }

    public String cipherToken(String jwt, KeysetHandle keysetHandle) throws Exception {

        if (jwt == null || jwt.isEmpty() || keysetHandle == null) {
            throw new IllegalArgumentException("Both parameters must be specified!");
        }


        Aead aead = AeadFactory.getPrimitive(keysetHandle);


        byte[] cipheredToken = aead.encrypt(jwt.getBytes(), null);

        return DatatypeConverter.printHexBinary(cipheredToken);
    }

    public String decipherToken(String jwtInHex, KeysetHandle keysetHandle) throws Exception {

        if (jwtInHex == null || jwtInHex.isEmpty() || keysetHandle == null) {
            throw new IllegalArgumentException("Both parameters must be specified !");
        }

        byte[] cipheredToken = DatatypeConverter.parseHexBinary(jwtInHex);


        Aead aead = AeadFactory.getPrimitive(keysetHandle);

        byte[] decipheredToken = aead.decrypt(cipheredToken, null);

        return new String(decipheredToken);
    }

}