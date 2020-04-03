package com.example.arcore.util.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.BasePermissionListener

private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
private const val PACKAGE = "package"

object CameraPermissionHelper {

    fun requestPermissionIfNeeded(activity: Activity, cancelable: Boolean, onGranted: () -> Unit) {
        Dexter.withActivity(activity)
            .withPermission(CAMERA_PERMISSION)
            .withListener(object : BasePermissionListener() {

                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    onGranted.invoke()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    if (response?.isPermanentlyDenied == true) {
                        showPermissionPermanentlyDeniedDialog(activity, cancelable)
                        return
                    }
                    showPermissionDeniedDialog(activity, cancelable, onGranted)
                }
            })
            .onSameThread()
            .check()
    }

    private fun showPermissionDeniedDialog(
        activity: Activity,
        cancelable: Boolean,
        onGranted: () -> Unit
    ) {
        AlertDialog.Builder(activity)
            .setTitle("Permission denied")
            .setCancelable(cancelable)
            .setMessage("App need camera permission to continue!")
            .setPositiveButton("Ok") { _, _ ->
                requestPermissionIfNeeded(activity, cancelable, onGranted)
            }
            .show()
    }

    private fun showPermissionPermanentlyDeniedDialog(activity: Activity, cancelable: Boolean) {
        AlertDialog.Builder(activity)
            .setCancelable(cancelable)
            .setTitle("Permission permanently denied")
            .setMessage("App need camera permission to continue, please go to settings and grant permission!")
            .setPositiveButton("Settings") { _, _ ->
                openAppSystemSettings(activity)
            }
            .show()
    }

    private fun openAppSystemSettings(context: Context) {
        context.startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts(PACKAGE, context.packageName, null)
        })
    }
}