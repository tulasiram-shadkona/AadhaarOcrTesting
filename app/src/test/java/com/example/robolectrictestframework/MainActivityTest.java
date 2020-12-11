package com.example.robolectrictestframework;

import android.content.Intent;
import android.os.Build;
import android.widget.Button;

import org.apache.tools.ant.Main;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class)
@Config(manifest=Config.NONE)
public class MainActivityTest {
    private MainActivity mainActivity;

    /*@Before
    public void setUp() throws Exception{
        mainActivity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .resume()
                .get();
    }
*/
  /*  @Test
    public void checkActivityNotNull() throws Exception {
        assertNotNull(mainActivity);
    }
*/
    /*@Test
    public void buttonClickShouldStartNewActivity() throws Exception
    {
        Button button = (Button) mainActivity.findViewById( R.id.startNextActivity );
        button.performClick();
        Intent intent = Shadows.shadowOf(mainActivity).peekNextStartedActivity();
        assertEquals(NextActivity.class.getCanonicalName(), intent.getComponent().getClassName());
    }*/

    @Test
    public void testButtonClickShouldShowToast() throws Exception {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().get();
//        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        Button view = (Button) activity.findViewById(R.id.showToast);
        assertNotNull(view);
        view.performClick();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo("Lala") );
    }
}
