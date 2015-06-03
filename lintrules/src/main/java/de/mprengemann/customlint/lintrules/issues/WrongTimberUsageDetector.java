package de.mprengemann.customlint.lintrules.issues;

import com.android.annotations.NonNull;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import lombok.ast.AstVisitor;
import lombok.ast.MethodInvocation;
import lombok.ast.VariableReference;

import java.util.Arrays;
import java.util.List;

public class WrongTimberUsageDetector extends Detector implements Detector.JavaScanner {

    public static final Issue ISSUE = Issue.create("LogNotTimber",
                                                   "Called to Log instead of Timber",
                                                   "Since Timber is included in the project, it is likely " +
                                                   "that calls to Log should instead be going to Timber.",
                                                   Category.MESSAGES,
                                                   5,
                                                   Severity.WARNING,
                                                   new Implementation(WrongTimberUsageDetector.class,
                                                                      Scope.JAVA_FILE_SCOPE));

    @Override
    public List<String> getApplicableMethodNames() {
        return Arrays.asList("v", "d", "i", "w", "e", "wtf");
    }

    @Override
    public void visitMethod(@NonNull JavaContext context, AstVisitor visitor, @NonNull MethodInvocation node) {
        if (!(node.astOperand() instanceof VariableReference)) {
            return;
        }
        VariableReference ref = (VariableReference) node.astOperand();
        if ("Log".equals(ref.astIdentifier().astValue())) {
            context.report(ISSUE,
                           node,
                           context.getLocation(node),
                           "Using 'Log' instead of 'Timber'");
        }
    }
}
