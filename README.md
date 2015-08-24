This is a template project which shows how to include custom Lint checks into your Gradle based Android project.
You can execute your Lint checks with:

```
./gradlew lint
```

Besides that it shows how to write tests for the custom checks.
You can execute our custom test with:

```
./gradlew :lintrules:cleanTest :lintrules:test --tests de.mprengemann.customlint.lintrules.issues.WrongTimberUsageTest
```

You can find the information about the talk here:
http://droidcon.de/session/power-custom-lint-checks

The slides are published here:
https://speakerdeck.com/winterdroid/the-power-of-custom-lint-checks

## Udpate

Since the release of ```com.android.tools.lint:lint-tests:24.3.1``` it is recommended to use ```LintDetectorTest```
instead of ```LintCheckTest```. You can find an example implementation here: https://github.com/winterDroid/droidcon_nyc