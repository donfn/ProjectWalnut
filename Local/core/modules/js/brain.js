const lights = require("../../iot/lights")
lights.connect()
var risk = [0]
var armed = true
var triggered = false

module.exports = {
    arm: () => {
        require("./core/modules/js/moduleInitialize")
        lights.connect()
        
        module.exports.startCountdown(60)
            .then(()=>{
                armed = true
            })
    },
    feed: (data) => {
        if(!armed) return

        if(data.dataType == "camera"){
            let factor = data.movement*0.9 + data.faces * 100
            if(factor > 80){
                if(risk >= 100) return
                risk += risk**0.5/6
            }else{
                if(risk <= 10) return
                risk -= Math.log(risk)*1.4
            }
        }
        module.exports.decide(data.motion)
    },
    decide: () => {
        if(!armed) return

        console.log(risk)
        if(risk > 80){
            lights.on()

        }else{
            lights.off()
        }

    },

    startCountdown: (time)=>{
        if(triggered == true) return

        triggered = true
        return new Promise((done)=>{
            setTimeout(done(),time*100)
        })
    }
}
