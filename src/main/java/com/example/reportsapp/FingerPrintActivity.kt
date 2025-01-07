package com.example.reportsapp

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.reportsapp.databinding.ActivityFingerPrintBinding

class FingerPrintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFingerPrintBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFingerPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val animBlink = AnimationUtils.loadAnimation(applicationContext, R.anim.blink_text_view)
        binding.loginWithFingerPrintTxt.startAnimation(animBlink)

        val animBlinkImg = AnimationUtils.loadAnimation(applicationContext, R.anim.blink_text_view)
        binding.fingerPrintImg.startAnimation(animBlinkImg)

        val label = getString(R.string.use_your_fingerprint_to_login_your_account)
        val stringBuilder = StringBuilder()
        Thread {
            for (letter in label) {
                stringBuilder.append(letter)
                Thread.sleep(300)
                runOnUiThread {
                    binding.loginUseTxt.text = stringBuilder.toString()
                }
            }
        }.start()

        val executor = ContextCompat.getMainExecutor(this@FingerPrintActivity)
        biometricPrompt = BiometricPrompt(this@FingerPrintActivity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                startActivity(Intent(this@FingerPrintActivity, LoginActivity::class.java))

            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@FingerPrintActivity, getString(R.string.fingerprint_verification_failed), Toast.LENGTH_SHORT).show()
            }
        })
                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.login))
                    .setSubtitle(getString(R.string.use_your_fingerprint_to_login_your_account))
                    .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                    .build()
                val biometricManager = BiometricManager.from(this@FingerPrintActivity)
                when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        biometricPrompt.authenticate(promptInfo)
                    }

                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                        Toast.makeText(
                            this@FingerPrintActivity,
                            getString(R.string.the_device_does_not_support_fingerprint),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                        Toast.makeText(
                            this@FingerPrintActivity,
                            getString(R.string.fingerprint_is_currently_unavailable),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        Toast.makeText(
                            this@FingerPrintActivity,
                            getString(R.string.please_register_fingerprint_in_device_settings),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
