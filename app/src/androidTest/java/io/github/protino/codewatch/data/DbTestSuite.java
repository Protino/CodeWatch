package io.github.protino.codewatch.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Gurupad Mamadapur
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({TestDb.class, TestLeaderContract.class, TestProvider.class, TestUriMatcher.class, TestUtilities.class})
public class DbTestSuite {
}
