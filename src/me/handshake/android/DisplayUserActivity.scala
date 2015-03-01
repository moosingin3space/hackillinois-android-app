package me.handshake.android

import android.content._
import android.widget._
import com.getpebble.android.kit.PebbleKit
import com.getpebble.android.kit.PebbleKit.PebbleDataLogReceiver
import com.getpebble.android.kit.util.PebbleDictionary
import com.parse._
import java.util.UUID
import org.scaloid.common._

class DisplayUserActivity extends SActivity {
  // UI
  lazy val githubField = new STextView("github")
  lazy val linkedinField = new STextView("linkedin")

  onCreate {
    val intent = getIntent()
    val username = intent.getStringExtra("handshake")
    contentView = new SVerticalLayout {
      STextView(username).textSize(24.5 sp).<<.marginBottom(25 dip).>>
      this += githubField
      this += linkedinField
    }

    val query = ParseUser.getQuery()
    query.whereEqualTo("username", username)
    query.getFirstInBackground(new GetCallback[ParseUser] {
      def done(user: ParseUser, e: ParseException) = {
        if (e == null) {
          runOnUiThread(() => {
            githubField.text = user.get("github").toString
            linkedinField.text = user.get("linkedin").toString
          })
        }
      }
    })
  }
}
