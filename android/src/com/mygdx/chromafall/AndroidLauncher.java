package com.mygdx.chromafall;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.chromafall.MyGdxGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = true;
		config.useGyroscope = false;
		config.useCompass = false;
		config.hideStatusBar = true;
		config.useImmersiveMode = true;
		initialize(new MyGdxGame(), config);
	}
}
