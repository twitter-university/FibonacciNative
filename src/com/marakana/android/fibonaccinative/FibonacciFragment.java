package com.marakana.android.fibonaccinative;

import java.util.Locale;

import android.app.Fragment;
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

	public static interface OnResultListener {
		public void onResult(String result);
	}

	private OnResultListener onResultListener;
	private String pendingResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		Bundle arguments = super.getArguments();
		final int type = arguments.getInt("type");
		final long n = arguments.getLong("n");
		final Locale locale = super.getActivity().getResources()
				.getConfiguration().locale;
		Log.d(TAG, "onCreate() with type=" + type + ", n=" + n
				+ ", and locale=" + locale);

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
				return ret;
			}

			@Override
			protected void onPostExecute(String result) {
				if (FibonacciFragment.this.onResultListener == null) {
					Log.d(TAG, "Saving pending result: " + result);
					FibonacciFragment.this.pendingResult = result;
				} else {
					Log.d(TAG, "Submitting result: " + result);
					FibonacciFragment.this.onResultListener.onResult(result);
				}
			}
		}.execute();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.onResultListener = (OnResultListener) super.getActivity();
		if (this.pendingResult == null) {
			Log.d(TAG, "No pending result. Saving listener for future result");
		} else {
			Log.d(TAG, "Submitting pending result: " + this.pendingResult);
			this.onResultListener.onResult(this.pendingResult);
			this.pendingResult = null;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		this.onResultListener = null;
		Log.d(TAG, "Detached from listener.");
	}

}