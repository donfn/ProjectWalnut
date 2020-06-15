
module.exports = (brain) =>{
    var app = require('express')();
    var http = require('http').createServer(app);
    var io = require('socket.io')(http);

    io.on("connection", client => client.on("data",  data => { 
        let risk = brain.feed(data)
        io.emit("risk", risk)
    }))

    http.listen(process.env.APIPort, () => {
        console.log(`ℹ️ Started the dataAPI server at port ${process.env.APIPort}`);
        // brain.socket(io)
    })
}
