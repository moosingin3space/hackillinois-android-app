package me.handshake.android

import android.content.Context
import com.getpebble.android.kit.PebbleKit
import com.getpebble.android.kit.PebbleKit.PebbleDataLogReceiver
import com.getpebble.android.kit.util.PebbleDictionary
import com.parse._
import java.util.UUID
import java.util.List
import org.scaloid.common._

class WatchControlActivity extends SActivity {
  val WATCHAPP_UUID = UUID.fromString("04444c59-e7a5-48ad-a145-4de0a5f833ce")
  val KEY_DATA = 5
  val KEY_TIMESTAMP = 6

  implicit val tag = LoggerTag("Handshake.me")

  // TODO Adapter

  // UI
  lazy val toggle = new SToggleButton() textOn("Enable") textOff("Disable")
  lazy val handshakeStatus = new STextView("No Handshakes")
  
  // State
  var isWatchAppRunning = false

  onCreate {
    contentView = new SVerticalLayout {
      STextView("Handshake.me").textSize(24.5 sp).<<.marginBottom(25 dip).>>
      this += toggle
      STextView("This is unfinished")
      this += handshakeStatus
    }

    toggle.onClick(toggleAppRunning)

    val dataHandler = new PebbleKit.PebbleDataReceiver(WATCHAPP_UUID) {
      def receiveData(context: Context, transactionId: Int, data: PebbleDictionary) = {
        info("received data from pebble" + data.size())

        val timestamp = data.getInteger(KEY_TIMESTAMP)
        handshakeStatus.text = "Last handshake at " + timestamp

        PebbleKit.sendAckToPebble(getApplicationContext(), transactionId)
      }
    }

    PebbleKit.registerReceivedDataHandler(this, dataHandler)
  }

  def toggleAppRunning() = {
    if (isWatchAppRunning) {
      PebbleKit.closeAppOnPebble(getApplicationContext(), WATCHAPP_UUID)
    } else {
      PebbleKit.startAppOnPebble(getApplicationContext(), WATCHAPP_UUID)
    }
    isWatchAppRunning = !isWatchAppRunning
  }

  /*def postHandshake(timestamp: Long) = {
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
  }*/
}
