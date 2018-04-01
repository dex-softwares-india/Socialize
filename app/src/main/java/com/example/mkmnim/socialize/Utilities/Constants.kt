package com.example.mkmnim.socialize.Utilities

import com.example.mkmnim.socialize.Databases.DatabaseHandler

var HOTSPOT_STATE_CHANGE = "HOTSPOT_STATE_CHANGE"
var WIFI_STATE_CHANGE = "WIFI_STATE_CHANGE"
var DISCOVER_CLIENTS = true
var HOTSPOT_ON = false
var WIFI_ON= false
var UNIQUE_CLIENTS_SET= mapOf<String,String>()
var NO_OF_CLIENTS=0
var CONNECTED_USERS_FRAGMENT_INITIALIZED_ONCE=false
var DATABASE_HANDLER:DatabaseHandler?=null