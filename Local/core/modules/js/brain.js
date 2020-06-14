const lights = require("../../iot/lights")
lights.connect()
var risk = 1
var armed = false
var triggered = false
var lightSwitch = false
var io

module.exports = {
    arm: () => {
        require("../../modules/js/moduleInitialize")
        lights.off()
        // lights.on(3, [2])
        module.exports.startCountdown(5)
            .then(()=>{
                console.log("Armed!")
                armed = true
            })
    },
    feed: (data) => {
        if(!armed) return

        if(data.dataType == "camera"){
            let factor = data.movement + data.faces*0.2
            if(factor > 70){
                risk += factor**0.5

                if(risk >= 100) risk = 100

                if(!lightSwitch){
                    lights.on(30)
                    lightSwitch = true
                    
                    armed = false
                    module.exports.startCountdown(2.5)
                    .then(()=>{
                        armed = true
                    })
                }
            }else{
                risk -= Math.log(risk) * 0.5
                if(risk <= 10) risk = 10

                if(lightSwitch){
                    lightSwitch = false
                    armed = false
                    module.exports.startCountdown(3.5)                    
                        .then(()=>{
                            armed = true
                        })

                    lights.off()
                    // lights.on(10, [2])

                    // module.exports.startCountdown(120)                    
                    //     .then(()=>{
                    //         armed=false
                    //         lights.off()
                    //         module.exports.startCountdown(3.5)                    
                    //             .then(()=>{
                    //                 armed = true
                    //             })

                    //     })
                }
            }
        }

        console.log(risk)
        module.exports.decide(data.motion)

        return risk
    },
    decide: () => {
        if(!armed) return
        if(risk > 70){
            console.log("INTRUSION!")
        }
    },

    startCountdown: (time)=>{
        // if(triggered == true) return

        // triggered = true
        return new Promise((resolve,reject)=>{
            setTimeout(function(){
                triggered = false
                resolve()
            },time*1000)
        })
    },
    socket: (socket)=>{
        io = socket
    }
}
