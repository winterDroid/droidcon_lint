package de.mprengemann.customlint.lintrules.issues;

import com.android.tools.lint.detector.api.Detector;

public class WrongTimberUsageTest extends LintCheckTest {

    @Override
    protected Detector getDetector() {
        return new WrongTimberUsageDetector();
    }

    public void testLogTest() throws Exception {
        assertEquals(
            "src/de/mprengemann/customlintrules/WrongTimberTestActivity.java:16: Warning: Using 'Log' instead of 'Timber' [LogNotTimber]\n" +
            "        Log.d(TAG, \"Test android logging\");\n" +
            "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
            "0 errors, 1 warnings\n",
            lintFiles("WrongTimberTestActivity.java=>" +
                      "src/de/mprengemann/customlintrules/WrongTimberTestActivity.java"));
    }
}
