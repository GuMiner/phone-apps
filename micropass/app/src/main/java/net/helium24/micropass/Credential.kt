package net.helium24.micropass

import java.security.spec.AlgorithmParameterSpec
import java.security.spec.KeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class Credential(val Name: String) {
    fun DecryptCredential(password: String, base64Value: String): String {
        // https://github.com/GuMiner/CommonNet/blob/master/CommonNet/Cryptography/Aes256ByteEncoder.cs
        val defaultSalt = "CodeLibraryCryptography_abc123^%$".toByteArray()

        // https://stackoverflow.com/questions/24405731/rfc2898derivebytes-in-java
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), defaultSalt, 1000, 384)
        val secretKey = factory.generateSecret(spec)
        val secret = SecretKeySpec(secretKey.encoded, "AES")

        val key = ByteArray(32)
        val iv = ByteArray(16)
        System.arraycopy(secretKey.encoded, 0, key, 0, 32)
        System.arraycopy(secretKey.encoded, 32, iv, 0, 16)
        val ivSpec: AlgorithmParameterSpec = IvParameterSpec(iv)


        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), ivSpec)

        val encodedBytes = Base64.getDecoder().decode(base64Value);
        val encodedBytesWithoutIvCount = ByteArray(encodedBytes.size - 4)

        System.arraycopy(encodedBytes, 4, encodedBytesWithoutIvCount, 0, encodedBytesWithoutIvCount.size)
        val result = cipher.doFinal(encodedBytesWithoutIvCount)
        val resultString = result.toString()

        return resultString
    }
}