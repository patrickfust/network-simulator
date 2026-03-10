package dk.fust.networksimulator.util

import spock.lang.Specification
import spock.lang.Unroll

class GlobMatcherSpec extends Specification {

    @Unroll
    def "matches('#glob', '#path') == #expected"() {
        expect:
        GlobMatcher.matches(glob, path) == expected

        where:
        glob                    | path                        | expected
        // null / empty glob matches everything
        null                    | '/api/users'                | true
        ''                      | '/api/users'                | true

        // exact match
        '/api/users'            | '/api/users'                | true
        '/api/users'            | '/api/orders'               | false

        // * wildcard
        '/api/*'                | '/api/users'                | true
        '/api/*'                | '/api/orders'               | true
        '/api/*/details'        | '/api/users/details'        | true
        '/api/*/details'        | '/api/users'                | false

        // * matches across segments
        '/api/*'                | '/api/users/123'            | true

        // ? wildcard
        '/api/user?'            | '/api/users'                | true
        '/api/user?'            | '/api/user'                 | false
        '/api/user?'            | '/api/userss'               | false

        // regex metacharacters in pattern treated as literals (not as regex)
        '/api/v1.0/users'       | '/api/v1.0/users'           | true
        '/api/v1.0/users'       | '/api/v100/users'           | false  // dot is literal, not "any char"
        '/api/(test)'           | '/api/(test)'               | true
        '/api/(test)'           | '/api/Xtest)'               | false
        '/api/users+'           | '/api/users+'               | true
        '/api/users+'           | '/api/userssss'             | false
    }

    def "globToRegex converts wildcards and escapes metacharacters"() {
        expect:
        GlobMatcher.globToRegex('/api/*.json') == '^/api/.*\\.json$'
        GlobMatcher.globToRegex('/a(b)c')      == '^/a\\(b\\)c$'
        GlobMatcher.globToRegex('/a+b')        == '^/a\\+b$'
    }

}
