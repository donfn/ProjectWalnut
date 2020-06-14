const {PythonShell} = require("python-shell");
 
let options = {
  mode: 'text',
  pythonOptions: ['-u'], // get print results in real-time
  scriptPath: './core/modules/python',
  args: [new Buffer.from(process.env.publicKey).toString('base64')]
};
 
PythonShell.run('/motionAndFaceDetection/main.py', options, (info,err)=>{
    console.log(info,err)
});