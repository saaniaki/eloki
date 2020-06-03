(function() {
    var ticks = [];

    var capturing = false;
    var mousePos;

    function handleMouseMove(event) {
        mousePos = {
            x: event.clientX,
            y: event.clientY
        };
    }
    function getMousePosition() {
        var pos = mousePos;
        if (!pos) {
            // console.log("Capturing will start as soon as you move the mouse.");
        } else {
            ticks.push({
                mx: pos.x,
                my: pos.y,
                sx: window.pageXOffset,
                sy: window.pageYOffset
            });
            // console.log("mouse: " + pos.x + ", " + pos.y + " | " + "scroll: " + window.pageXOffset + ", " + window.pageYOffset);
        }
    }
    var theInterval;
    window.onkeydown = function(event) {
        if (event.ctrlKey) {
            if (capturing) {
                console.log("STOP");
                document.onmousemove = null;
                document.onclick = null;
                clearInterval(theInterval);
                document.write(JSON.stringify(ticks));
            } else {
                console.log("START");
                document.onmousemove = handleMouseMove;
                theInterval = setInterval(getMousePosition, 1);
                document.onclick = (cEvent) => {
                    cEvent.preventDefault();
                    ticks.push("click");
                }
            }
            capturing = !capturing;
        }
    }
})();

// http://chromedriver.storage.googleapis.com/index.html