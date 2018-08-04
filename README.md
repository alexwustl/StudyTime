This is the repository for my released Alexa skill, Study Time. It aims to allow use of Quizlet through Alexa. It can be found [here](https://www.amazon.com/Alex-Baker-Study-Time/dp/B07G2G6K6V/ref=sr_1_2?s=digital-skills&ie=UTF8&qid=1533352655&sr=1-2&keywords=study+time).
# Main code
- The main code is written in Java for AWS Lambda.
# Proxy details
- Due to problems with Quizlet and Alexa OAuth linking, you need both the username and auth token from quizlet to be able to get a user's data, but Alexa only passes the auth token to me. Therefore I intercepted to process using Javascript AWS Lambda functions, to make Alexa think that a string containing both the username and OAuth token was the token itself, bypassing this problem.
# Credits
- Credit to help for the HttpProxy to https://developer.amazon.com/blogs/post/TxQN2C04S97C0J/how-to-set-up-amazon-api-gateway-as-a-proxy-to-debug-account-linking for helping me get started with a proxy
