// To test, you can upload this file on your webserver and comment out the line below
// export { init };
// Then, you can run this file by executing the following in your console:
// import('http(s)://<domain></path>/mouseCapture.js').then(m => m.init(<height>, <width>));
// ex. import('http://www.eloki.tk/mouseCapture.js').then(m => m.init());

function iframeURLChange(iframe, callback) {
    var lastDispatched = null;

    var dispatchChange = function () {
        var newHref = null;
        if (iframe.contentWindow !== null)
            newHref = iframe.contentWindow.location.href;

        if (newHref !== lastDispatched) {
            callback(newHref);
            lastDispatched = newHref;
        }
    };

    var unloadHandler = function () {
        // Timeout needed because the URL changes immediately after
        // the `unload` event is dispatched.
        setTimeout(dispatchChange, 0);
    };

    function attachUnload() {
        // Remove the unloadHandler in case it was already attached.
        // Otherwise, there will be two handlers, which is unnecessary.
        iframe.contentWindow.removeEventListener("unload", unloadHandler);
        iframe.contentWindow.addEventListener("unload", unloadHandler);
    }

    iframe.addEventListener("load", function () {
        attachUnload();

        // Just in case the change wasn't dispatched during the unload event...
        dispatchChange();
    });

    attachUnload();
}

var ticks = [];

function init(ifHeight = 1200, ifWeight = 1920) {
    var html = document.querySelector('html');
    for (let element of html.children)
        if (element.tagName !== 'HEAD')
            html.removeChild(element);

    var body = document.createElement('body');
    body.style.height = '100vh';
    body.style.width = '100wh';
    body.style.padding = '0';
    body.style.margin = '0 auto';
    body.style.backgroundColor = 'white';
    body.style.textAlign = 'center';

    var div = document.createElement('div');
    div.style.display = 'block';

    var printButton = document.createElement('button');
    printButton.onclick = print_ticks;
    printButton.innerText = "Print Result";
    printButton.disabled = true;
    div.appendChild(printButton);
    body.appendChild(div);

    var ifrm = document.createElement('iframe');
    ifrm.setAttribute('src', window.location.origin);
    body.appendChild(ifrm);
    ifrm.style.height = ifHeight + 'px';
    ifrm.style.width = ifWeight + 'px';
    ifrm.style.padding = '0';
    ifrm.style.margin = '0';
    ifrm.style.border = 'none';
    html.appendChild(body);


    var capturing = false;
    var waiting = false;
    var mousePos;
    var theInterval;

    function toggleCapturing(ifrmDoc) {
        if (capturing) {
            body.style.backgroundColor = 'white';
            ifrmDoc.onmousemove = null;
            ifrmDoc.onclick = null;
            clearInterval(theInterval);
            printButton.disabled = false;
            // ticks.push({
            //     content: `stopped`,
            //     t: new Date()
            // });
        } else {
            body.style.backgroundColor = 'red';
            // ticks.push({
            //     content: `started`,
            //     t: new Date()
            // });
            theInterval = setInterval(() => {
                if (!!mousePos)
                    ticks.push(mousePos);
            }, 1);
            printButton.disabled = true;
        }
        capturing = !capturing;
    }



    window.scrollTo(0, 0);
    alert('Make sure the mouse focus is on the iframe and hit Ctrl to start and stop recording.');

    iframeURLChange(ifrm, newURL => {
        console.log("iFrame is on " + newURL);
        if (capturing) {
            toggleCapturing(ifrm.contentWindow.document);
            ticks.push({
                content: `waiting`,
                t: new Date()
            });
            waiting = true;
        }
    });

    ifrm.onload = () => {
        var ifrmDoc = ifrm.contentWindow.document;
        // ticks.push({
        //     content: ifrmDoc.readyState,
        //     t: new Date()
        // });

        if (waiting) {
            toggleCapturing(ifrmDoc);
            waiting = false;
        }

        ifrmDoc.onmousemove = event => {
            mousePos = {
                content: `${event.x},${event.y},${ifrm.contentWindow.pageXOffset},${ifrm.contentWindow.pageYOffset}`,
                t: new Date()
            };
        };

        ifrmDoc.onclick = event => {
            if (capturing) {
                ticks.push({
                    content: `click`,
                    t: new Date()
                });
            }
        };

        ifrmDoc.oncontextmenu = event => {
            if (capturing) {
                ticks.push({
                    content: `right_click`,
                    t: new Date()
                });
            }
        };

        ifrmDoc.body.onkeydown = event => {
            if (event.ctrlKey) {
                toggleCapturing(ifrmDoc);
            }
        }

    };

}

function print_ticks() {
    var body = document.querySelector('body');

    var result = "";
    ticks.sort((a, b) => {
        if (a.t < b.t)
            return -1;
        if (a.t > b.t)
            return 1;
        return 0;
    });
    for (let tick of ticks)
        result += tick.content + '\n';
    // result += tick.content + ' @ ' + tick.t.getMinutes() + ":" + tick.t.getSeconds() + ":" + tick.t.getMilliseconds() + '\n';

    body.style.fontFamily = 'monospace';
    body.style.color = 'black';
    body.innerText = result;
}
