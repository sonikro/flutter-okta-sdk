package com.sonikro.flutter_okta_sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull
import com.okta.oidc.OIDCConfig
import com.okta.oidc.clients.AuthClient
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.net.response.IntrospectInfo
import com.sonikro.flutter_okta_sdk.okta.entities.AvailableMethods
import com.sonikro.flutter_okta_sdk.okta.entities.Errors
import com.sonikro.flutter_okta_sdk.okta.entities.PendingOperation
import com.sonikro.flutter_okta_sdk.okta.entities.enumContains
import com.sonikro.flutter_okta_sdk.okta.operations.*
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

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        this.onAttachedToEngine(flutterPluginBinding.applicationContext, flutterPluginBinding.binaryMessenger)
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

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        val arguments = call.arguments<Map<String, Any>>()
        checkAndSetPendingOperation(call.method, result)

        if(!enumContains<Errors>(call.method) ){
            finishWithError(Errors.METHOD_NOT_IMPLEMENTED)
        }

        if (call.method == AvailableMethods.CREATE_CONFIG){
            try{
                if (applicationContext == null )
                    finishWithError(Errors.NO_CONTEXT)

                createConfig(arguments, applicationContext!!)
                finishWithSuccess(true)
            }catch (ex:  java.lang.Exception){
                finishWithError(Errors.OKTA_OIDC_ERROR)
            }
        }else{
            checkDependencies()
            try{
                when (call.method){
                    AvailableMethods.SIGN_IN.methodName ->{
                        signIn(this.webClient!!, this.mainActivity!!)
                        finishWithSuccess()
                    }
                    AvailableMethods.SIGN_OUT.methodName ->{
                        signOut(this.webClient!!, this.mainActivity!!)
                        finishWithSuccess()
                    }
                    AvailableMethods.GET_USER.methodName ->{
                        val user = getUser(this.webClient!!)
                        finishWithSuccess(user)
                    }
                    AvailableMethods.IS_AUTHENTICATED.methodName ->{
                        val isAuthenticated = isAuthenticated(this.webClient!!)
                        finishWithSuccess(isAuthenticated)
                    }
                    AvailableMethods.GET_ACCESS_TOKEN.methodName ->{
                        val accessToken = getAccessToken(this.webClient!!)
                        if(accessToken ==null)finishWithError(Errors.NO_ACCESS_TOKEN)
                        finishWithSuccess(accessToken)
                    }
                    AvailableMethods.GET_ID_TOKEN.methodName ->{
                        val idToken = getIdToken(this.webClient!!)
                        if(idToken ==null)finishWithError(Errors.NO_ID_TOKEN)
                        finishWithSuccess(idToken)
                    }
                    AvailableMethods.REVOKE_ACCESS_TOKEN.methodName ->{
                        revokeAccessToken(this.webClient!!)
                        finishWithSuccess()
                    }
                    AvailableMethods.REVOKE_ID_TOKEN.methodName ->{
                        revokeIdToken(this.webClient!!)
                        finishWithSuccess()
                    }
                    AvailableMethods.REVOKE_REFRESH_TOKEN.methodName ->{
                        revokeRefreshToken(this.webClient!!)
                        finishWithSuccess()
                    }
                    AvailableMethods.CLEAR_TOKENS.methodName ->{
                        clearTokens(this.webClient!!,this.authClient!!)
                        finishWithSuccess()
                    }
                    AvailableMethods.INTROSPECT_ACCESS_TOKEN.methodName ->{
                        val result: IntrospectInfo = introspectAccessToken(this.webClient!!)
                        finishWithSuccess(result)
                    }
                    AvailableMethods.INTROSPECT_ID_TOKEN.methodName ->{
                        val result: IntrospectInfo = introspectIdToken(this.webClient!!)
                        finishWithSuccess(result)
                    }
                    AvailableMethods.INTROSPECT_REFRESH_TOKEN.methodName ->{
                        val result: IntrospectInfo = introspectRefreshToken(this.webClient!!)
                        finishWithSuccess(result)
                    }
                    AvailableMethods.REFRESH_TOKENS.methodName ->{
                        refreshTokens(this.webClient!!)
                        finishWithSuccess()
                    }
                }
            }catch (ex: java.lang.Exception) {
                finishWithGenericError(ex.localizedMessage)
            }
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

    fun checkDependencies(){
        if (this.mainActivity == null) {
            finishWithError(Errors.NO_VIEW)
        }
        if (this.webClient == null) {
            finishWithError(Errors.NOT_CONFIGURED)
        }
    }

    private fun checkAndSetPendingOperation(method: String, result: Result) {
        if (pendingOperation != null) {
            throw IllegalStateException(
                    "Concurrent operations detected: " + pendingOperation!!.method + ", " + method)
        }
        pendingOperation = PendingOperation(method, result)
    }

    private fun finishWithSuccess(data: Any? = null) {
        if (pendingOperation != null) {
            pendingOperation!!.result.success(data)
            pendingOperation = null
        }
    }

    private fun finishWithError(error: Errors) {
        if (pendingOperation != null) {
            pendingOperation!!.result.error(error.errorCode, error.errorMessage, null)
            pendingOperation = null
        }
    }

    private fun finishWithGenericError( message:String) {
        if (pendingOperation != null) {
            pendingOperation!!.result.error(Errors.GENERIC_ERROR, message, null)
            pendingOperation = null
        }
    }
}
