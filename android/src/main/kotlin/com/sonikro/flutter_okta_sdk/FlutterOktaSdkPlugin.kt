package com.sonikro.flutter_okta_sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull
import com.okta.oidc.OIDCConfig
import com.okta.oidc.Okta.AuthBuilder
import com.okta.oidc.Okta.WebAuthBuilder
import com.okta.oidc.clients.AuthClient
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.storage.SharedPreferenceStorage
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar


/** FlutterOktaSdkPlugin */
class FlutterOktaSdkPlugin : FlutterPlugin, MethodCallHandler,
        PluginRegistry.ActivityResultListener, ActivityAware{
    private lateinit var channel: MethodChannel

    private var config: OIDCConfig? = null
    private var webClient: WebAuthClient? = null
    private var authClient: AuthClient? = null
    private var applicationContext: Context? = null
    private var pendingOperation: PendingOperation? = null
    private var mainActivity: Activity? = null


    companion object {
        fun registerWith(registrar: Registrar) {
            val plugin = FlutterOktaSdkPlugin()
            plugin.setActivity(registrar.activity())
            plugin.onAttachedToEngine(registrar.context(), registrar.messenger())
            registrar.addActivityResultListener(plugin)
        }
    }

    fun setActivity(activity: Activity) {
        this.mainActivity = activity
    }

    private fun onAttachedToEngine(context: Context, binaryMessenger: BinaryMessenger) {
        applicationContext = context
        channel = MethodChannel(binaryMessenger, "com.sonikro.flutter_okta_sdk")
        channel.setMethodCallHandler(this)
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        this.onAttachedToEngine(flutterPluginBinding.applicationContext, flutterPluginBinding.binaryMessenger)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        val arguments = call.arguments<Map<String, Any>>()
        checkAndSetPendingOperation(call.method, result)

        when (call.method) {
            AvailableMethods.SETUP.methodName -> {
                val oktaParams = processOktaRequestArguments(arguments)
                createConfig(oktaParams)
                finishWithSuccess(true)
            }
            AvailableMethods.SIGN_IN.methodName -> try {
                handleSignInMethodCall(this.webClient!!, this.mainActivity!!)
            } catch (ex: java.lang.Exception) {
                finishWithError(Errors.SIGNIN_ERROR_CODE, ex.localizedMessage)
            }
            AvailableMethods.SIGN_OUT.methodName -> try {
                handleSignOutMethodCall(this.webClient!!, this.mainActivity!!)
            } catch (ex: java.lang.Exception) {
                finishWithError(Errors.SIGNOUT_ERROR_CODE, ex.localizedMessage)
            }
            else -> finishWithError(Errors.NOT_IMPLEMENTED_ERROR_CODE, "Method not implemented")
        }
    }

    fun checkDependencies(){
        if (this.mainActivity == null) {
            finishWithError(Errors.ACTIVITY_NOT_DEFINED, "Activity not defined")
        }
        if (this.webClient == null) {
            finishWithError(Errors.WEB_CLIENT_NOT_DEFINED, "Web Client not defined")
        }
    }

    private fun finishWithError(error: Errors, errorMessage: String) {
        if (pendingOperation != null) {
            pendingOperation!!.result.error(error.errorCode, errorMessage, null)
            pendingOperation = null
        }
    }

    private fun handleSignInMethodCall(webAuthClient: WebAuthClient, activity: Activity) {
        checkDependencies()
        webAuthClient.signIn(activity, null)
        finishWithSuccess()
    }

    fun handleSignOutMethodCall(webAuthClient: WebAuthClient, activity: Activity) {
        checkDependencies()
        webAuthClient.signOutOfOkta(activity)
        finishWithSuccess()
    }


    private fun processOktaRequestArguments(arguments: Map<String, Any>): OktaRequestParameters {
        return OktaRequestParameters(
                clientId = (arguments["clientId"] as String?)!!,
                discoveryUri = (arguments["discoveryUrl"] as String?)!!,
                endSessionRedirectUri = (arguments["endSessionRedirectUri"] as String?)!!,
                redirectUri = (arguments["redirectUrl"] as String?)!!,
                requireHardwareBackedKeyStore = (arguments["requireHardwareBackedKeyStore"] as Boolean?)
                        ?: false,
                scopes = arguments["scopes"] as ArrayList<String>,
                userAgentTemplate = (arguments["userAgentTemplate"] as String?) ?: ""
        )
    }

    private fun checkAndSetPendingOperation(method: String, result: Result) {
        if (pendingOperation != null) {
            throw IllegalStateException(
                    "Concurrent operations detected: " + pendingOperation!!.method + ", " + method)
        }
        pendingOperation = PendingOperation(method, result)
    }

    private fun createConfig(params: OktaRequestParameters) {
        try {
            this.config = OIDCConfig.Builder()
                    .clientId(params.clientId)
                    .redirectUri(params.redirectUri)
                    .endSessionRedirectUri(params.endSessionRedirectUri)
                    .scopes(*params.scopes.toTypedArray())
                    .discoveryUri(params.discoveryUri)
                    .create()
            this.webClient = WebAuthBuilder()
                    .withConfig(this.config as OIDCConfig)
                    .withContext(this.applicationContext)
                    .withStorage(SharedPreferenceStorage(this.applicationContext))
                    .withOktaHttpClient(HttpClientImpl(params.userAgentTemplate))
                    .setRequireHardwareBackedKeyStore(params.requireHardwareBackedKeyStore)
                    .create()

            this.authClient = AuthBuilder()
                    .withConfig(this.config as OIDCConfig)
                    .withContext(this.applicationContext)
                    .withStorage(SharedPreferenceStorage(this.applicationContext))
                    .withOktaHttpClient(HttpClientImpl(params.userAgentTemplate))
                    .setRequireHardwareBackedKeyStore(params.requireHardwareBackedKeyStore)
                    .create()
        } catch (ex: Exception) {
            finishWithError(Errors.CREATECONFIG_ERROR_CODE, ex.localizedMessage)
        }
    }

    private fun finishWithSuccess(data: Any? = null) {
        if (pendingOperation != null) {
            pendingOperation!!.result.success(data)
            pendingOperation = null
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        webClient!!.handleActivityResult(requestCode, resultCode, data)
        return pendingOperation != null
    }

    override fun onDetachedFromActivity() {
        this.mainActivity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        binding.addActivityResultListener(this)
        mainActivity = binding.activity
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        binding.addActivityResultListener(this)
        mainActivity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        this.mainActivity = null
    }
}
