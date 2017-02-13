/** Send a notice to IRC with the current build result
 *
 * @param server server to connect to
 * @param nick nickname to use
 * @param channel channel to join
*/
def call(String server = 'irc.mozilla.org:6697',
         String nick = 'fxtest',
         String channel = '#fx-test-alerts') {
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
