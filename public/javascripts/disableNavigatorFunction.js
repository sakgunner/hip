  document.onkeydown = function() {
        switch(event.keyCode) {
            case 116 :   event.returnValue = false;
                         event.keyCode = 0;
                         return false;

            case 82 :    if(event.ctrlKey){
                            event.returnValue = false;
                            event.keyCode = 0;
                            return false;
                         }
               }
    }// end disable refresh by F5 and ctrl+R
    var tenth = ''; 
 
    function ninth() { 
        if (document.all) { 
            (tenth); 
            return false; 
        } 
    } 
 
    function twelfth(e) { 
        if (document.layers || (document.getElementById && !document.all)) { 
            if (e.which == 2 || e.which == 3) { 
                (tenth); 
                return false; 
            } 
        } 
    } 
    if (document.layers) { 
        document.captureEvents(Event.MOUSEDOWN); 
        document.onmousedown = twelfth; 
    } else { 
        document.onmouseup = twelfth; 
        document.oncontextmenu = ninth; 
    } 
    document.oncontextmenu = new Function(' return false') 

