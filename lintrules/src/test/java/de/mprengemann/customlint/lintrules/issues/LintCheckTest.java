package de.mprengemann.customlint.lintrules.issues;

import com.android.testutils.AbstractCheckTest;
import com.android.tools.lint.client.api.IssueRegistry;
import de.mprengemann.customlint.lintrules.CustomIssueRegistry;

public abstract class LintCheckTest extends AbstractCheckTest {

    @Override
    protected IssueRegistry getIssueRegistry() {
        return new CustomIssueRegistry();
    }

    @Override
    protected String getTestDataRelPath() {
        // Needs to be relative to lintrules/src/test/resources
        return "data/src";
    }
}
