package karega.scott.alright.tests.models;


import karega.scott.alright.models.AlrightApplication;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * This is a simple framework for a test of an Application.  See 
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on 
 * how to write and extend Application tests.
 * 
 * To run this test, you can type:
 * adb shell am instrument -w \
 *   -e class karega.scott.alright.models.test.AlrightApplicationTest \
 *   karega.scott.alright.test/android.test.InstrumentationTestRunner
 */
public class AlrightApplicationTest extends ApplicationTestCase<AlrightApplication> {

    public AlrightApplicationTest() {
        super(AlrightApplication.class);
      }

      @Override
      protected void setUp() throws Exception {
          super.setUp();
      }

      /**
       * The name 'test preconditions' is a convention to signal that if this
       * test doesn't pass, the test case was not set up properly and it might
       * explain any and all failures in other tests.  This is not guaranteed
       * to run before other tests, as junit uses reflection to find the tests.
       */
      @SmallTest
      public void testPreconditions() {
      }
      
      /**
       * Test basic startup/shutdown of Application
       */
      @MediumTest
      public void testSimpleCreate() {
          createApplication(); 
      }
      
}
