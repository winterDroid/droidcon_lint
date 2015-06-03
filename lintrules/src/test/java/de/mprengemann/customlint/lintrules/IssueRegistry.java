package de.mprengemann.customlint.lintrules;

import com.android.tools.lint.detector.api.Issue;
import de.mprengemann.customlint.lintrules.issues.WrongTimberUsageDetector;
import junit.framework.TestCase;

import java.util.List;

public class IssueRegistry extends TestCase {

    private CustomIssueRegistry registry;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        registry = new CustomIssueRegistry();
    }

    public void testRegistry() throws Exception {
        List<Issue> issues = registry.getIssues();
        assertEquals(issues.size(), 1);
        assertEquals(issues.get(0), WrongTimberUsageDetector.ISSUE);
        assertEquals(registry.getIssue("LogNotTimber"), WrongTimberUsageDetector.ISSUE);
    }

    @Override
    public void tearDown() throws Exception {
        registry = null;
        super.tearDown();
    }
}
