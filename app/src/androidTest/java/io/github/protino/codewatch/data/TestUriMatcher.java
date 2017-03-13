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
