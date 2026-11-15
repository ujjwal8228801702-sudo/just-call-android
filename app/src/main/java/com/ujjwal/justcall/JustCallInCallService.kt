package com.ujjwal.justcall

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService

class JustCallInCallService : InCallService() {
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
}

object CallSession {
    var current: Call? = null
}
