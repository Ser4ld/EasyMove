package com.example.easymove

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.easymove.View.LoginFragment
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.SignupViewModel
import com.example.easymove.repository.UserRepository
import com.example.easymove.View.HomeFragment
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentManager


class ExampleUnitTest {
    private var userrep = UserRepository()
    var signupViewModel = SignupViewModel(userrep)
    var richiestaViewModel = RichiestaViewModel()
    val loginFragment = LoginFragment()



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

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun clickOnLoginButton(){

        (loginFragment.view?.findViewById<View>(R.id.Email) as EditText).setText("lucarossi@example.com")
        (loginFragment.view?.findViewById<View>(R.id.Password) as EditText).setText("Luca123@")
        (loginFragment.view?.findViewById<View>(R.id.login) as Button).performClick()

        // Get the fragment name after the button click
        val fragmentManager = loginFragment.fragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        val currentFragment = fragmentManager?.findFragmentById(R.id.fragmentContainer)

        val fragmentName = currentFragment?.javaClass?.simpleName

        assertEquals("HomeFragment", fragmentName)

    }


}