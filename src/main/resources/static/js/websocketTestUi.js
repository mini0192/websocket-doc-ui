let stompClient = null;

function setConnected(connected) {
    document.getElementById("connectBtn").disabled = connected;
    document.getElementById("disconnectBtn").disabled = !connected;
    document.getElementById("sendBtn").disabled = !connected;
    document.getElementById("subscribe").disabled = !connected;
    document.getElementById("unsubscribe").disabled = !connected;
}

function log(message, status = 'info') {
    const logContainer = document.getElementById("messages");
    const div = document.createElement("div");
    const now = new Date();
    const timeString = now.toLocaleTimeString(); // HH:mm:ss
    div.textContent = `[${timeString}] ${message}`;
    div.classList.add('log-' + status);
    logContainer.appendChild(div);
    logContainer.scrollTop = logContainer.scrollHeight; // Auto-scroll to bottom
}

function connect() {
    const endpoint = document.getElementById("endpoint").value;
    const socket = new SockJS(endpoint);

    stompClient = Stomp.over(socket);

    stompClient.connect({}, (frame) => {
        setConnected(true);
        log(`Connection EndPoint: ${endpoint}`, 'success');
    }, (error) => {
        log(`Connection error: ${error}`, 'failure');
        log(`EndPoint: ${endpoint}`, 'failure')
        setConnected(false);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect(() => {
            setConnected(false);
            log("Disconnected", 'failure');
        });
    } else {
        setConnected(false);
    }
}

let subscriptions = []
function subscribe() {
    const topic = document.getElementById("subscribe-topic").value;

    const alreadySubscribed = subscriptions.some(sub => sub.topic === topic);
    if (alreadySubscribed) {
        log(`Already subscribed to: ${topic}`, 'failure');
        return;
    }

    const subscription = stompClient.subscribe(topic, (greeting) => {
        log(`Message received from: ${topic}`, 'info');
        let payload;
        try {
            payload = JSON.parse(greeting.body);
            showGreeting(payload);
        } catch (e) {
            log(`Error parsing message: ${e.message}`, 'failure');
            log("Raw message: " + greeting.body, 'info');
        }
    });

    subscriptions.push({ topic, subscription });
    log(`Subscribed to: ${topic}`, 'success');
}

function unsubscribe() {
    const topic = document.getElementById("subscribe-topic").value;
    const index = subscriptions.findIndex(sub => sub.id === topic);
    if (index !== -1) {
        subscriptions[index].unsubscribe();
        subscriptions.splice(index, 1);
        log(`Unsubscribed from: ${topic}`, 'info');
    } else {
        log(`No subscription found for topic: ${topic}`, 'failure');
    }
}

function send() {
    const app = document.getElementById("send-app").value;
    const content = document.getElementById("name").value;
    stompClient.send(app, {}, content);
    log(`Send To: ${app}`, 'success');
}

function showGreeting(message) {
    log(`Received: ${JSON.stringify(message)}`, 'info');
}

window.addEventListener("DOMContentLoaded", () => {
    document.getElementById("connectBtn").addEventListener("click", connect);
    document.getElementById("disconnectBtn").addEventListener("click", disconnect);
    document.getElementById("sendBtn").addEventListener("click", send);
    document.getElementById("subscribe").addEventListener("click", subscribe);
    document.getElementById("unsubscribe").addEventListener("click", unsubscribe);

    const forms = document.querySelectorAll("form");
    forms.forEach(form => form.addEventListener("submit", e => e.preventDefault()));
});
