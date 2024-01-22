package com.task.imageuploadsoft.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.task.imageuploadsoft.R

class AppPermissionsRunTime {
    private var permissionsNeeded: MutableList<String>? = null
    private var permissionsList: MutableList<String>? = null
    private val dialog_parent: AlertDialog? = null
    val REQUEST_CODE_PERMISSIONS = Constants.REQUEST_CODE
    fun getPermission(permission_list: ArrayList<Permission>?, activity: Activity): Boolean {
        /*
         * Creating the List if not created .
         * if created then clear the list for refresh use.*/
        if (permissionsNeeded == null || permissionsList == null) {
            permissionsNeeded = ArrayList()
            permissionsList = ArrayList()
        } else {
            permissionsNeeded!!.clear()
            permissionsList!!.clear()
        }
        if (dialog_parent != null && dialog_parent.isShowing) {
            dialog_parent.dismiss()
            dialog_parent.cancel()
        }
        var count = 0
        while (permission_list != null && count < permission_list.size) {
            when (permission_list[count]) {
                Permission.CAMERA -> if (!addPermission(
                        permissionsList!!, Manifest.permission.CAMERA, activity
                    )
                ) {
                    permissionsNeeded!!.add(Constants.CAMERA)
                }

                Permission.READ_EXTERNAL_STORAGE -> if (!addPermission(
                        permissionsList!!, Manifest.permission.READ_EXTERNAL_STORAGE, activity
                    )
                ) {
                    permissionsNeeded!!.add(Constants.WRITE_EXTERNAL_STORAGE)
                }

                Permission.WRITE_EXTERNAL_STORAGE -> if (!addPermission(
                        permissionsList!!, Manifest.permission.WRITE_EXTERNAL_STORAGE, activity
                    )
                ) {
                    permissionsNeeded!!.add(Constants.READ_EXTERNAL_STORAGE)
                }

                Permission.PHONE -> if (!addPermission(
                        permissionsList!!, Manifest.permission.READ_PHONE_STATE, activity
                    )
                ) {
                    permissionsNeeded!!.add(Constants.READ_PHONE_STATE)
                }

                Permission.READ_MEDIA_IMAGE -> {
                    if (!addPermission(
                            permissionsList!!,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            activity
                        )
                    ) {
                        permissionsNeeded!!.add(Constants.READ_MEDIA_IMAGES)
                    }
                    if (!addPermission(
                            permissionsList!!,
                            Manifest.permission.READ_MEDIA_AUDIO,
                            activity
                        )
                    ) {
                        permissionsNeeded!!.add(Constants.READ_MEDIA_AUDIOS)
                    }
                }

                Permission.READ_MEDIA_AUDIO -> if (!addPermission(
                        permissionsList!!, Manifest.permission.READ_MEDIA_AUDIO, activity
                    )
                ) {
                    permissionsNeeded!!.add(Constants.READ_MEDIA_AUDIOS)
                }

                Permission.READ_MEDIA_VIDEO -> if (!addPermission(
                        permissionsList!!, Manifest.permission.READ_MEDIA_VIDEO, activity
                    )
                ) {
                    permissionsNeeded!!.add(Constants.READ_MEDIA_VIDEOS)
                }

                else -> {}
            }
            count++
        }
        return if (permissionsList!!.size > 0 && permissionsNeeded!!.size > 0) {
            val message = StringBuilder(
                activity.getString(R.string.permission_grant_access)
                        + permissionsNeeded!![0]
            )
            for (i in 1 until permissionsNeeded!!.size) {
                message.append(", ").append(permissionsNeeded!![i])
            }
            check_for_Permission(permissionsList!!.toTypedArray<String>(), activity)
            false
        } else {
            true
        }
    }

    fun check_for_Permission(permissions: Array<String>?, mActivity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mActivity.requestPermissions(permissions!!, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun addPermission(
        permissionsList: MutableList<String>,
        permission: String,
        activity: Activity
    ): Boolean {
        return if (ContextCompat.checkSelfPermission(
                activity,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(permission)
            false
        } else {
            true
        }
    }

    enum class Permission {
        CAMERA,
        READ_EXTERNAL_STORAGE,
        WRITE_EXTERNAL_STORAGE,
        PHONE,
        READ_MEDIA_IMAGE,
        READ_MEDIA_VIDEO,
        READ_MEDIA_AUDIO
    }

    companion object {
        val instance = AppPermissionsRunTime()
    }
}
