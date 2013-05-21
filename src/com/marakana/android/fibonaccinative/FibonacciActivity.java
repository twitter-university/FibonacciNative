package com.marakana.android.fibonaccinative;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class FibonacciActivity extends Activity implements OnClickListener,
		FibonacciFragment.OnResultListener {
	private static final String TAG = "FibonacciActivity";

	private EditText input;

	private RadioGroup type;

	private TextView output;

	private Button button;

	private FibonacciFragment fibonacciFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.input = (EditText) super.findViewById(R.id.input);
		this.type = (RadioGroup) super.findViewById(R.id.type);
		this.output = (TextView) super.findViewById(R.id.output);
		this.button = (Button) super.findViewById(R.id.button);
		this.button.setOnClickListener(this);
		// reconnect to our fragment (if it exists)
		this.fibonacciFragment = (FibonacciFragment) super.getFragmentManager()
				.findFragmentByTag("fibFrag");
		Log.d(TAG, "onCreate fibonacciFragment=" + this.fibonacciFragment);
	}

	// called from button
	public void onClick(View view) {
		String s = this.input.getText().toString();
		if (TextUtils.isEmpty(s)) {
			return;
		}
		int type = FibonacciActivity.this.type.getCheckedRadioButtonId();
		long n = Long.parseLong(s);
		Log.d(TAG, "onClick for type=" + type + " and n=" + n);
		this.button.setEnabled(false);
		// create our fragment and add
		this.fibonacciFragment = FibonacciFragment.newInstance(type, n);
		super.getFragmentManager().beginTransaction()
				.add(this.fibonacciFragment, "fibFrag").commit();
		Log.d(TAG, "Passed control to " + this.fibonacciFragment);
	}

	// called from fragment
	public void onResult(String result) {
		Log.d(TAG, "Posting result: " + result);
		this.output.setText(result);
		this.button.setEnabled(true);
		// git rid of our fragment
		super.getFragmentManager().beginTransaction()
				.remove(this.fibonacciFragment).commit();
		this.fibonacciFragment = null;
		Log.d(TAG, "Removed fragment");
	}
}
