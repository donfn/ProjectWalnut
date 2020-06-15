const axios = require("axios").create({
    httpsAgent: new require('https').Agent({  
      rejectUnauthorized: false
    })
  })

var cloudConfig
var bridge = {}

const testConnection= () => {
    return new Promise((pass, fail)=>{
        if(!bridge.token || !bridge.address) fail()
        axios
            .get(`http://${bridge.address}/api/${bridge.token}`)
            .then(res=>{
                bridge.lights = res.data.lights
                pass()
            })
            .catch(e=>fail())
    })
}

const setState= (state, percentage = 100, lights = "all") =>{
    testConnection().then(()=>{
        return new Promise((complete, failed)=>{
        if(lights == "all") lights = Object.keys(bridge.lights)
        // if(typeof lights !== Array) lights = [lights]

        // console.log(lights)
        percentage = Math.floor(percentage*254/100)
        console.log(percentage)
        lights.map(light=>{
            if(typeof light == String) for(_light in bridge.lights) if(bridge.lights[_light]['name'] == light) light == _light
            else light = parseInt(light)
            axios.put(`http://${bridge.address}/api/${bridge.token}/lights/${light}/state`,{
                on: state,
                bri: percentage,
                transitiontime: 0
            })
            .then(complete())
            .catch(e=>failed())
        })
        })
    })
}

module.exports = (config) => {
    cloudConfig = config
    return {
        connect: ()=>{
            return new Promise(async (connected, failed)=>{
                console.info("ℹ️ Searching for a Hue Bridge")
                await cloudConfig.get().then(remote=>{
                    console.log("☁️ Downloaded Hue Bridge credentials from the cloud")
                    bridge = remote.data()["hueBridge"]
                })

                testConnection()
                    .then(()=>{
                        console.log("✅ Connected to Hue Bridge!")
                        connected()
                    })
                    .catch(e=>{
                        axios
                            .get("https://discovery.meethue.com")
                            .then(discovery=>{
                                bridge.address = discovery.data[0].internalipaddress
                                console.info(`ℹ️  Found Hue Bridge at ${bridge.address}. Trying to connect.`)
                                console.warn("📥 Please press the Hue Bridge button.")
                                let interval = setInterval(()=>{
                                        axios
                                            .post(`http://${bridge.address}/api`, {
                                                devicetype: "ProjectWalnut#LocalServer"
                                            })
                                            .then(auth=>{
                                                if(auth.data[0].success){
                                                    bridge.token = auth.data[0].success.username
                                                    console.log("✅ Connected to Hue Bridge!");
                                                    cloudConfig.update({
                                                        hueBridge: {
                                                            token: bridge.token,
                                                            address: bridge.address
                                                        }
                                                    })
                                                    clearInterval(interval)
                                                    connected()
                                                }
                                            })
                                            .catch()
            
                                },2000)
                            })
                            .catch(e=>{
                                console.error("😪 No Hue bridges were found!")
                                failed()
                            })
                    })
            })
        },
        on: (percentage, lights) => setState(true,percentage,lights),
        off: (lights) => setState(false,0,lights)
    }
}