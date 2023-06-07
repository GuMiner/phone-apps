package net.helium24.micropass

import org.junit.Test

import org.junit.Assert.*

/**
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CredentialTest {
    @Test
    fun credential_decryption_isCorrect() {
        // With real credentials, this works, although the IV stripping logic needs copying still
        val decryptedCred = Credential("test").DecryptCredential(
            "testKey",
            "EAAAAFeKLjg286jKMGM3IAcMf15zL3py5Ki45NTalikl6YB8jVjdci/hhzQjM67y680aNw=="
        )
        assertEquals(decryptedCred, "testValue")
    }
}
