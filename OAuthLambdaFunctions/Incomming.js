exports.handler = (event, context, callback) => {
    console.log(event);
    if(event.httpMethod==='GET'){
        callback(null, {
        statusCode: 302,
        headers: {Location: 'https://pitangui.amazon.com/api/skill/link/M30V14UQYJSEB6?code='+event.queryStringParameters.code+'&state='+event.queryStringParameters.state}
    });
    }
};