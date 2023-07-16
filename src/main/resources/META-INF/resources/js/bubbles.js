document.querySelector('#filterIcon').addEventListener('click', function(event) {
    var filterIcon = event.target;
    var filterOverlay = document.querySelector('.overlay');

    // Position des Filter-Icons abrufen
    var iconRect = filterIcon.getBoundingClientRect();
    var iconTop = iconRect.top + iconRect.height;
    var iconRight = window.innerWidth - iconRect.right;

    // Position des Overlays festlegen
    filterOverlay.style.top = iconTop + 'px';
    filterOverlay.style.right = iconRight + 'px';
    filterOverlay.style.display = 'block';

    // Klick außerhalb der Bubble abfangen
    event.stopPropagation(); // Bubble-Events stoppen
    document.addEventListener('click', closeFilterBubble);
});

function closeFilterBubble(event) {
    var filterOverlay = document.querySelector('.overlay');

    // Überprüfen, ob Klick innerhalb der Bubble erfolgt ist
    if (!filterOverlay.contains(event.target)) {
        filterOverlay.style.display = 'none';
        document.removeEventListener('click', closeFilterBubble);
    }
}

document.querySelector('#searchIcon').addEventListener('click', function(event) {
    var searchIcon = event.target;
    var searchOverlay = document.querySelector('.overlay');

    // Position des Search-Icons abrufen
    var iconRect = searchIcon.getBoundingClientRect();
    var iconTop = iconRect.top + iconRect.height;
    var iconLeft = iconRect.left;

    // Position des Overlays festlegen
    searchOverlay.style.top = iconTop + 'px';
    searchOverlay.style.left = iconLeft + 'px';
    searchOverlay.style.display = 'block';

    // Klick außerhalb der Bubble abfangen
    event.stopPropagation(); // Bubble-Events stoppen
    document.addEventListener('click', closeSearchBubble);
});

function closeSearchBubble(event) {
    var searchOverlay = document.querySelector('.searchOverlay');

    // Überprüfen, ob Klick innerhalb der Bubble erfolgt ist
    if (!searchOverlay.contains(event.target)) {
        searchOverlay.style.display = 'none';
        document.removeEventListener('click', closeSearchBubble);
    }
}
