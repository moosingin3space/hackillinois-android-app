package me.handshake.android

import com.parse._
import org.scaloid.common._

class MainActivity extends SActivity {
  implicit val tag = LoggerTag("Handshake.me")

  onCreate {
    val user = ParseUser.getCurrentUser()
    if (user != null) {
      // go straight to WatchControl
      startActivity[WatchControlActivity]
    }
    else {
      contentView = new SVerticalLayout {
        STextView("Handshake.me").textSize(24.5 sp).<<.marginBottom(25 dip).>>
        SButton("Sign in", startActivity[SignInActivity])
        SButton("Sign up", startActivity[SignUpActivity])
      }
    }
  }
}
