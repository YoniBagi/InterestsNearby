package com.interestsnearby.managers.permissions

interface IPermissionManagerContract {
    interface IPermissionManager {
        fun hasPermission(permission: String): Boolean
        fun requestPermission(permission: String)
    }
}