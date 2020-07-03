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
            ticks.push(`${pos.x},${pos.y},${window.pageXOffset},${window.pageYOffset}`);
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

                var result = "";
                for (let tick of ticks)
                    result += tick + '<br>';
                
                document.write(result);
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
    window.scrollTo(0, 0);
    console.log("Capturing will start as soon as you move the mouse.");
})();