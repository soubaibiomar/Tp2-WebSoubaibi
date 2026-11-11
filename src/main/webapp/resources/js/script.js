
function defilerConversation() {
    const zone = document.getElementById("conversation");

    if (zone) {
        zone.scrollTop = zone.scrollHeight;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    defilerConversation();
});
