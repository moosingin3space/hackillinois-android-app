package me.handshake.android

import android.content.Context

import com.getpebble.android.kit.PebbleKit
import com.getpebble.android.kit.PebbleKit.PebbleDataLogReceiver
import com.getpebble.android.kit.util.PebbleDictionary
import java.io._
import java.util.UUID

import com.parse._
import org.scaloid.common._

class MainActivity extends SActivity {
  val WATCHAPP_UUID = UUID.fromString("Insert UUID here")
  implicit val tag = LoggerTag("Handshake.me")

  // UI
  lazy val linkedInField = new SEditText()
  lazy val githubField = new SEditText()
  lazy val saveButton = new SButton("Save")

  onCreate {
    ParseCrashReporting.enable(this)
    Parse.initialize(this, "OUQCL4EOiQL7kdNjM0BVyrjRTkEqkAHYP7emrnCJ", "RaNDyjQBAM7Dd7kN4AJZHTVbyCKVoVMxmUJD10Cb")
    ParseUser.enableAutomaticUser()

    contentView = new SVerticalLayout {
      STextView("Handshake.me").textSize(24.5 sp).<<.marginBottom(25 dip).>>
      this += linkedInField
      this += githubField
      this += saveButton
    }

    saveButton.onClick(saveValues())

    val dataHandler = new PebbleKit.PebbleDataReceiver(WATCHAPP_UUID) {
      def receiveData(context: Context, transactionId: Int, data: PebbleDictionary) = {
        info("received data from pebble")

        // TODO do stuff here

        PebbleKit.sendAckToPebble(getApplicationContext(), transactionId)
      }
    }

    PebbleKit.registerReceivedDataHandler(this, dataHandler)
  }

  def postHandshake(timestamp: Long) = {
    val handshake = new ParseObject("Handshake")
    handshake.put("endTime", timestamp)
    handshake.put("userId", ParseUser.getCurrentUser().getObjectId())
    handshake.saveInBackground()
  }

  def findOtherHandshake(timestamp: Long, callback: ParseUser => Unit) = {
    val handshakeQuery: ParseQuery[ParseObject] = ParseQuery.getQuery("Handshake")
    val loRange = timestamp - 5000
    val hiRange = timestamp + 5000
    handshakeQuery.whereGreaterThan("endTime", loRange)
    handshakeQuery.whereLessThan("endTime", hiRange)
    handshakeQuery.getFirstInBackground(new GetCallback[ParseObject] {
      def done(obj: ParseObject, e: ParseException) = {
        if (obj == null) {
          debug("no handshake")
        }
        else {
          debug("got handshake")
          val userQuery = ParseUser.getQuery()
          userQuery.whereEqualTo("objectId", obj.get("userId"))
          userQuery.getFirstInBackground(new GetCallback[ParseUser] {
            def done(user: ParseUser, e: ParseException) = {
              if (user == null) {
                debug("no handshake")
              }
              else {
                callback(user)
              }
            }
          })
        }
      }
    })
  }

  def saveValues() = {
    // save them in the db
    val currentUser = ParseUser.getCurrentUser()
    currentUser.put("github", githubField.text.toString)
    currentUser.put("linkedin", linkedInField.text.toString)
    currentUser.saveInBackground()
  }
}
