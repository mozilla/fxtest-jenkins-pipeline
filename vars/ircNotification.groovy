/** Send a notice to IRC with the current build result
 *
 * @param channel channel to join
 * @param nick nickname to use
 * @param server server to connect to
*/
def call(String channel = '#fx-test-alerts',
         String nick = 'fxtest',
         String server = 'irc.mozilla.org:6697') {
    nick = "${nick}-${BUILD_NUMBER}"
    result = currentBuild.result ?: 'SUCCESS'
    message = "Project ${JOB_NAME} build #${BUILD_NUMBER}: ${result}: ${BUILD_URL}"
    sh """
        (
        echo NICK ${nick}
        echo USER ${nick} 8 * : ${nick}
        sleep 5
        echo "JOIN ${channel}"
        echo "NOTICE ${channel} :${message}"
        echo QUIT
        ) | openssl s_client -connect ${server}
    """
}
