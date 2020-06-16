var risk = 1
var halted = false
var triggered = false
var lightSwitch = false
var io
var cloudConfig
var armed = false
var halted = true
var lights

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
    if(halted) return
    if(risk > 70){
        console.log("INTRUSION!")
    }
}

const watchForUpdate = () =>{
    cloudConfig.onSnapshot(update=>{
        update = update.data()
        if(!armed){
            if(update.armed) arm()
        }else{
            if(!update.armed) disarm()
        }
    })
}

const arm = async (seconds = 0)=>{
    lights.off()
    // lights.on(3, [2])
    startCountdown(seconds)
        .then(()=>{
            console.log("ℹ️ Security system armed")
            armed = true
            halted = false
        })
}

const disarm = async () => {
    console.log("ℹ️ Security system disarmed")
    armed = false
    halted = true
}

module.exports = (config) => {
    cloudConfig = config
    lights = require("../../iot/lights")(cloudConfig)


    lights.connect().then(()=>{
        watchForUpdate()
    })

    return {
        socket: (socket)=>{
            io = socket
        },
        arm: arm,
        disarm: disarm,
        feed: (data) => {
            if(halted) return

            if(data.dataType == "camera"){
                let factor = data.movement
                if(factor > 70){
                    risk += factor**0.5

                    if(risk >= 100) risk = 100

                    if(!lightSwitch){
                        lights.on(100,[1,3,4])
                        lightSwitch = true
                        
                        halted = false
                        startCountdown(2.5)
                        .then(()=>{
                            halted = true
                        })
                    }
                }else{
                    risk -= Math.log(risk) * 0.5
                    if(risk <= 10) risk = 10

                    if(lightSwitch){
                        lightSwitch = false
                        halted = false
                        startCountdown(3.5)                    
                            .then(()=>{
                                halted = true
                            })

                        lights.off()
                        // lights.on(10, [2])

                        // startCountdown(120)                    
                        //     .then(()=>{
                        //         halted=false
                        //         lights.off()
                        //         startCountdown(3.5)                    
                        //             .then(()=>{
                        //                 halted = true
                        //             })

                        //     })
                    }
                }
            }
            decide()
        }
    }
}
