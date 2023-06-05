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
        val decryptedCred = Credential("abc").DecryptCredential("", "")
        assertEquals(decryptedCred, "abc")
        assertEquals(4, 2 + 2)
    }
}