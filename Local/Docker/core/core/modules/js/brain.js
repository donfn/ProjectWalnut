var risk = 1
var armed = false
var triggered = false
var lightSwitch = false
var io
var cloudConfig

const startCountdown = (time)=>{
    // if(triggered == true) return

    // triggered = true
    return new Promise((resolve,reject)=>{
        setTimeout(function(){
            triggered = false
            resolve()
        },time*1000)
    })
}

const decide = () => {
    if(!armed) return
    if(risk > 70){
        console.log("INTRUSION!")
    }
}

module.exports = (config) => {
    cloudConfig = config

    const lights = require("../../iot/lights")(cloudConfig)
    lights.connect()

    return {
        socket: (socket)=>{
            io = socket
        },
        arm: () => {
            lights.off()
            // lights.on(3, [2])
            startCountdown(5)
                .then(()=>{
                    console.log("ℹ️ Security system armed")
                    armed = true
                })
        },
        feed: (data) => {
            if(!armed) return

            if(data.dataType == "camera"){
                let factor = data.movement
                if(factor > 70){
                    risk += factor**0.5

                    if(risk >= 100) risk = 100

                    if(!lightSwitch){
                        lights.on(100,[1,3,4])
                        lightSwitch = true
                        
                        armed = false
                        startCountdown(2.5)
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
                        startCountdown(3.5)                    
                            .then(()=>{
                                armed = true
                            })

                        lights.off()
                        // lights.on(10, [2])

                        // startCountdown(120)                    
                        //     .then(()=>{
                        //         armed=false
                        //         lights.off()
                        //         startCountdown(3.5)                    
                        //             .then(()=>{
                        //                 armed = true
                        //             })

                        //     })
                    }
                }
            }

            console.log(risk)
            decide(data.motion)
        }
    }
}
