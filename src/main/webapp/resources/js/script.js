
function defilerConversation() {
    const zone = document.querySelector(".conversation");

    if (zone) {
        zone.scrollTop = zone.scrollHeight;
    }
}

document.addEventListener("DOMContentLoaded", defilerConversation);
