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

import android.net.Uri;
import android.test.AndroidTestCase;


public class TestLeaderContract extends AndroidTestCase {

    private static final String TEST_USER_ID = "/23g23fv23h4";

    public void testBuildLeaderLocation() {
        Uri profileUri = LeaderContract.LeaderEntry.buildProfileUri(TEST_USER_ID);
        assertNotNull("Error: Null Uri returned." + "LeaderContract.", profileUri);
        assertEquals("Error: user id not properly appended to the end of the Uri",
                TEST_USER_ID, profileUri.getLastPathSegment());
        assertEquals("Error: Mismatch",
                profileUri.toString(),
                "content://" + LeaderContract.CONTENT_AUTHORITY + "/leader/%2F23g23fv23h4");
    }
}
