package com.example.easymove


import com.example.easymove.View.LoginFragment
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.SignupViewModel
import com.example.easymove.repository.UserRepository
import org.junit.Test
import org.junit.Assert.*
import com.example.easymove.ViewModel.VeicoliViewModel


class ExampleUnitTest {
    private var userrep = UserRepository()
    var signupViewModel = SignupViewModel(userrep)
    var richiestaViewModel = RichiestaViewModel()
    val veicoliViewModel = VeicoliViewModel()



    @Test
    fun testPassword() {
        val validPassword = "Test123@"
        val invalidPassword = "weakpass"
        val shortPassword = "T3@S1"

        val resultvalid = signupViewModel.checkPassword(validPassword)
        val resultinvalid = signupViewModel.checkPassword(invalidPassword)
        val resultshort = signupViewModel.checkPassword(shortPassword)

        assertTrue(resultvalid)
        assertFalse(resultinvalid)
        assertFalse(resultshort)
    }


    @Test
    fun testCheckDate() {
        val futureDate = "31/12/2025"
        val pastDate = "01/01/2000"

        val resultFuture = richiestaViewModel.checkDate(futureDate)
        val resultPast = richiestaViewModel.checkDate(pastDate)

        assertTrue(resultFuture)
        assertFalse(resultPast)
    }
    @Test
    fun testCalcoloCapienza() {
        // Valori di input
        val lunghezza = 50.0
        val altezza = 30.0
        val larghezza = 20.0

        // Calcola il risultato atteso
        val expectedCapienza = "0,03 m³"

        // Chiama la funzione
        val result = veicoliViewModel.calcoloCapienza(lunghezza, altezza, larghezza)

        // Verifica se il risultato è uguale all'aspettativa
        assertEquals(expectedCapienza, result)
    }

    @Test
    fun testIsValidItalianLicensePlate() {
        val validLicensePlate = "AB123CD"
        val invalidLicensePlate = "1234AB"

        // Verifica per la targa valida
        val validResult = veicoliViewModel.isValidItalianLicensePlate(validLicensePlate)
        assertEquals(true, validResult)

        // Verifica per la targa non valida
        val invalidResult = veicoliViewModel.isValidItalianLicensePlate(invalidLicensePlate)
        assertEquals(false, invalidResult)
    }


}