/**
 * Controls the tab UI.
 * @param {string} tabName - The ID of the tab to activate (e.g., 'documentation', 'test-client').
 */
function openTab(tabName) {
    // Hide all tab content.
    const tabContents = document.querySelectorAll('.tab-content');
    tabContents.forEach(content => {
        content.classList.remove('active');
    });

    // Deactivate all tab buttons.
    const tabButtons = document.querySelectorAll('.tab-button');
    tabButtons.forEach(button => {
        button.classList.remove('active');
    });

    // Activate the selected tab and button.
    document.getElementById(tabName).classList.add('active');
    event.currentTarget.classList.add('active');
}

// Run the script after the DOM is fully loaded.
document.addEventListener('DOMContentLoaded', () => {

    // --- Documentation Tab Scripts ---

    /**
     * Adds a click event to all 'topic-copy-button' elements.
     * Copies the topic text to the clipboard when the button is clicked.
     */
    document.querySelectorAll('.topic-copy-button').forEach(button => {
        button.addEventListener('click', async (event) => {
            const topicTextElement = event.currentTarget.closest('.topic').querySelector('.topic-text');
            if (topicTextElement) {
                const topicText = topicTextElement.textContent;
                try {
                    await navigator.clipboard.writeText(topicText);
                    const originalText = button.textContent;
                    button.textContent = 'Copied!'; // Show a success message.
                    setTimeout(() => {
                        button.textContent = originalText;
                    }, 2000); // Revert to the original text after 2 seconds.
                } catch (err) {
                    console.error('Failed to copy topic: ', err);
                }
            }
        });
    });

    /**
     * Adds a click event to all 'copy-button' elements.
     * Copies the payload (JSON) to the clipboard when the button is clicked.
     */
    document.querySelectorAll('.copy-button').forEach(button => {
        button.addEventListener('click', async (event) => {
            const preElement = event.currentTarget.closest('.payload-container').querySelector('.payload-content');
            if (preElement) {
                const payload = preElement.textContent;
                try {
                    await navigator.clipboard.writeText(payload);
                    const originalText = button.textContent;
                    button.textContent = 'Copied!'; // Show a success message.
                    setTimeout(() => {
                        button.textContent = originalText;
                    }, 2000); // Revert to the original text after 2 seconds.
                } catch (err) {
                    console.error('Failed to copy: ', err);
                }
            }
        });
    });

    // --- Test Client Tab Scripts ---

    // Get the UI elements for the WebSocket test client.
    const endpointInput = document.getElementById('endpoint');
    const connectBtn = document.getElementById('connectBtn');
    const disconnectBtn = document.getElementById('disconnectBtn');
    const sendBtn = document.getElementById('sendBtn');
    const messageInput = document.getElementById('messageInput');
    const messagesDiv = document.getElementById('messages');

    let stompClient; // Variable to store the Stomp client instance.

    /**
     * Logs a message to the screen.
     * @param {string} message - The message to display.
     * @param {string} type - The message type ('info', 'sent', 'received', 'error').
     */
    function log(message, type = 'info') {
        const p = document.createElement('p');
        p.className = `message ${type}`;
        p.textContent = `[${new Date().toLocaleTimeString()}] ${message}`;
        messagesDiv.appendChild(p);
        messagesDiv.scrollTop = messagesDiv.scrollHeight; // Scroll to the bottom to show the latest message.
    }

    // Start the WebSocket connection when the 'Connect' button is clicked.
    connectBtn.addEventListener('click', () => {
        const endpoint = endpointInput.value;
        if (!endpoint) {
            alert('Please enter a WebSocket endpoint.');
            return;
        }

        const socket = new SockJS(endpoint);
        stompClient = Stomp.over(socket);

        socket.onclose = () => {
            log('Connection closed.', 'info');
            connectBtn.disabled = false;
            disconnectBtn.disabled = true;
            sendBtn.disabled = true;
        };

        stompClient.connect({},
            (frame) => {
                log("Connected: ", frame, "received");
                connectBtn.disabled = true;
                disconnectBtn.disabled = false;
                sendBtn.disabled = false;
            },
            (error) => {
                log("Connection error: ", error, "error");
            }
        )
    });

    // Close the WebSocket connection when the 'Disconnect' button is clicked.
    disconnectBtn.addEventListener('click', () => {
        if (stompClient) {
            stompClient.disconnect(() => {
                log('Disconnected.', 'info');
                connectBtn.disabled = false;
                disconnectBtn.disabled = true;
                sendBtn.disabled = true;
            });
        }
    });

    // Send a message to the server when the 'Send' button is clicked.
    sendBtn.addEventListener('click', () => {
        const message = messageInput.value;
        if (message && stompClient && stompClient.connected) {
            // Example of sending a message to a specific destination.
            // You might need to change the destination depending on your server-side implementation.
            stompClient.send("/app/chat", {}, JSON.stringify({'content': message}));
            log(`Sent: ${message}`, 'sent');
            messageInput.value = ''; // Clear the message input field.
        }
    });
});
