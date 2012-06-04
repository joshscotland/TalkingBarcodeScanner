package mobileax.talkingbarcodescanner;

import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class TalkingBarcodeScannerActivity extends Activity implements TextToSpeech.OnInitListener, OnGestureListener  {

	private static final String TAG = "TalkingBarcodeScannerActivity";
	private TextToSpeech mTts;
	private TextView tv;
	private GestureDetector gestureScanner;
	Vibrator v; 

	private String intro = "Welcome, scan a barcode by tapping the screen";
	private String returned = "Tap to play results";
	private String results = "No results";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.main);
		tv = new TextView(this);
		tv.setText(intro);
		tv.setTextSize(40);
		tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		setContentView(tv);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mTts = new TextToSpeech(this, this);
		gestureScanner = new GestureDetector(this);
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown!
		if (mTts != null) {
			mTts.stop();
			mTts.shutdown();
		}

		super.onDestroy();
	}

	// Implements TextToSpeech.OnInitListener.
	public void onInit(int status) {
		// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
		if (status == TextToSpeech.SUCCESS) {
			// Set preferred language to US english.
			// Note that a language may not be available, and the result will indicate this.
			int result = mTts.setLanguage(Locale.US);
			// Try this someday for some interesting results.
			// int result mTts.setLanguage(Locale.FRANCE);
			if (result == TextToSpeech.LANG_MISSING_DATA ||
					result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Lanuage data is missing or the language is not supported.
				Log.e(TAG, "Language is not available.");
			} else {
				// Check the documentation for other possible result codes.
				// For example, the language may be available for the locale,
				// but not for the specified country and variant.

				// The TTS engine has been successfully initialized.
				// Allow the user to press the button for the app to speak again.
				// Greet the user.
				mTts.speak(intro,
						TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
						null);
			}
		} else {
			// Initialization failed.
			Log.e(TAG, "Could not initialize TextToSpeech.");
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gestureScanner.onTouchEvent(me);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		//tv.setText("-" + "DOWN" + "-");
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		//tv.setText("-" + "FLING" + "-");
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		v.vibrate(100);
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
		//tv.setText("-" + "LONG PRESS" + "-");
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		//tv.setText("-" + "SCROLL" + "-");
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		//tv.setText("-" + "SHOW PRESS" + "-");
	}    

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		//tv.setText("-" + "SINGLE TAP UP" + "-");
		v.vibrate(100);
		if (results == "No results") {
			IntentIntegrator integrator = new IntentIntegrator(this);
			integrator.initiateScan();
		} else {
			mTts.speak(results,
					TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
					null);
		}
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		v.vibrate(100);
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			results = scanResult.getContents();
		}
		tv.setText(results);
		mTts.speak(returned,
				TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
				null);
	}
}