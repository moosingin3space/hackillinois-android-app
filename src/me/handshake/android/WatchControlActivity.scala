package me.handshake.android

import android.content._
import android.widget._
import android.view._
import com.getpebble.android.kit.PebbleKit
import com.getpebble.android.kit.PebbleKit.PebbleDataLogReceiver
import com.getpebble.android.kit.util.PebbleDictionary
import com.parse._
import java.util.UUID
import org.scaloid.common._
import scala.collection.mutable._

class HandshakeListAdapter extends BaseAdapter {
  val handshakes = new MutableList[ParseUser]

  def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    implicit val ctx = parent.getContext()
    val tv = new STextView(handshakes(position).getUsername())
    tv.onClick(() => {
      val intent = SIntent[DisplayUserActivity].putExtra("handshake", handshakes(position).getUsername())
      intent.start[DisplayUserActivity]
    })
    return tv
  }

  def getCount(): Int = handshakes.size
  def getItem(x: Int): Object = handshakes(x)
  def getItemId(x: Int): Long = 0
}

class WatchControlActivity extends SActivity {
  val WATCHAPP_UUID = UUID.fromString("04444c59-e7a5-48ad-a145-4de0a5f833ce")
  val KEY_DATA = 5
  val KEY_TIMESTAMP = 6

  implicit val tag = LoggerTag("Handshake.me")

  // Adapter
  val adapter = new HandshakeListAdapter()

  // UI
  lazy val toggle = new SToggleButton() textOn("Enable") textOff("Disable")
  lazy val handshakeStatus = new STextView("No Handshakes")
  lazy val handshakesList = new SListView() adapter(adapter)
  
  // State
  var isWatchAppRunning = false

  onCreate {
    contentView = new SVerticalLayout {
      STextView("Handshake.me").textSize(24.5 sp).<<.marginBottom(25 dip).>>
      this += toggle
      this += handshakeStatus
      this += handshakesList
    }

    toggle.onClick(toggleAppRunning)

    val dataHandler = new PebbleKit.PebbleDataReceiver(WATCHAPP_UUID) {
      def receiveData(context: Context, transactionId: Int, data: PebbleDictionary) = {
        info("received data from pebble" + data.size())

        val timestamp = data.getInteger(KEY_TIMESTAMP)
        handshakeStatus.text = "Last handshake at " + timestamp
        postHandshake(timestamp)
        findOtherHandshake(timestamp, (u: ParseUser) => adapter.handshakes += u)

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

  def postHandshake(timestamp: Long) = {
    val handshake = new ParseObject("Handshake")
    handshake.put("endTime", timestamp)
    handshake.put("userId", ParseUser.getCurrentUser().getObjectId())
    handshake.saveInBackground()
  }

  def findOtherHandshake(timestamp: Long, callback: ParseUser => Unit) = {
    val handshakeQuery: ParseQuery[ParseObject] = ParseQuery.getQuery("Handshake")
    val loRange = timestamp - 2500
    val hiRange = timestamp + 2500
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
}
