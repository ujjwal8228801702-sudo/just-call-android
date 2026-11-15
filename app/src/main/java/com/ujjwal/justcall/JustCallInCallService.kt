package com.ujjwal.justcall

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService

class JustCallInCallService : InCallService() {
    override fun onCreate() {
        super.onCreate()
        CallAudioController.service = this
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        CallSession.current = call
        val intent = Intent(this, CallActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        if (CallSession.current == call) CallSession.current = null
    }

    override fun onDestroy() {
        CallAudioController.service = null
        super.onDestroy()
    }
}

object CallSession {
    var current: Call? = null
}

object CallAudioController {
    var service: JustCallInCallService? = null
    fun setMuted(value: Boolean) { service?.setMuted(value) }
    fun setSpeaker(value: Boolean) {
        service?.setAudioRoute(if (value) android.telecom.CallAudioState.ROUTE_SPEAKER else android.telecom.CallAudioState.ROUTE_EARPIECE)
    }
}
