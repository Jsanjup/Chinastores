package com.app.chinastores;

import android.app.Activity;
import android.os.Bundle;

public class InfoActivity extends Activity{

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_info);
        setTitle(R.string.menu_help);
	}
}
