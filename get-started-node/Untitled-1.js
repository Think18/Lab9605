/**
  *
  * main() will be run when you invoke this action
  *
  * @param Cloud Functions actions accept a single parameter, which must be a JSON object.
  *
  * @return The output of this action, which must be a JSON object.
  *
  */
var Q = require('q');
function main(params){
    var mainPromise = Q.defer();
    var name=params.docs.name;
    var pwd = params.docs.password;
    var Cloudant = require('cloudant');
    var cloudant = Cloudant("https://d5f5a173-9aa2-46e4-a578-73e3553b2abd-bluemix:e0bbe58bbdc63ffe351d2ddd4ee5b1c62afa5111d1708d92d8cfba64ec0f2c41@d5f5a173-9aa2-46e4-a578-73e3553b2abd-bluemix.cloudant.com");
    var bankbook = cloudant.db.use('bankbook');
     searchDocument(bankbook,name,pwd)
        .then(function(params){
            return authenticate(params,pwd);
        }).then(function(result){
            // console.log(JSON.stringify(result));
            mainPromise.resolve(result);
        }).catch(function(err){
            console.log("ERROR "+err);
            mainPromise.reject(err);
        });
    return mainPromise.promise;

}
    
    var searchDocument = function(bankbook,name,pwd){
        var searchQuery = {
              "selector":{
                  "name":{
                      "$eq":name
                  }
                }
        };
        console.log("Searching document having '$name' in name field");
        var defer = Q.defer();
        bankbook.find(searchQuery, function(err, data){
            console.log("Inside Find");
            if (!err) {
                    defer.resolve(data);
            } else {
                console.log(err);
                defer.reject(err);
              }
        });
        return defer.promise;
    }

    var authenticate =function(params,pwd){
            console.log("Authenticating User.....");
            if(params.docs[0].password == pwd){
                console.log("Successful Login");
                params = {docs:
                            {
                                _id: params.docs[0]._id, 
                                name: params.docs[0].name, 
                                actnumber: params.docs[0].actnumber, 
                                balance: params.docs[0].balance
                            }
                          };
                return params;
            }else{
                console.log("Incorrect Username or Password");
                return false;
            }
    }