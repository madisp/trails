package com.madisp.trails;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class Main extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Toast.makeText(this, "Hullo guvnor!", Toast.LENGTH_SHORT).show();
	}
}
