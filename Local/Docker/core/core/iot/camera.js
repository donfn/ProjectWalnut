const cv = require('opencv4nodejs');
const webcam = new cv.VideoCapture(0)

setInterval(()=>{
    const image = webcam.read();
    const classifier = new cv.CascadeClassifier(cv.HAAR_FRONTALFACE_ALT2);

    // detect faces
    const { objects, numDetections } = classifier.detectMultiScale(image.bgrToGray());
    console.log('faceRects:', objects);
    console.log('confidences:', numDetections);

    if (!objects.length) console.log('No faces detected!');  
},1000)

