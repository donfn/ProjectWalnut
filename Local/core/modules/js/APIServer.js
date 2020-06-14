
module.exports = (brain) =>{
    var app = require('express')();
    var http = require('http').createServer(app);
    var io = require('socket.io')(http);
    const NodeRSA = require('node-rsa');
    const keys = new NodeRSA({b: 512});

    process.env.publicKey = keys.exportKey("pkcs1-public")

    io.on("connection", client => client.on("data",  data => { 
        let risk = brain.feed(data)
        io.emit("risk", risk)
    }))

    http.listen(process.env.APIPort, () => {
        console.log(`Started the dataAPI server at port ${process.env.APIPort}`);
        brain.socket(io)
    })
}
