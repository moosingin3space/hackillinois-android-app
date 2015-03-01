package me.handshake.android

import com.parse._

class Application extends android.app.Application {
  override def onCreate() = {
    super.onCreate

    ParseCrashReporting.enable(this)
    Parse.initialize(this, "OUQCL4EOiQL7kdNjM0BVyrjRTkEqkAHYP7emrnCJ", "RaNDyjQBAM7Dd7kN4AJZHTVbyCKVoVMxmUJD10Cb")
    ParseInstallation.getCurrentInstallation().saveInBackground
  }
}
