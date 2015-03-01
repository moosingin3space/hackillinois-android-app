package me.handshake.android

import com.parse._
import org.scaloid.common._

class SignInActivity extends SActivity {
  implicit val tag = LoggerTag("Handshake.me")

  // UI components
  lazy val emailField = new SEditText() {
    inputType(TEXT_EMAIL_ADDRESS) 
    hint("Email")
  }
  lazy val passwordField = new SEditText() {
    inputType(TEXT_PASSWORD) 
    hint("Password")
  }

  onCreate {
    contentView = new SVerticalLayout {
      STextView("Sign in").textSize(24.5 sp).<<.marginBottom(25 dip).>>
      this += emailField
      this += passwordField
      SButton("Sign in", login())
    }
  }

  def login() = {
    val email = emailField.text.toString
    val password = passwordField.text.toString
    ParseUser.logInInBackground(email, password, new LogInCallback {
      def done(user: ParseUser, e: ParseException) = {
        if (user != null) {
          debug("logged in")
          startActivity[WatchControlActivity]
        } else {
          debug("log-in failed")
          // TODO display log-in failure
        }
      }
    })
  }
}
