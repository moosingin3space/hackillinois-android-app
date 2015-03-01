package me.handshake.android

import com.parse._
import org.scaloid.common._

class SignUpActivity extends SActivity {
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
  lazy val githubField = new SEditText() hint("GitHub Handle")
  lazy val linkedinField = new SEditText() hint("LinkedIn Name")

  onCreate {
    contentView = new SVerticalLayout {
      STextView("Sign up").textSize(24.5 sp).<<.marginBottom(25 dip).>>
      this += emailField
      this += passwordField
      this += githubField
      this += linkedinField
      SButton("Sign up", signup())
    }
  }

  def signup() = {
    val email = emailField.text.toString
    val password = passwordField.text.toString
    val github = githubField.text.toString
    val linkedin = linkedinField.text.toString
    val user = new ParseUser()
    user.setUsername(email)
    user.setEmail(email)
    user.setPassword(password)
    user.put("github", github)
    user.put("linkedin", linkedin)

    user.signUpInBackground(new SignUpCallback {
      def done(e: ParseException) {
        if (e == null) {
          debug("sign-up successful")
          startActivity[WatchControlActivity]
        } else {
          debug("sign-up failed")
          // TODO display failure
        }
      }
    })
  }
}
