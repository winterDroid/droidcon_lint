package com.android.testutils;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.eclipse.org/org/documents/epl-v10.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.android.SdkConstants;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Common test case for SDK unit tests. Contains a number of general utility methods
 * to help writing test cases, such as looking up a temporary directory, comparing golden
 * files, computing string diffs, etc.
 */
@SuppressWarnings("javadoc")
public abstract class SdkTestCase extends TestCase {
    private static File sTempDir = null;
    protected static Set<File> sCleanDirs = Sets.newHashSet();

    protected abstract String getTestDataRelPath();

    /**
     * Get the location to write missing golden files to
     */
    protected File getTargetDir() {
        // Set $ADT_SDK_SOURCE_PATH to point to your git "sdk" directory; if done, then
        // if you run a unit test which refers to a golden file which does not exist, it
        // will be created directly into the test data directory and you can rerun the
        // test
        // and it should pass (after you verify that the golden file contains the correct
        // result of course).
        String sdk = System.getenv("ADT_SDK_SOURCE_PATH");
        if (sdk != null) {
            File sdkPath = new File(sdk);
            if (sdkPath.exists()) {
                File testData = new File(sdkPath, getTestDataRelPath().replace('/',
                                                                               File.separatorChar));
                if (testData.exists()) {
                    addCleanupDir(testData);
                    return testData;
                }
            }
        }
        return getTempDir();
    }

    public static File getTempDir() {
        if (sTempDir == null) {
            File base = new File(System.getProperty("java.io.tmpdir"));     //$NON-NLS-1$
            if (SdkConstants.CURRENT_PLATFORM == SdkConstants.PLATFORM_DARWIN) {
                base = new File("/tmp"); //$NON-NLS-1$
            }
            // On Windows, we don't want to pollute the temp folder (which is generally
            // already incredibly busy). So let's create a temp folder for the results.
            Calendar c = Calendar.getInstance();
            String name = String.format("sdkTests_%1$tF_%1$tT", c).replace(':', '-'); //$NON-NLS-1$
            File tmpDir = new File(base, name);
            if (!tmpDir.exists() && tmpDir.mkdir()) {
                sTempDir = tmpDir;
            } else {
                sTempDir = base;
            }
            addCleanupDir(sTempDir);
        }
        return sTempDir;
    }

    protected InputStream getTestResource(String relativePath, boolean expectExists) {
        String path = "testdata" + File.separator + relativePath; //$NON-NLS-1$
        InputStream stream = SdkTestCase.class.getResourceAsStream(path);
        if (!expectExists && stream == null) {
            return null;
        }
        return stream;
    }

    protected void deleteFile(File dir) {
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                deleteFile(f);
            }
        } else if (dir.isFile()) {
            assertTrue(dir.getPath(), dir.delete());
        }
    }

    protected File makeTestFile(String name, String relative,
                                final InputStream contents) throws IOException {
        return makeTestFile(getTargetDir(), name, relative, contents);
    }

    protected File makeTestFile(File dir, String name, String relative,
                                final InputStream contents) throws IOException {
        if (relative != null) {
            dir = new File(dir, relative);
            if (!dir.exists()) {
                boolean mkdir = dir.mkdirs();
                assertTrue(dir.getPath(), mkdir);
            }
        } else if (!dir.exists()) {
            boolean mkdir = dir.mkdirs();
            assertTrue(dir.getPath(), mkdir);
        }
        File tempFile = new File(dir, name);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        Files.copy(new InputSupplier<InputStream>() {
            @Override
            public InputStream getInput() throws IOException {
                return contents;
            }
        }, tempFile);
        return tempFile;
    }

    protected File getTestfile(File targetDir, String relativePath) throws IOException {
        // Support replacing filenames and paths with a => syntax, e.g.
        //   dir/file.txt=>dir2/dir3/file2.java
        // will read dir/file.txt from the test data and write it into the target
        // directory as dir2/dir3/file2.java
        String targetPath = relativePath;
        int replaceIndex = relativePath.indexOf("=>"); //$NON-NLS-1$
        if (replaceIndex != -1) {
            // foo=>bar
            targetPath = relativePath.substring(replaceIndex + "=>".length());
            relativePath = relativePath.substring(0, replaceIndex);
        }
        InputStream stream = getTestResource(relativePath, true);
        assertNotNull(relativePath + " does not exist", stream);
        int index = targetPath.lastIndexOf('/');
        String relative = null;
        String name = targetPath;
        if (index != -1) {
            name = targetPath.substring(index + 1);
            relative = targetPath.substring(0, index);
        }
        return makeTestFile(targetDir, name, relative, stream);
    }

    protected static void addCleanupDir(File dir) {
        sCleanDirs.add(dir);
        try {
            sCleanDirs.add(dir.getCanonicalFile());
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        }
        sCleanDirs.add(dir.getAbsoluteFile());
    }

    protected String cleanup(String result) {
        List<File> sorted = new ArrayList<File>(sCleanDirs);
        // Process dirs in order such that we match longest substrings first
        Collections.sort(sorted, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                String path1 = file1.getPath();
                String path2 = file2.getPath();
                int delta = path2.length() - path1.length();
                if (delta != 0) {
                    return delta;
                } else {
                    return path1.compareTo(path2);
                }
            }
        });
        for (File dir : sorted) {
            if (result.contains(dir.getPath())) {
                result = result.replace(dir.getPath(), "/TESTROOT");
            }
        }
        // The output typically contains a few directory/filenames.
        // On Windows we need to change the separators to the unix-style
        // forward slash to make the test as OS-agnostic as possible.
        if (File.separatorChar != '/') {
            result = result.replace(File.separatorChar, '/');
        }
        return result;
    }
}
