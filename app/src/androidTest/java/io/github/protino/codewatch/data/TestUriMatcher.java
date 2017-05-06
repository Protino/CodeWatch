/*
 * Copyright 2017 Gurupad Mamadapur
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.protino.codewatch.data;


import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {

    private static final Uri TEST_LEADER_DIR = LeaderContract.LeaderEntry.CONTENT_URI;
    private static final Uri TEST_PROFILE_ITEM = LeaderContract.LeaderEntry.buildProfileUri(TestUtilities.TEST_USER_ID);

    public void testUriMatcher() {
        UriMatcher testMatcher = LeaderProvider.buildUriMatcher();

        assertEquals("Error: The LEADER URI was matched incorrectly.",
                testMatcher.match(TEST_LEADER_DIR), LeaderProvider.LEADER);
        assertEquals("Error: The PROFILE URI was matched incorrectly",
                testMatcher.match(TEST_PROFILE_ITEM), LeaderProvider.PROFILE);
    }
}
