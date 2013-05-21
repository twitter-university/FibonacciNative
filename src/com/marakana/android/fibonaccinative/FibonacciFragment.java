package com.marakana.android.fibonaccinative;

import java.util.Locale;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

public class FibonacciFragment extends Fragment {
	private static final String TAG = "FibonacciFragment";

	public static FibonacciFragment newInstance(int algorithmType, long n) {
		Log.d(TAG, "Creating a new instance for type=" + algorithmType
				+ " and n=" + n);
		FibonacciFragment fibonacciFragment = new FibonacciFragment();
		Bundle args = new Bundle();
		args.putInt("type", algorithmType);
		args.putLong("n", n);
		fibonacciFragment.setArguments(args);
		return fibonacciFragment;
	}

	// to be implemented by our activity
	public static interface OnResultListener {
		public void onResult(String result);
	}

	private Dialog dialog;
	private OnResultListener onResultListener;
	private String pendingResult;

	// invoked when the fragment is first created (not on configuration change)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// indicate that we want to survive configuration changes
		setRetainInstance(true);
		// parse our arguments
		Bundle arguments = super.getArguments();
		final int type = arguments.getInt("type");
		final long n = arguments.getLong("n");
		final Locale locale = super.getActivity().getResources()
				.getConfiguration().locale;
		Log.d(TAG, "onCreate() with type=" + type + ", n=" + n
				+ ", and locale=" + locale);

		// run the expensive operation asynchronosly
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				Log.d(TAG, "starting doInBackground() with " + type + ", n="
						+ n + ", and locale=" + locale);
				long result = 0;
				long t = SystemClock.uptimeMillis();
				switch (type) {
				case R.id.type_fib_jr:
					result = FibLib.fibJR(n);
					break;
				case R.id.type_fib_ji:
					result = FibLib.fibJI(n);
					break;
				case R.id.type_fib_nr:
					result = FibLib.fibNR(n);
					break;
				case R.id.type_fib_ni:
					result = FibLib.fibNI(n);
					break;
				}
				t = SystemClock.uptimeMillis() - t;
				String ret = String.format(locale, "fib(%d)=%d in %d ms", n,
						result, t);
				Log.d(TAG, "finished doInBackground() with " + type + ", n="
						+ n + ", locale=" + locale + " and result=" + result);
				// send the result (ret) to the UI thread
				return ret;
			}

			// handle the result on the UI thread
			@Override
			protected void onPostExecute(String result) {
				// if there is no listener (i.e. activity)
				if (FibonacciFragment.this.onResultListener == null) {
					Log.d(TAG, "Saving pending result: " + result);
					// save for the activity when it comes back (if possible?)
					FibonacciFragment.this.pendingResult = result;
				} else {
					// we are done, send the result
					Log.d(TAG, "Submitting result: " + result);
					FibonacciFragment.this.onResultListener.onResult(result);
				}
			}
		}.execute();
	}

	// invoked every time we are given the activity (even on config changes)
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// get our activity (as a listener)
		this.onResultListener = (OnResultListener) super.getActivity();
		if (this.pendingResult == null) {
			Log.d(TAG, "No pending result. Saving listener for future result");
		} else {
			// send the result if we have one
			Log.d(TAG, "Submitting pending result: " + this.pendingResult);
			this.onResultListener.onResult(this.pendingResult);
			this.pendingResult = null;
		}
		// pop up a progress dialog
		Log.d(TAG, "Showing dialog");
		this.dialog = ProgressDialog.show(super.getActivity(), "", super
				.getActivity().getText(R.string.progress_text), true);
	}

	// activity going away (possibly due to a configuration change)
	@Override
	public void onDetach() {
		super.onDetach();
		// dismiss our dialog and forget our listener (as it's going away)
		Log.d(TAG, "Detached. Dismissing the listener and the dialog.");
		this.dialog.dismiss();
		this.dialog = null;
		this.onResultListener = null;
	}
}