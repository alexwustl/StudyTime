const fetch = require('node-fetch');

exports.handler = (event, context, callback) => {
    console.log(event);
    if(event.httpMethod==='GET'){
        callback(null, {
            statusCode: 302,
            headers: {Location: 'https://quizlet.com/authorize?client_id=uBdNMkEvyq&response_type=code&state='+event.queryStringParameters.state+'&scope=read&redirect_uri=https://i5x4b8qsbk.execute-api.us-east-1.amazonaws.com/Testing/QuizletToAlexa'}
        });
    } else {
        const input = event.body;
        const array = input.split("&");
        let code = "";
        for(const item of array){
            if(item.substring(0,4)==='code'){
                code=item.substring(5);
            }
        }
        console.log(code);
        console.log(JSON.stringify({grant_type:'authorization_code',code:code,redirect_uri:'https://i5x4b8qsbk.execute-api.us-east-1.amazonaws.com/Testing/QuizletToAlexa',client_id:'uBdNMkEvyq'}));
        console.log('grant_type=authorization_code&code='+code+'&redirect_uri=https://pitangui.amazon.com/api/skill/link/M30V14UQYJSEB6&client_id=uBdNMkEvyq');
        fetch('https://api.quizlet.com/oauth/token',{
            headers:{
                Authorization: 'Basic dUJkTk1rRXZ5cTpQTTUzOUFEVEJTM1NRSlpqUFk1YTdS',
				"content-type": "application/x-www-form-urlencoded"	
            },
           body:  'grant_type=authorization_code&code='+code+'&redirect_uri=https://i5x4b8qsbk.execute-api.us-east-1.amazonaws.com/Testing/QuizletToAlexa',
           method: 'POST'
        }).then((r)=>{
            return r.json();
        }).then((r)=>{
            console.log(r);
            r.access_token=r.access_token+"&"+r.user_id;
            callback(null, {
            statusCode: 200,
            headers: {
                "Access-Control-Allow-Origin": "*" // Required for CORS support to work
            },
            body: JSON.stringify(r)
        });
        });
        
        
    }
};