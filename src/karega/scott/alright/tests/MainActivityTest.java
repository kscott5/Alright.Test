package karega.scott.alright.tests;

import karega.scott.alright.MainActivity;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Make sure that the main launcher activity opens up properly, which will be
 * verified by {@link #testActivityTestCaseSetUpProperly}.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    /**
     * Create an {@link ActivityInstrumentationTestCase2} that tests the {@link MainActivity} activity.
     */
    public MainActivityTest() {
        super(MainActivity.class);
    }

    /**
     * Verifies that activity under test can be launched.
     */
    public void testActivityTestCaseSetUpProperly() {
    	MainActivity activity = this.getActivity();
    	
        assertNotNull("activity should be launched successfully", activity);
    }
}
