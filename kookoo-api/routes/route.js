

var db = require('.././db/db')
var dal = require('.././db/dal')

exports.validateCall = function (request, response) {
    
    
    response.set('Content-Type', 'text/xml')
    console.log(request.query);
    
    if (request.query.event == 'NewCall') {
        
        var phnum = request.query.cid.substring(request.query.cid.length -10, request.query.cid.length)

        console.log("Got new call from " + phnum)       
        
        
        dal.GetItemByPhnum(phnum, function (result, id) {
            if (result.docs.length > 0) {
                var member = result.docs[0];
                response.write("<response><playtext>Welcome to Quick Conference, " + member.name + "</playtext><playtext>Connecting to your conference now.</playtext><conference caller_onhold_music=\"default\" record=\"true\">"+ member.confId + "</conference></response>");
            }
            else {
                response.write("<response><playtext>Sorry cannot validate your number</playtext></response>");
            }
            console.log(request.query.event);
            console.log('\n Sending backsasa... \n');
            response.end()
            
        }

        );
        
        console.log('Pin entered correctly');
        
       // response.write("<response><conference caller_onhold_music=\"default\" record=\"true\">2345</conference></response>");
    }

    else if (request.query.event == 'Hangup') {

    }

    else {
        response.write("<response><hangup></hangup></response>");
        console.log(request.query.event);
        console.log('\n Sending backsasa... \n');
        response.end()
    }

    //if (request.query.event == 'GotDTMF' && request.query.data == '1234') {
        
    //    console.log('Pin entered correctly');
        
    //    response.write("<response><conference caller_onhold_music=\"default\" record=\"true\">2345</conference></response>");
    //}
    //else {
    //    response.write("<response><collectdtmf l=\"4\" t=\"#\" o=\"5000\"><playtext>Please enter your pin number</playtext></collectdtmf></response>");
    //}
    
    
    //console.log(request.query.event);
    //console.log('\n Sending backsasa... \n');
    //response.end();
}

exports.register = function (request, response) {
    if (request.method == 'POST') {
        var body = '';
        
        request.on('data', function (data) {
            body += data;
            
            // Too much POST data, kill the connection!
            // 1e6 === 1 * Math.pow(10, 6) === 1 * 1000000 ~~~ 1MB
            if (body.length > 1e6)
                request.connection.destroy();
        });
        
        request.on('end', function () {
            var post = JSON.parse(body);
            
            
            
            dal.GetItemByMember(post, function (result, member) {
                
                if (result.docs.length > 0) {

                }
                else {
                    db.dbConnection.insert({
                        name : post.name,                
                        confId : post.confId,
                        phnum : post.phnum
                    }, post.phnum, function (err, doc) {
                        if (err) {
                            console.log(err);
                           // response.sendStatus(500);
                        } else { }
                          //  response.sendStatus(200);
                      
                    });
                }
            }
            );
            
            if (post.members.length > 0) {
                for (var i = 0; i < post.members.length ; i++) {
                    
                    var member = post.members[i];
                    
                    dal.GetItemByMember(member, function (result, member) {
                        
                        if (result.docs.length > 0) {

                        }
                        else {
                            db.dbConnection.insert({
                                name : member.name,                
                                confId : member.confId,
                                phnum : member.phnum
                            }, member.phnum, function (err, doc) {
                                if (err) {
                                    console.log(err);
                                  //  response.sendStatus(500);
                                } else { }
                                    //response.sendStatus(200);
                                
                            });
                        }
                    }
                    );

                };
                

            }
            response.sendStatus(200);
            response.end();

            //db.dbConnection.insert({
            //    name : post.name,                
            //    confId : post.confId,
            //    phnum : post._id
            //            },post._id, function (err, doc) {
            //    if (err) {
            //        console.log(err);
            //        response.sendStatus(500);
            //    } else
            //        response.sendStatus(200);
            //    response.end();
            //});

            // use post['blah'], etc.
        });
    }
};
