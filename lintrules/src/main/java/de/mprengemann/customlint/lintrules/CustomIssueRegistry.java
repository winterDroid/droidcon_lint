package de.mprengemann.customlint.lintrules;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;
import de.mprengemann.customlint.lintrules.issues.WrongTimberUsageDetector;

import java.util.Arrays;
import java.util.List;

public class CustomIssueRegistry extends IssueRegistry {
    @Override
    public List<Issue> getIssues() {
        return Arrays.asList(WrongTimberUsageDetector.ISSUE);
    }
}
